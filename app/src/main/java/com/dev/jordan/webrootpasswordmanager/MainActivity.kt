package com.dev.jordan.webrootpasswordmanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dev.jordan.webrootpasswordmanager.Model.User
import com.dev.jordan.webrootpasswordmanager.Utility.Constants.Companion.PASS_KEY
import com.dev.jordan.webrootpasswordmanager.Utility.Constants.Companion.SHARED_PREF
import com.dev.jordan.webrootpasswordmanager.Utility.Constants.Companion.USER_KEY
import com.dev.jordan.webrootpasswordmanager.Utility.DatabaseHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private val db = DatabaseHelper(this@MainActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupUi()
    }

    private fun setupUi() {

        create_account.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }

        login_button.setOnClickListener {
            if (!verifyFieldsPopulated()) {
                toast("Please fill out all fields")
                return@setOnClickListener
            }

            val username = username_edit.text.toString()
            val password = password_edit.text.toString()
            var user: User? = null

            try {
                user = db.attemptLogin(username,password)
            } catch (e: Exception) {
                Log.d("LOGIN", "Error logging in: " + e.localizedMessage)
                toast("Credentials not found")
            } finally {
                Log.d("LOGIN", "Credentials found")
                if (user != null) {
                    completeLogin(user)
                }
            }
        }
    }

    /**
     * Save shared preferences for current user,
     * and show currentUserActivity
     *
     * @param user that was retrieved from database
     */
    private fun completeLogin(user: User) {
        val prefs: SharedPreferences = this.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        prefs.edit().putString(USER_KEY, user.username).apply()
        prefs.edit().putString(PASS_KEY, user.password).apply()

        val intent = Intent(this, CurrentUserActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    /**
     * Verify all required fields are
     * populated
     *
     * @return false if empty field found, true otherwise
     */
    private fun verifyFieldsPopulated(): Boolean {
        if (username_edit.text.isEmpty() || password_edit.text.isEmpty()) {
            return false
        }
        return true
    }
}
