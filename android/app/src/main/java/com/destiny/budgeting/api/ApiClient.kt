package com.destiny.budgeting.api

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class ApiClient {
    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080/api/"
    }

    private lateinit var retrofit: Retrofit

    private val client: Retrofit
        get() {
            if (::retrofit.isInitialized.not()) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }

    fun getTransactionsService(): TransactionsService {
        return client.create(TransactionsService::class.java)
    }

    fun getSheetService(): SheetService {
        return client.create(SheetService::class.java)
    }
}
