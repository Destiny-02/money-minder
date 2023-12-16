package com.destiny.budgeting.util

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.destiny.budgeting.enum.Range

class ViewUtil {
    companion object {
        fun setSpinner(spinner: Spinner, context: Context, callback: (Range) -> Unit) {
            // Set up the spinner
            val array = RangeUtil.getRangeStrings()

            // Create an ArrayAdapter using the string array and a default spinner layout
            val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, array)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            // Set the default option as selected
            spinner.setSelection(0)

            // Set the spinner item selection listener
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedOption = array[position]
                    callback.invoke(RangeUtil.getRangeEnum(selectedOption))
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }
        }
    }
}
