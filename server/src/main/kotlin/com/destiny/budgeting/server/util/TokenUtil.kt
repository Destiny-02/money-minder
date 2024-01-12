package com.destiny.budgeting.server.util

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson.JacksonFactory
import com.google.api.services.drive.DriveScopes
import com.google.api.services.sheets.v4.SheetsScopes
import io.github.cdimascio.dotenv.dotenv
import java.util.Collections

class TokenUtil {
    companion object {
        private val dotenv = dotenv()
        private val WEB_CLIENT_ID = dotenv["WEB_CLIENT_ID"]
        fun getCredentials(): GoogleCredential {
            return GoogleCredential.fromStream(
                this::class.java.classLoader.getResourceAsStream("client_secret.json")
            ).createScoped(listOf(SheetsScopes.SPREADSHEETS_READONLY, DriveScopes.DRIVE_METADATA_READONLY))
        }

        fun getEmailFromToken(token: String): String? {
            val verifier: GoogleIdTokenVerifier =
                GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory(),
                )
                    .setAudience(Collections.singletonList(WEB_CLIENT_ID))
                    .build()

            val idToken: GoogleIdToken = verifier.verify(token)
            if (idToken != null) {
                val payload: GoogleIdToken.Payload = idToken.payload
                return payload.email
            }
            return null
        }
    }
}
