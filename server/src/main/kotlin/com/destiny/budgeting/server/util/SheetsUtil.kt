package com.destiny.budgeting.server.util

import com.destiny.budgeting.server.model.Classification
import com.destiny.budgeting.server.model.Filter
import com.destiny.budgeting.server.model.Transaction
import com.destiny.budgeting.server.service.GoogleSheetsService
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.services.sheets.v4.SheetsScopes

class SheetsUtil {
    companion object {
        fun getTransactions(sheetValues: List<List<Any>>): List<Transaction> {
            return sheetValues.mapNotNull { row ->
                Transaction(
                    date = row[0].toString(),
                    value = row[1].toString().toFloat(),
                    category = row[2].toString(),
                    particulars = row[3].toString()
                )
            }
        }

        fun getClassifications(sheetValues: List<List<Any>>): List<Classification> {
            return sheetValues.mapNotNull { row ->
                Classification(
                    category = row[0].toString(),
                    type = row[1].toString(),
                    description = row[2].toString()
                )
            }
        }

        fun getFilters(sheetValues: List<List<Any>>): List<Filter> {
            return sheetValues.mapNotNull { row ->
                Filter(
                    category = row[0].toString(),
                    query = row[1].toString(),
                )
            }
        }

        fun checkSheetId(googleSheetsService: GoogleSheetsService, sheetId: String, token: String): Boolean {
            val credentials = TokenUtil.getCredentials()
            val serviceAccountEmail = credentials.serviceAccountId
            val userEmail = TokenUtil.getEmailFromToken(token) ?: return false
            return googleSheetsService.isSheetSharedWithUser(sheetId, credentials, serviceAccountEmail) &&
                    googleSheetsService.isSheetSharedWithUser(sheetId, credentials, userEmail)
        }
    }
}
