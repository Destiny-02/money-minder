package com.destiny.budgeting.util

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.destiny.budgeting.R
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class TokenUtil {
    companion object {
        fun storeTokenInKeystore(context: Context, alias: String, token: String) {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            if (!keyStore.containsAlias(alias)) {
                val keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
                )
                val builder = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(false)

                keyGenerator.init(builder.build())
                keyGenerator.generateKey()
            }

            val secretKeyEntry = keyStore.getEntry(alias, null) as KeyStore.SecretKeyEntry
            val secretKey = secretKeyEntry.secretKey

            val encryptedToken = encryptToken(secretKey, token)

            // Store the encrypted token in SharedPreferences or any other secure storage mechanism
            val sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("encrypted_token", encryptedToken).apply()
        }

        fun retrieveTokenFromKeystore(context: Context, alias: String): String? {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            if (!keyStore.containsAlias(alias)) {
                return null
            }

            val secretKeyEntry = keyStore.getEntry(alias, null) as KeyStore.SecretKeyEntry
            val secretKey = secretKeyEntry.secretKey

            val sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            val encryptedToken = sharedPreferences.getString("encrypted_token", null) ?: return null

            return decryptToken(secretKey, encryptedToken)
        }

        private fun encryptToken(secretKey: SecretKey, token: String): String {
            val cipher = Cipher.getInstance("${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val ivBytes = cipher.iv

            val encryptedBytes = cipher.doFinal(token.toByteArray(StandardCharsets.UTF_8))
            val encryptedToken = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)

            return Base64.encodeToString(ivBytes, Base64.DEFAULT) + encryptedToken
        }

        private fun decryptToken(secretKey: SecretKey, encryptedToken: String): String {
            val cipher = Cipher.getInstance("${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}")

            val ivBytes = Base64.decode(encryptedToken.substring(0, 24), Base64.DEFAULT)
            val encryptedBytes = Base64.decode(encryptedToken.substring(24), Base64.DEFAULT)

            val ivParameterSpec = IvParameterSpec(ivBytes)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes, StandardCharsets.UTF_8)
        }
    }
}
