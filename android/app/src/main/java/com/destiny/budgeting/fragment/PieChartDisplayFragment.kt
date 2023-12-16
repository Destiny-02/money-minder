package com.destiny.budgeting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.destiny.budgeting.enum.Range
import com.destiny.budgeting.model.SummaryGroup
import com.destiny.budgeting.util.RangeUtil
import com.destiny.budgeting.util.SummaryUtil
import com.destiny.budgeting.util.ViewUtil
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieEntry

abstract class PieChartDisplayFragment: Fragment() {
    protected lateinit var spinner: Spinner
    protected lateinit var pieChart: PieChart
    protected lateinit var emptyMessage: TextView
    protected lateinit var switch: Switch
    protected lateinit var loadingLayout: View

    // Contains all the summary data for any set of filters
    // null when loading
    protected var summaryGroup: SummaryGroup? = null
    // Contains the entries currently displayed in the pie chart
    protected lateinit var entries: List<PieEntry>

    protected var rangeSelection: Range = Range.THIS_MONTH
    protected var checkState: Boolean = false

    abstract override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    protected fun setLoading(isLoading: Boolean) {
        spinner.visibility = if (isLoading) View.GONE else View.VISIBLE
        pieChart.visibility = if (isLoading) View.GONE else View.VISIBLE
        switch.visibility = if (isLoading) View.GONE else View.VISIBLE
        loadingLayout.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    protected fun setSwitch() {
        switch.setOnCheckedChangeListener { _, isChecked ->
            this.checkState = isChecked
            updateChart()
        }
    }

    protected fun setSpinner() {
        if (context == null) return

        ViewUtil.setSpinner(spinner, requireContext()) { range ->
            this.rangeSelection = range
            updateChart()
        }
    }

    protected abstract fun updateSummaryGroup(summaryGroup: SummaryGroup)
    protected abstract fun updateChart()

}
