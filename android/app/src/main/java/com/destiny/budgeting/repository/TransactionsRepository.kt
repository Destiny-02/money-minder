package com.destiny.budgeting.repository

import android.content.Context
import com.destiny.budgeting.R
import com.destiny.budgeting.api.ApiClient
import com.destiny.budgeting.api.TransactionsService
import com.destiny.budgeting.model.SummaryGroup
import com.destiny.budgeting.model.SummaryItem
import com.destiny.budgeting.util.DateUtil
import com.destiny.budgeting.util.TokenUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class TransactionsRepository(private val context: Context) {
    private val transactionsService: TransactionsService = ApiClient().getTransactionsService()

    fun getMockSummaryGroup(callback: SummaryGroupCallback) {
        val call = transactionsService.getMockSummaryGroup()
        call.enqueue(object : Callback<SummaryGroup?> {
            override fun onResponse(call: Call<SummaryGroup?>, response: Response<SummaryGroup?>) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else {
                    println(response.errorBody())
                    callback.onError("An error occurred")
                }
            }

            override fun onFailure(call: Call<SummaryGroup?>, t: Throwable) {
                println(t.message)
                callback.onError("An error occurred")
            }
        })
    }

    fun getSummaryItems(sheetId: String, startDate: LocalDate, endDate: LocalDate, callback: SummaryItemsCallback) {
        val token = TokenUtil.retrieveTokenFromKeystore(context, context.getString(R.string.oauth_token_alias))
        if (token == null) {
            callback.onError("Please try signing in again.")
            return
        }

        val call = transactionsService.getSummaryItems(sheetId, DateUtil.formatDate(startDate), DateUtil.formatDate(endDate), token)
        call.enqueue(object : Callback<List<SummaryItem>?> {
            override fun onResponse(call: Call<List<SummaryItem>?>, response: Response<List<SummaryItem>?>) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else if (response.code() == 429) {
                    callback.onError("Too many requests. Please try again later.")
                } else if (response.code() == 400) {
                    callback.onError("Bad Request.")
                } else {
                    println(response.errorBody())
                    callback.onError("An error occurred")
                }
            }

            override fun onFailure(call: Call<List<SummaryItem>?>, t: Throwable) {
                println(t.message)
                callback.onError("An error occurred")
            }
        })
    }

    interface SummaryItemsCallback {
        fun onSuccess(summaryItems: List<SummaryItem>)
        fun onError(message: String?)
    }

    interface SummaryGroupCallback {
        fun onSuccess(summaryGroup: SummaryGroup)
        fun onError(message: String?)
    }
}
