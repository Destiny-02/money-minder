package com.destiny.budgeting.server.model

data class SummaryGroup(
    val thisMonth: List<SummaryItem>,
    val last30Days: List<SummaryItem>,
    val monthlyAverage: List<SummaryItem>
)
