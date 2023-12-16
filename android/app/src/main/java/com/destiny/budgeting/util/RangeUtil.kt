package com.destiny.budgeting.util

import com.destiny.budgeting.enum.Range
import com.destiny.budgeting.model.SummaryGroup
import com.destiny.budgeting.model.SummaryItem

class RangeUtil {
    companion object {
        private fun getRangeName(range: Range): String {
            return when (range) {
                Range.THIS_MONTH -> "This Month"
                Range.LAST_30_DAYS -> "Last 30 Days"
                Range.MONTHLY_AVERAGE -> "Monthly Average"
            }
        }

        fun getRangeEnum(rangeName: String): Range {
            return when (rangeName) {
                "This Month" -> Range.THIS_MONTH
                "Last 30 Days" -> Range.LAST_30_DAYS
                "Monthly Average" -> Range.MONTHLY_AVERAGE
                else -> Range.THIS_MONTH
            }
        }

        fun getRangeStrings(): List<String> {
            return listOf(
                getRangeName(Range.THIS_MONTH),
                getRangeName(Range.LAST_30_DAYS),
                getRangeName(Range.MONTHLY_AVERAGE)
            )
        }

        fun getSummary(summaryGroup: SummaryGroup, range: Range): List<SummaryItem> {
            return when (range) {
                Range.THIS_MONTH -> summaryGroup.thisMonth
                Range.LAST_30_DAYS -> summaryGroup.last30Days
                Range.MONTHLY_AVERAGE -> summaryGroup.monthlyAverage
            }
        }
    }
}
