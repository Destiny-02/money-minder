package com.destiny.budgeting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.destiny.budgeting.R
import com.destiny.budgeting.enum.Range
import com.destiny.budgeting.model.SummaryGroup
import com.destiny.budgeting.util.PieChartUtil
import com.destiny.budgeting.util.RangeUtil
import com.destiny.budgeting.util.SummaryUtil
import com.github.mikephil.charting.data.PieEntry

class TypesFragment : PieChartDisplayFragment() {
    companion object {
        private const val ARG_SUMMARY_GROUP = "summaryGroup"

        fun newInstance(summaryGroup: SummaryGroup?): TypesFragment {
            val fragment = TypesFragment()
            val args = Bundle()
            args.putSerializable(ARG_SUMMARY_GROUP, summaryGroup)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_types, container, false)

        // Retrieve the argument
        val summaryGroupArg: SummaryGroup? = arguments?.getSerializable(ARG_SUMMARY_GROUP) as SummaryGroup?
        summaryGroup = summaryGroupArg

        // Find views
        spinner = view.findViewById(R.id.types_spinner)
        pieChart = view.findViewById(R.id.types_chart)
        emptyMessage = view.findViewById(R.id.types_empty_message_text_view)
        switch = view.findViewById(R.id.types_switch)
        loadingLayout = view.findViewById(R.id.loading_layout)

        // Set up views
        setLoading(summaryGroup == null)
        setSpinner()
        setSwitch()
        updateChart()

        return view
    }

    override fun updateSummaryGroup(summaryGroup: SummaryGroup) {
        this.summaryGroup = summaryGroup
        this.entries = SummaryUtil.summaryToTypeEntries(RangeUtil.getSummary(summaryGroup, rangeSelection))
        updateChart()
    }

    // Use when entries, rangeSelection, or checkState changes
    override fun updateChart() {
        if (context == null || summaryGroup == null) return

        this.entries = SummaryUtil.summaryToTypeEntries(RangeUtil.getSummary(summaryGroup!!, rangeSelection))
        var entriesToDisplay = entries

        val isChecked = checkState
        if (isChecked) {
            // Convert the Income type to savings if the Show Savings switch is checked
            // Get the sum of the Needs and Wants types
            val needsAndWantsSum = entries.filter { entry ->
                entry.label.equals("Needs") || entry.label.equals("Wants")
            }.sumOf { it.value.toDouble() }

            // Get the sum of the Income type
            val incomeEntries = entries.filter { entry ->
                entry.label.equals("Income")
            }

            // Get the value of the Savings type
            val savingsEntryValue = incomeEntries.sumOf { it.value.toDouble() } + needsAndWantsSum

            // Create a new PieEntry for the Savings type and add it to the entries list
            // Exclude the Income types
            val savingsEntry = PieEntry(savingsEntryValue.toFloat(), "Savings")
            entriesToDisplay = entries.filter { entry ->
                !entry.label.equals("Income")
            }.toMutableList()
            entriesToDisplay.add(savingsEntry)
        }
        
        // Sort entries alphabetically
        entriesToDisplay = entriesToDisplay.sortedBy { it.label }

        val chartHasBeenSet = PieChartUtil.setChart(pieChart, entriesToDisplay, requireContext(), if (rangeSelection == Range.MONTHLY_AVERAGE) 6 else 1)
        emptyMessage.visibility = if (chartHasBeenSet) View.GONE else View.VISIBLE
        pieChart.visibility = if (chartHasBeenSet) View.VISIBLE else View.GONE
        switch.visibility = if (chartHasBeenSet) View.VISIBLE else View.GONE
        spinner.visibility = if (chartHasBeenSet) View.VISIBLE else View.GONE
    }
}
