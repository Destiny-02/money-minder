package com.destiny.budgeting.server.api

import com.destiny.budgeting.server.db.MockData
import com.destiny.budgeting.server.model.*
import com.destiny.budgeting.server.model.ModelUtil.Companion.toLocalDate
import com.destiny.budgeting.server.service.GoogleSheetsService
import com.destiny.budgeting.server.util.SheetsUtil
import com.destiny.budgeting.server.util.TokenUtil
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.format.DateTimeParseException

@RestController
@CrossOrigin
@RequestMapping("/api/transactions")
class TransactionsController(private val googleSheetsService: GoogleSheetsService) {

    @GetMapping("")
    fun getTransactions(
        @RequestParam(required = true) sheetId: String,
        @RequestParam(required = false) startDate: String?,
        @RequestParam(required = false) endDate: String?,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<List<Transaction>?> {
        return try {
            if (!SheetsUtil.checkSheetId(googleSheetsService, sheetId, token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
            }

            // Read the sheet values
            val sheetValues: List<List<List<Any>>> = googleSheetsService.readSheetValues(sheetId, TokenUtil.getCredentials())
            val transactionValues = sheetValues.subList(0, sheetValues.size-2).flatten()
            val transactions = SheetsUtil.getTransactions(transactionValues)

            val filteredTransactions = filterTransactionsByDate(transactions, startDate, endDate)

            ResponseEntity.ok(filteredTransactions)
        } catch (e: DateTimeParseException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        } catch (e: GoogleJsonResponseException) {
            if (e.statusCode == 429) {
                ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null)
            } else {
                throw e
            }
        } catch (e: Exception) {
            println(e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @GetMapping("/summary")
    fun getSummary(
        @RequestParam(required = true) sheetId: String,
        @RequestParam(required = false) startDate: String?,
        @RequestParam(required = false) endDate: String?,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<List<SummaryItem>?> {
        return try {
            if (!SheetsUtil.checkSheetId(googleSheetsService, sheetId, token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
            }

            // Read the sheet values
            val sheetValues: List<List<List<Any>>> = googleSheetsService.readSheetValues(sheetId, TokenUtil.getCredentials())
            val transactionValues = sheetValues.subList(0, sheetValues.size-2).flatten()
            val transactions = SheetsUtil.getTransactions(transactionValues)
            val classificationValues: List<List<Any>> = sheetValues[sheetValues.size-2]
            val classifications = SheetsUtil.getClassifications(classificationValues)

            // Filter transactions by date range
            val filteredTransactions = filterTransactionsByDate(transactions, startDate, endDate)

            // Generate the summary
            var summaryItems = ModelUtil.transactionsToSummaryItems(filteredTransactions, classifications)

            // Remove summaryItems that are of type "Excluded"
            summaryItems = summaryItems.filter { it.type != "Excluded" }

            ResponseEntity.ok(summaryItems)
        } catch (e: DateTimeParseException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        } catch (e: GoogleJsonResponseException) {
            if (e.statusCode == 429) {
                ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null)
            } else {
                throw e
            }
        } catch (e: Exception) {
            println(e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @GetMapping("/category-summary")
    fun getMonthSummaries(
            @RequestParam(required = true) sheetId: String,
            @RequestParam(required = true) startDate: String,
            @RequestParam(required = true) endDate: String,
            @RequestHeader("Authorization") token: String
    ): ResponseEntity<List<ClassificationMonth>?> {
        return try {
            if (!SheetsUtil.checkSheetId(googleSheetsService, sheetId, token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
            }

            // Read the sheet values
            val sheetValues: List<List<List<Any>>> = googleSheetsService.readSheetValues(sheetId, TokenUtil.getCredentials())
            val transactionValues = sheetValues.subList(0, sheetValues.size-2).flatten()
            val transactions = SheetsUtil.getTransactions(transactionValues).filter { it.category != "Excluded" }
            val classificationValues: List<List<Any>> = sheetValues[sheetValues.size-2]
            val classifications = SheetsUtil.getClassifications(classificationValues)

            // Generate the category months
            val categoryMonths = ModelUtil.transactionsToCategoryMonths(transactions, classifications, startDate, endDate)

            ResponseEntity.ok(categoryMonths)
        } catch (e: DateTimeParseException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        } catch (e: GoogleJsonResponseException) {
            if (e.statusCode == 429) {
                ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null)
            } else {
                throw e
            }
        } catch (e: Exception) {
            println(e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @GetMapping("/mock-summary-data")
    fun getMockSummaryData() = SummaryGroup(MockData.getSummaryItems(), MockData.getSummaryItems(), MockData.getSummaryItems())

    /**
     * @throws DateTimeParseException if the date is not in the format dd/MM/yyyy
     */
    private fun filterTransactionsByDate(
        transactions: List<Transaction>,
        startDate: String?,
        endDate: String?
    ): List<Transaction> {
        val parsedStartDate = startDate?.toLocalDate()
        val parsedEndDate = endDate?.toLocalDate()

        return transactions.filter { transaction ->
            ModelUtil.passesDateFilter(parsedStartDate, parsedEndDate, transaction.date)
        }
    }
}
