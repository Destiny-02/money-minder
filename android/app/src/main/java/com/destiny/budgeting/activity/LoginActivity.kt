package com.destiny.budgeting.activity

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.destiny.budgeting.R
import com.destiny.budgeting.util.TokenUtil
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes

class LoginActivity : AppCompatActivity()  {
    private lateinit var oneTapClient: SignInClient
    private lateinit var signUpRequest: BeginSignInRequest
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true

    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        loginButton = findViewById(R.id.login_button)
        setUpOneTap()
        setUpLoginButton()
    }

    private fun setUpOneTap() {
        oneTapClient = Identity.getSignInClient(this)
        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.web_client_id))
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()
    }

    private fun setUpLoginButton() {
        loginButton.setOnClickListener {
            oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this@LoginActivity) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0)
                    } catch (e: IntentSender.SendIntentException) {
                        println("Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(this@LoginActivity) { e ->
                    println(e)
                    Toast.makeText(this@LoginActivity, "No Google Accounts found", Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            launchSheetSelectActivity(idToken)
                        }
                        else -> {
                            // Shouldn't happen.
                            print("No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            println("One-tap dialog was closed.")
                            // Don't re-prompt the user.
                            showOneTapUI = false
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            println("One-tap encountered a network error.")
                        }
                        else -> {
                            println("Couldn't get credential from result. (${e.localizedMessage})")
                        }
                    }
                }
            }
        }
    }

    private fun launchSheetSelectActivity(idToken: String) {
        TokenUtil.storeTokenInKeystore(this, getString(R.string.oauth_token_alias), idToken)
        val intent = Intent(this, SheetSelectActivity::class.java)
        startActivity(intent)
    }
}
