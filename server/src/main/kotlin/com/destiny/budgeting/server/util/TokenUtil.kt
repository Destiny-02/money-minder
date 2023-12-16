package com.destiny.budgeting.server.util

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson.JacksonFactory
import com.google.api.services.drive.DriveScopes
import com.google.api.services.sheets.v4.SheetsScopes
import java.util.Collections

class TokenUtil {
    companion object {
        private const val WEB_CLIENT_ID = "344607355066-d8qof5lqcv92k24igpb9pra3cqsh8ili.apps.googleusercontent.com"

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
