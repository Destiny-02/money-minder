package com.destiny.budgeting.api

import com.destiny.budgeting.model.SummaryGroup
import com.destiny.budgeting.model.SummaryItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface TransactionsService {
    @GET("transactions/mockSummaryData")
    fun getMockSummaryGroup(): Call<SummaryGroup?>

    @GET("transactions/summary")
    fun getSummaryItems(
        @Query("sheetId") sheetId: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Header("Authorization") token: String,
    ): Call<List<SummaryItem>?>
}
