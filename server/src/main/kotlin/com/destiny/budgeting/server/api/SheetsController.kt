package com.destiny.budgeting.server.api

import com.destiny.budgeting.server.model.Classification
import com.destiny.budgeting.server.model.Filter
import com.destiny.budgeting.server.service.GoogleSheetsService
import com.destiny.budgeting.server.util.SheetsUtil
import com.destiny.budgeting.server.util.TokenUtil
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/api/sheets")
class SheetsController(private val googleSheetsService: GoogleSheetsService) {

    @GetMapping("/classifications")
    fun getClassifications(
        @RequestParam(required = true) sheetId: String,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<List<Classification>?> {
        return try {
            if (!SheetsUtil.checkSheetId(googleSheetsService, sheetId, token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
            }

            // Read the sheet values
            val sheetValues: List<List<List<Any>>> = googleSheetsService.readSheetValues(sheetId, TokenUtil.getCredentials())
            val classificationValues: List<List<Any>> = sheetValues[sheetValues.size-2]

            val classifications = SheetsUtil.getClassifications(classificationValues)

            ResponseEntity.ok(classifications)
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

    @GetMapping("/filters")
    fun getFilters(
        @RequestParam(required = true) sheetId: String,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<List<Filter>?> {
        return try {
            if (!SheetsUtil.checkSheetId(googleSheetsService, sheetId, token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
            }

            // Read the sheet values
            val sheetValues: List<List<List<Any>>> = googleSheetsService.readSheetValues(sheetId, TokenUtil.getCredentials())
            val filterValues: List<List<Any>> = sheetValues.last()

            val filters = SheetsUtil.getFilters(filterValues)

            ResponseEntity.ok(filters)
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

    @GetMapping("/verify")
    fun getIsSheetIdValid(
        @RequestParam(required = true) sheetId: String,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Boolean?> {
        return try {
            val sheetIdIsOk = SheetsUtil.checkSheetId(googleSheetsService, sheetId, token)
            ResponseEntity.ok(sheetIdIsOk)
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
}
