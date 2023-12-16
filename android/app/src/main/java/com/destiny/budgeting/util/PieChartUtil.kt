package com.destiny.budgeting.util

import android.content.Context
import android.graphics.Color
import android.widget.Toast
import com.destiny.budgeting.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlin.math.absoluteValue

class PieChartUtil {
    companion object {
        private fun createColorList(color1: Int, color2: Int, numColors: Int): List<Int> {
            val colorList = ArrayList<Int>()

            // Extract RGB components of color1
            val red1 = Color.red(color1)
            val green1 = Color.green(color1)
            val blue1 = Color.blue(color1)

            // Extract RGB components of color2
            val red2 = Color.red(color2)
            val green2 = Color.green(color2)
            val blue2 = Color.blue(color2)

            // Calculate the step size for each RGB component
            val stepSize = 1.0f / (numColors - 1)

            // Interpolate between the two colors and add to the color list
            for (i in 0 until numColors) {
                val ratio = i * stepSize
                val interpolatedColor = Color.rgb(
                    (red1 + ratio * (red2 - red1)).toInt(),
                    (green1 + ratio * (green2 - green1)).toInt(),
                    (blue1 + ratio * (blue2 - blue1)).toInt()
                )
                colorList.add(interpolatedColor)
            }

            return colorList
        }

        fun setChart(pieChart: PieChart, originalEntries: List<PieEntry>, context: Context, divideBy: Int): Boolean {
            if (originalEntries.isEmpty()) return false

            // Make sure the entries are non-negative
            // This makes a copy of the entries so the reference to the original list is lost
            val entries = divideBy(makeValuesNonNegative(originalEntries), divideBy)
            val sumOfEntries = entries.sumOf { it.value.toDouble() }

            // Create and style the data set
            val dataSet = PieDataSet(entries, "")
            val data = PieData(dataSet)
            pieChart.data = data

            // Format the values as integers
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }

            // Style the chart
            // Set the colors as interpolated between two colors
            val startColor = context.getColor(R.color.graphColorStart)
            val endColor = context.getColor(R.color.graphColorEnd)
            val colors = createColorList(startColor, endColor, entries.size)
            dataSet.colors = colors // Set the colors

            // Set the font size and font
            dataSet.valueTextSize = 12f
            dataSet.valueTypeface = context.resources.getFont(R.font.poppins_regular)

            // Don't show value if it is too small (less than 5% of the total)
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value/sumOfEntries < 0.05) "" else value.toInt().toString()
                }
            }

            pieChart.isDrawHoleEnabled = false // Hide the center hole
            pieChart.setDrawEntryLabels(false) // Hide the labels
            pieChart.description.isEnabled = false // Hide the description
            pieChart.isRotationEnabled = false // Disable rotation

            // Configure the legend
            val legend = pieChart.legend
            legend.isWordWrapEnabled = true // Enable word wrapping
            legend.textSize = 12f // Set the font size for the legend labels
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.typeface = context.resources.getFont(R.font.poppins_regular) // Set the font
            legend.xEntrySpace = 12f // Set the spacing between legend entries
            legend.textColor = context.getColor(R.color.colorOnSurfaceVariant) // Set the font color

            // Show label when a pie section is selected
            pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    val pieEntry: PieEntry? = e as? PieEntry
                    if (pieEntry != null) {
                        val label = pieEntry.label
                        val value = pieEntry.value

                        val totalValue = entries.sumOf { it.value.toDouble() }
                        val percentage = (value / totalValue) * 100

                        val displayText = "$label $${String.format("%.2f", value)} (${String.format("%.0f", percentage)}%)"
                        Toast.makeText(context, displayText, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onNothingSelected() {
                    // Do nothing
                }
            })

            pieChart.invalidate()
            pieChart.notifyDataSetChanged()
            return true
        }

        private fun makeValuesNonNegative(entries: List<PieEntry>) : List<PieEntry> {
            val newEntries = ArrayList<PieEntry>()
            for (entry in entries) {
                val value = entry.value
                val newEntry = if (value < 0) {
                    PieEntry(value.absoluteValue, entry.label)
                } else {
                    PieEntry(value, entry.label)
                }
                newEntries.add(newEntry)
            }
            return newEntries
        }

        private fun divideBy(entries: List<PieEntry>, divideBy: Int) : List<PieEntry> {
            val newEntries = ArrayList<PieEntry>()
            for (entry in entries) {
                newEntries.add(PieEntry(entry.value / divideBy, entry.label))
            }
            return newEntries
        }
    }
}
