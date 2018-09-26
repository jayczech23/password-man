package com.dev.jordan.webrootpasswordmanager

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import com.dev.jordan.webrootpasswordmanager.Utility.Constants
import com.dev.jordan.webrootpasswordmanager.Utility.DatabaseHelper
import kotlinx.android.synthetic.main.activity_current_user.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

class CurrentUserActivity : AppCompatActivity() {

    private val db = DatabaseHelper(this@CurrentUserActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_user)

        setupUi()
    }

    private fun setupUi() {

        val fields = ArrayList<EditText>()
        fields.add(old_password_update)
        fields.add(new_password_update)
        fields.add(new_password_confirm)

        val prefs: SharedPreferences = this.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
        val currentUsername: String = prefs.getString(Constants.USER_KEY, "User")
        this.title = "Hello " + currentUsername

        update_password_button.setOnClickListener {

            val currentPassword: String = prefs.getString(Constants.PASS_KEY, "password")

            if (!verifyFields(currentPassword, fields)) {
                return@setOnClickListener
            }

            // update password for current user
            val newPass: String = new_password_update.text.toString()
            Log.d("TAG", "FIELDS VERIFIED, UPDATING PASS")

            try {
                db.updatePassForCurrentUser(currentUsername, newPass)
            } catch (e: Exception) {
                Log.d("PASS_UPDATE", "Error updating password: " + e.localizedMessage)
                return@setOnClickListener
            }
            // update shared prefs
            Log.d("PASS_UPDATE", "new password: " + newPass)
            prefs.edit().putString(Constants.PASS_KEY, newPass).apply()
            alert("Password updated","Success!") {
                yesButton {
                    clearTextFields()
                }
            }.show()
        }
    }

    /**
     * Verify all text edit fields,
     * checking for empty, confirmation, and
     * if old_password matches current password.
     *
     * @param currentPass user's current password (stored in shared prefs)
     * @param fields Activity's EditText fields to be checked
     * @return false if verification fails, true otherwise
     */
    private fun verifyFields(currentPass: String, fields: ArrayList<EditText>): Boolean {

        val oldPass: String = old_password_update.text.toString()

        for (field in fields) {
            if (field.text.toString().isEmpty()) {
                toast("Please fill out all fields")
                return false
            }
        }
        // check if old password entry is valid && that passwords match for new pass and confirmation
        if (!oldPass.equals(currentPass) ||
                !new_password_update.text.toString().equals(new_password_confirm.text.toString())) {
            toast("Invalid password(s)")
            return false
        }
        return true
    }

    /**
     * Clear all EditText fields
     * after updating password.
     */
    private fun clearTextFields() {
    old_password_update.setText(R.string.empty_string)
    new_password_update.setText(R.string.empty_string)
    new_password_confirm.setText(R.string.empty_string)
    }
}
