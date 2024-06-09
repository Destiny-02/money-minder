package com.destiny.budgeting.server.model

data class ClassificationMonth(val classification: Classification, val monthValue: List<MonthValue>)

data class MonthValue(val month: String, val value: Float)
