package com.destiny.budgeting.util

import android.content.Context
import com.destiny.budgeting.R

class SharedPreferencesUtil {
    companion object {
        fun getSheetId(context: Context): String? {
            val sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
            return sharedPreferences.getString(context.getString(R.string.sheet_id_key), null)
        }

        fun writeSheetId(context: Context, sheetId: String) {
            val sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
            with(sharedPreferences.edit()) {
                putString(context.getString(R.string.sheet_id_key), sheetId)
                apply()
            }
        }
    }
}
