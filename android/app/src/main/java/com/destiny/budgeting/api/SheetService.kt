package com.destiny.budgeting.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SheetService {
    @GET("sheets/verify")
    fun getIsSheetIdValid(
        @Header("Authorization") token: String,
        @Query("sheetId") sheetId: String
    ): Call<Boolean?>
}
