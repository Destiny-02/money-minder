package com.destiny.budgeting.server.model

data class SummaryItem(
    val category: String,
    val type: String,
    val description: String,
    val value: Float
)
