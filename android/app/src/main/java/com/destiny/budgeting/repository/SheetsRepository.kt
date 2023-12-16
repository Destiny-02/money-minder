package com.destiny.budgeting.repository

import android.content.Context
import com.destiny.budgeting.R
import com.destiny.budgeting.api.ApiClient
import com.destiny.budgeting.util.TokenUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SheetsRepository(private val context: Context) {
    private val sheetService = ApiClient().getSheetService()

    fun checkIsValidSheet(sheetId: String, callback: ValidSheetCallback) {
        val token = TokenUtil.retrieveTokenFromKeystore(context, context.getString(R.string.oauth_token_alias))

        if (token == null) {
            println("Token is null")
            callback.onError("Please try signing in again.")
            return
        }

        val call = sheetService.getIsSheetIdValid(token, sheetId)
        call.enqueue(object : Callback<Boolean?> {
            override fun onResponse(call: Call<Boolean?>, response: Response<Boolean?>) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else if (response.code() == 429) {
                    callback.onError("Too many requests. Please try again later.")
                } else {
                    println(response.errorBody())
                    callback.onError("An error occurred")
                }
            }

            override fun onFailure(call: Call<Boolean?>, t: Throwable) {
                println(t.message)
                callback.onError("An error occurred")
            }
        })
    }

    interface ValidSheetCallback {
        fun onSuccess(isValid: Boolean)
        fun onError(message: String?)
    }
}
