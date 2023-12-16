package com.destiny.budgeting.server.model

data class Transaction (
    val date: String,
    val value: Float,
    val category: String,
    val particulars: String
)
