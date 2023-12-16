package com.destiny.budgeting.server.service

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.sheets.v4.Sheets
import org.springframework.stereotype.Service

@Service
class GoogleSheetsService {
    /**
     * Read the values from the Google Sheet
     * @return List of spreadsheet values, each list representing a sheet, each sheet a list of rows, each row a list of cells
     */
    fun readSheetValues(spreadsheetId: String, credentials: GoogleCredential): List<List<List<Any>>> {
        val sheetsService = Sheets.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JacksonFactory(),
            credentials
        ).setApplicationName("Budgeting App").build()

        val spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute()
        val sheets = spreadsheet.sheets

        val retList = ArrayList<List<List<Any>>>()

        for (i in 0 until sheets.size) {
            val sheetValues = ArrayList<List<Any>>()
            val sheet = sheets[i]
            val sheetProperties = sheet.properties
            val sheetRange = "${sheetProperties.title}!A1:D"

            val response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, sheetRange)
                .execute()

            response.getValues()?.let {
                sheetValues.addAll(it)
            }

            retList.add(sheetValues)
        }

        return retList
    }

    /**
     * Check if the Google Sheet is shared with the given email
     * @return true if shared, false otherwise
     */
    fun isSheetSharedWithUser(spreadsheetId: String, credentials: GoogleCredential, email: String): Boolean {
        val driveService = Drive.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JacksonFactory(),
            credentials
        ).setApplicationName("Budgeting App").build()

        val permissionList = driveService.permissions().list(spreadsheetId).setFields("permissions(emailAddress,type)").execute().permissions

        for (permission in permissionList) {
            // Allow any role (e.g. owner, reader, writer)
            if (permission.emailAddress == email && permission.type == "user") {
                return true
            }
        }

        return false
    }
}
