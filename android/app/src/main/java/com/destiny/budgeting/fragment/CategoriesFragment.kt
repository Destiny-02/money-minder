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

class CategoriesFragment : PieChartDisplayFragment() {
    companion object {
        private const val ARG_SUMMARY_GROUP = "summaryGroup"

        fun newInstance(summaryGroup: SummaryGroup?): CategoriesFragment {
            val fragment = CategoriesFragment()
            val args = Bundle()
            args.putSerializable(ARG_SUMMARY_GROUP, summaryGroup)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_categories, container, false)

        // Retrieve the argument
        val summaryGroupArg: SummaryGroup? = arguments?.getSerializable(ARG_SUMMARY_GROUP) as SummaryGroup?
        summaryGroup = summaryGroupArg

        // Find views
        spinner = view.findViewById(R.id.categories_spinner)
        pieChart = view.findViewById(R.id.categories_chart)
        emptyMessage = view.findViewById(R.id.categories_empty_message_text_view)
        switch = view.findViewById(R.id.categories_switch)
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
        this.entries = SummaryUtil.summaryToCategoryEntries(RangeUtil.getSummary(summaryGroup, rangeSelection))
        updateChart()
    }

    // Use when entries, rangeSelection, or checkState changes
    override fun updateChart() {
        if (context == null || summaryGroup == null) return

        this.entries = SummaryUtil.summaryToCategoryEntries(RangeUtil.getSummary(summaryGroup!!, rangeSelection))

        // View all the categories if the Show Income switch is checked
        // Otherwise, view only the expenses
        val isChecked = checkState
        val categoriesToExclude = listOf("Salary", "Other Income")
        val filteredEntries = entries.filter { entry ->
            isChecked || (!isChecked && !categoriesToExclude.contains(entry.label))
        }

        val chartHasBeenSet = PieChartUtil.setChart(pieChart, filteredEntries, requireContext(), if (rangeSelection == Range.MONTHLY_AVERAGE) 6 else 1)
        emptyMessage.visibility = if (chartHasBeenSet) View.GONE else View.VISIBLE
        pieChart.visibility = if (chartHasBeenSet) View.VISIBLE else View.GONE
        switch.visibility = if (chartHasBeenSet) View.VISIBLE else View.GONE
        spinner.visibility = if (chartHasBeenSet) View.VISIBLE else View.GONE
    }
}
