package com.destiny.budgeting.util

import com.destiny.budgeting.model.SummaryItem
import com.github.mikephil.charting.data.PieEntry

class SummaryUtil {
    companion object {
        fun summaryToCategoryEntries(summary: List<SummaryItem>): List<PieEntry> {
            val entries = ArrayList<PieEntry>()
            for (summaryItem in summary) {
                entries.add(summaryItemToCategoryEntry(summaryItem))
            }
            return entries
        }

        private fun summaryItemToCategoryEntry(summaryItem: SummaryItem): PieEntry {
            return PieEntry(summaryItem.value, summaryItem.category)
        }

        fun summaryToTypeEntries(summary: List<SummaryItem>): List<PieEntry> {
            val typeSumMap = HashMap<String, Float>()

            // Calculate the sum of values for each type
            for (summaryItem in summary) {
                val type = summaryItem.type
                val value = summaryItem.value
                val currentValue = typeSumMap[type] ?: 0.0f
                typeSumMap[type] = currentValue + value
            }

            // Create PieEntry objects using the type and sum values
            val entries = ArrayList<PieEntry>()
            for ((type, sumValue) in typeSumMap) {
                entries.add(PieEntry(sumValue, type))
            }

            return entries
        }
    }
}
