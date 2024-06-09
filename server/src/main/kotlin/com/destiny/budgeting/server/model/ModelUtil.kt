package com.destiny.budgeting.server.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ModelUtil {
    companion object {
        fun transactionsToSummaryItems(transactions: List<Transaction>, classifications: List<Classification>): List<SummaryItem> {
            val summaryMap = mutableMapOf<String, Float>()
            val classificationMap = createClassificationMap(classifications)

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

        fun transactionsToCategoryMonths(
                transactions: List<Transaction>,
                classifications: List<Classification>,
                startDate: String,
                endDate: String
        ): List<ClassificationMonth> {
            val categoryMap = mutableMapOf<String, MutableMap<String, Float>>()
            val classificationMap = createClassificationMap(classifications)

            val parsedStartDate = startDate.toLocalDate()
            val parsedEndDate = endDate.toLocalDate()

            // Populate categoryMap with empty "months" (first day of the month)
            val months = getFirstDayOfMonths(parsedStartDate, parsedEndDate)
            for (classification in classifications) {
                val monthValue = mutableMapOf<String, Float>()
                for (month in months) {
                    monthValue[month.toAusString()] = 0F
                }
                categoryMap[classification.category] = monthValue
            }

            // Populate categoryMap with transactions
            for (transaction in transactions) {
                if (!passesDateFilter(parsedStartDate, parsedEndDate, transaction.date)) continue

                val tCategory = transaction.category
                val tValue = transaction.value
                val tDate = getFirstDayOfMonth(transaction.date)

                val monthValue = categoryMap[tCategory]
                if (monthValue != null) {
                    val value = monthValue[tDate] ?: 0F
                    monthValue[tDate] = value + tValue
                }
            }

            // Create ClassificationMonths from categoryMap
            val classificationMonths = mutableListOf<ClassificationMonth>()
            for ((category, monthValue) in categoryMap) {
                val classification = classificationMap[category]
                val monthValues = mutableListOf<MonthValue>()

                for ((month, value) in monthValue) {
                    monthValues.add(MonthValue(month, value))
                }

                classification?.let { classificationMonths.add(ClassificationMonth(it, monthValues)) }
            }

            return classificationMonths
        }

        /**
         * @throws DateTimeParseException if the date is not in the format dd/MM/yyyy
         *
         * startDate and endDate are both optional (nullable)
         */
        fun passesDateFilter(startDate: LocalDate?, endDate: LocalDate?, date: String): Boolean {
            val parsedDate = date.toLocalDate()

            return (startDate == null || parsedDate >= startDate) && (endDate == null || parsedDate <= endDate)
        }

        private fun createClassificationMap(classifications: List<Classification>): Map<String, Classification> {
            val classificationMap = mutableMapOf<String, Classification>()

            // Create a map of category to classification
            for (classification in classifications) {
                classificationMap[classification.category] = classification
            }

            return classificationMap
        }

        private fun getFirstDayOfMonth(date: String): String {
            val parsedStartDate = date.toLocalDate()

            val firstDay = LocalDate.of(parsedStartDate.year, parsedStartDate.month, 1)
            return firstDay.toAusString()
        }

        private fun getFirstDayOfMonths(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
            val firstDay = LocalDate.of(startDate.year, startDate.month, 1)
            val lastDay = LocalDate.of(endDate.year, endDate.month, 1)
            val months = mutableListOf<LocalDate>()

            var currMonth = firstDay
            while (currMonth.isBefore(lastDay) || currMonth.isAfter(lastDay)) {
                months.add(currMonth)
                currMonth = currMonth.plusMonths(1)
            }

            return months

        }

        fun String.toLocalDate(): LocalDate {
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            return this.let { LocalDate.parse(it, dateFormatter) }
        }

        private fun LocalDate.toAusString(): String {
            return this.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }
    }
}
