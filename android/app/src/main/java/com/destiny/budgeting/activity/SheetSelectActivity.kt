package com.destiny.budgeting.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.destiny.budgeting.R
import com.destiny.budgeting.repository.SheetsRepository
import com.destiny.budgeting.util.ActivityUtil
import com.destiny.budgeting.util.SharedPreferencesUtil


class SheetSelectActivity : AppCompatActivity() {
    private lateinit var tcCheckBox: CheckBox
    private lateinit var nextButton: Button
    private lateinit var sheetIdEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sheet_select)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Make the links clickable
        var instructions: TextView = findViewById(R.id.sheet_id_instructions)
        instructions.movementMethod = LinkMovementMethod.getInstance()
        tcCheckBox = findViewById(R.id.tc_checkbox)
        tcCheckBox.movementMethod = LinkMovementMethod.getInstance()

        sheetIdEditText = findViewById(R.id.sheet_id_edit_text)
        nextButton = findViewById(R.id.sheet_next_button)
        setUpNextButton()
    }

    private fun setUpNextButton() {
        // Populate with the user's previously entered sheet ID (if any)
        val storedSheetId = SharedPreferencesUtil.getSheetId(this)
        if (storedSheetId != null) {
            sheetIdEditText.setText(storedSheetId)
        }

        nextButton.setOnClickListener {
            val sheetId = sheetIdEditText.text.toString()

            if (!tcCheckBox.isChecked) {
                Toast.makeText(
                    this@SheetSelectActivity,
                    "Please agree to the Terms of Use",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (sheetIdEditText.text.isEmpty()) {
                Toast.makeText(
                    this@SheetSelectActivity,
                    "Please enter a sheet ID",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                SheetsRepository(this@SheetSelectActivity).checkIsValidSheet(sheetId, object : SheetsRepository.ValidSheetCallback {
                    override fun onSuccess(isValid: Boolean) {
                        if (isValid) {
                            launchMainActivity(sheetId)
                        } else {
                            Toast.makeText(
                                this@SheetSelectActivity,
                                "Invalid sheet ID",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onError(message: String?) {
                        Toast.makeText(this@SheetSelectActivity, message, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    fun copyText(textView: View?) {
        if (textView == null || textView !is TextView) {
            return
        }

        val text: String = textView.text.toString()
        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Text copied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_sheet_id, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.itemId == R.id.action_logout) {
            ActivityUtil.logout(this@SheetSelectActivity)
        }

        return when (item.itemId) {
            R.id.action_logout -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun launchMainActivity(sheetId: String) {
        SharedPreferencesUtil.writeSheetId(this, sheetId)
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("sheetId", sheetId)
        startActivity(intent)
    }
}
