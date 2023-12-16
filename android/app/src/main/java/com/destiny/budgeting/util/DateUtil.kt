package com.destiny.budgeting.util

import com.destiny.budgeting.enum.Range
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateUtil {
    companion object {
        fun getDates(range: Range): Pair<LocalDate, LocalDate> {
            val currentDate = LocalDate.now()
            // For testing
            // val currentDate = LocalDate.of(2023, 1, 15)
            return when (range) {
                Range.THIS_MONTH -> {
                    val startDate = currentDate.withDayOfMonth(1)
                    startDate to currentDate
                }
                Range.LAST_30_DAYS -> {
                    val startDate = currentDate.minusDays(30)
                    startDate to currentDate
                }
                Range.MONTHLY_AVERAGE -> {
                    val startDate = currentDate.minusMonths(6)
                    startDate to currentDate
                }
            }
        }

        fun formatDate(date: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            return date.format(formatter)
        }
    }
}
