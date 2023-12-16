package com.destiny.budgeting.util

import android.content.Context
import android.content.Intent
import com.destiny.budgeting.R
import com.destiny.budgeting.activity.LoginActivity

class ActivityUtil {
    companion object {
        fun logout(context: Context) {
            SharedPreferencesUtil.writeSheetId(context, "")
            TokenUtil.storeTokenInKeystore(context, context.getString(R.string.oauth_token_alias), "")
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }
}
