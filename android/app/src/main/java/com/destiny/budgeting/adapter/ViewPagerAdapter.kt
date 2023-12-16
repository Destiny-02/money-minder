package com.destiny.budgeting.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.destiny.budgeting.fragment.CategoriesFragment
import com.destiny.budgeting.fragment.TypesFragment
import com.destiny.budgeting.model.SummaryGroup

class ViewPagerAdapter(fm: FragmentManager, private var summaryGroup: SummaryGroup?) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val tabTitles = arrayOf("Categories", "Types")

    override fun getCount(): Int {
        return tabTitles.size
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> CategoriesFragment.newInstance(summaryGroup)
            1 -> TypesFragment.newInstance(summaryGroup)
            else -> throw IllegalArgumentException("Invalid tab position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }

    // Recreate all fragments when notifyDataSetChanged is called
    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    // Update the summaryGroup used by the fragments
    // Use null when summaryGroup is loading
    fun updateSummaryGroup(newSummaryGroup: SummaryGroup?) {
        summaryGroup = newSummaryGroup
        notifyDataSetChanged()
    }
}
