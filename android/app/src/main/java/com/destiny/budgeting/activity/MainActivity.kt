package com.destiny.budgeting.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.destiny.budgeting.R
import com.destiny.budgeting.adapter.ViewPagerAdapter
import com.destiny.budgeting.enum.Range
import com.destiny.budgeting.model.SummaryGroup
import com.destiny.budgeting.model.SummaryItem
import com.destiny.budgeting.repository.TransactionsRepository
import com.destiny.budgeting.util.ActivityUtil
import com.destiny.budgeting.util.DateUtil
import com.google.android.material.tabs.TabLayout
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private var summaryGroup: SummaryGroup? = null
    private val adapter: ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager, summaryGroup)
    private val transactionsRepository = TransactionsRepository(this@MainActivity)
    private lateinit var sheetId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get sheetId from extras
        sheetId = intent.getStringExtra("sheetId") ?: ""

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up the ViewPager with the sections adapter.
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = adapter
        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)
        useRefreshedSummaryGroup()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.itemId == R.id.action_refresh) {
            useRefreshedSummaryGroup()
        } else if (item.itemId == R.id.action_logout) {
            ActivityUtil.logout(this@MainActivity)
        }

        return when (item.itemId) {
            R.id.action_refresh -> true
            R.id.action_logout -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun useRefreshedSummaryGroup() {
        // Date ranges
        val thisMonthDates: Pair<LocalDate, LocalDate> = DateUtil.getDates(Range.THIS_MONTH)
        val last30DaysDates: Pair<LocalDate, LocalDate> = DateUtil.getDates(Range.LAST_30_DAYS)
        val monthlyAverageDates: Pair<LocalDate, LocalDate> = DateUtil.getDates(Range.MONTHLY_AVERAGE)

        adapter.updateSummaryGroup(null)

        val newSummaryGroup = SummaryGroup()

        // Make 3 requests to get the summary items for the 3 date ranges
        transactionsRepository.getSummaryItems(sheetId, thisMonthDates.first, thisMonthDates.second, object: TransactionsRepository.SummaryItemsCallback {
            override fun onSuccess(summaryItems: List<SummaryItem>) {
                newSummaryGroup.thisMonth = summaryItems
                adapter.updateSummaryGroup(newSummaryGroup)
            }
            override fun onError(message: String?) {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        })

        transactionsRepository.getSummaryItems(sheetId, last30DaysDates.first, last30DaysDates.second, object: TransactionsRepository.SummaryItemsCallback {
            override fun onSuccess(summaryItems: List<SummaryItem>) {
                newSummaryGroup.last30Days = summaryItems
                adapter.updateSummaryGroup(newSummaryGroup)
            }
            override fun onError(message: String?) {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        })

        transactionsRepository.getSummaryItems(sheetId, monthlyAverageDates.first, monthlyAverageDates.second, object: TransactionsRepository.SummaryItemsCallback {
            override fun onSuccess(summaryItems: List<SummaryItem>) {
                newSummaryGroup.monthlyAverage = summaryItems
                adapter.updateSummaryGroup(newSummaryGroup)
            }
            override fun onError(message: String?) {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
