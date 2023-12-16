package com.destiny.budgeting.server.model

class ModelUtil {
    companion object {
        fun transactionsToSummaryItems(transactions: List<Transaction>, classifications: List<Classification>): List<SummaryItem> {
            val summaryMap = mutableMapOf<String, Float>()
            val classificationMap = mutableMapOf<String, Classification>()

            // Create a map of category to classification
            for (classification in classifications) {
                classificationMap[classification.category] = classification
            }

            // Calculate the total value for each category
            for (transaction in transactions) {
                val category = transaction.category
                val value = transaction.value

                summaryMap[category] = summaryMap.getOrDefault(category, 0f) + value
            }

            // Create SummaryItem objects from the summaryMap
            val summaryItems = mutableListOf<SummaryItem>()
            for ((category, value) in summaryMap) {
                val summaryItem = classificationMap[category]?.let { SummaryItem(category, it.type, it.description, value) }
                if (summaryItem != null) summaryItems.add(summaryItem)
            }

            return summaryItems
        }
    }
}
