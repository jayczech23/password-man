package com.dev.jordan.webrootpasswordmanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.dev.jordan.webrootpasswordmanager.Model.User
import com.dev.jordan.webrootpasswordmanager.Utility.Constants
import com.dev.jordan.webrootpasswordmanager.Utility.DatabaseHelper
import com.dev.jordan.webrootpasswordmanager.Utility.SecurityHelper
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.toast

class SignUpActivity : AppCompatActivity() {

    private val TAG = SignUpActivity::class.java.name
    private val db = DatabaseHelper(this@SignUpActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setupUi()
    }

    private fun setupUi() {

        val textFields = ArrayList<EditText>()
        textFields.add(username_create_edit)
        textFields.add(password_create_edit)
        textFields.add(password_confirm_edit)
        textFields.add(security_question_edit)
        textFields.add(security_question_answer)

        /**
         * Verify fields when user tries to
         * create an account, and call Database handler
         * to store in database.
         */
        finalize_account_create_button.setOnClickListener {
            val username = username_create_edit.text.toString()
            val pass = password_create_edit.text.toString()
            val confirmPass = password_confirm_edit.text.toString()
            val securityQuestion = security_question_edit.text.toString()
            val securityAnswer = security_question_answer.text.toString()

            if (!verifyFieldsPopulated(textFields)) {
                toast("Please fill out all fields")
                return@setOnClickListener
            }

            val noMatch = "Passwords do not match"

            if (!SecurityHelper.confirmPassword(pass,confirmPass)) {
                toast(noMatch)
                Log.d(TAG, noMatch)
            } else {
                // check if username exists and create user in database if not.
                if (!db.checkIfUsernameExists(username)) {
                    val newUser = User(username, pass, securityQuestion, securityAnswer)
                    db.createNewUser(newUser)
                    setCurrentUser(newUser)
                    openCurrentUserView()
                } else {
                    val usernameExistsError = "That Username already exists"
                    toast(usernameExistsError)
                    Log.d(TAG, usernameExistsError)
                }
            }
        }
    }

    /**
     * Verify all required fields are populated
     *
     * @param fields ArrayList of EditText fields to be verified
     * @return false if empty field found, true otherwise.
     */
    private fun verifyFieldsPopulated(fields: ArrayList<EditText>) : Boolean {
        for (field in fields) {
            if (field.text.isEmpty())
                return false
        }
        return true
    }

    /**
     * Set the current user in shared preferences
     *
     * @param user User to save to shared preferences as current user
     */
    private fun setCurrentUser(user: User) {
        val prefs: SharedPreferences = this.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
        prefs.edit().putString(Constants.USER_KEY, user.username).apply()
        prefs.edit().putString(Constants.PASS_KEY, user.password).apply()
    }

    /**
     * Show CurrentUserActivity
     */
    private fun openCurrentUserView() {
        val intent = Intent(this, CurrentUserActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
