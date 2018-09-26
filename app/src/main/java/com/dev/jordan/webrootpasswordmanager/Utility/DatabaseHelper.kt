package com.dev.jordan.webrootpasswordmanager.Utility

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.dev.jordan.webrootpasswordmanager.Model.User
import org.jetbrains.anko.db.*

/**
 * Database handler class
 */
class DatabaseHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "Database", null, 1) {

    companion object {

        private var instance: DatabaseHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): DatabaseHelper {
            if (instance == null) {
                instance = DatabaseHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    /**
     * Create User table
     */
    override fun onCreate(db: SQLiteDatabase) {
        db.createTable("User", true,
                "id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "username" to TEXT,
                "password" to TEXT,
                "security_question" to TEXT,
                "security_answer" to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable("User",true)
    }

    /**
     * Create new user in 'User' table
     *
     * @param user user to be created
     */
    fun createNewUser(user: User) {
        this.writableDatabase.insert("User",
                "username" to user.username,
                "password" to user.password,
                "security_question" to user.securityQuestion,
                "security_answer" to user.securityAnswer)
    }

    /**
     * Retrieve all users from anko
     * database.
     *
     * @return ArrayList<User> of all users in database.
     */
    private fun retrieveAllUsers() : ArrayList<User> {
        val users = ArrayList<User>()

        this.readableDatabase.select("User").parseList(object : MapRowParser<ArrayList<User>> {
            override fun parseRow(columns: Map<String, Any?>): ArrayList<User> {

                val username = columns.getValue("username")
                val password = columns.getValue("password")
                val securityQuestion = columns.getValue("security_question")
                val securityAnswer = columns.getValue("security_answer")
                val user = User(username.toString(),password.toString(),securityQuestion.toString(),securityAnswer.toString())

                users.add(user)
                return users
            }
        })

        return users
    }

    /**
     * Check if a username exists in database
     * already, when user attempts to create a
     * new account.
     *
     * @param name the username attempting to be created
     */
    fun checkIfUsernameExists(name: String) : Boolean {
        val allUsers = retrieveAllUsers()

        if (allUsers.any { user ->  user.username.equals(name, ignoreCase = true) }) {
            return true
        }
        return false
    }

    /**
     * Query database for given username and password.
     * if found with given params, complete login
     *
     * @param username
     * @param password
     * @throws Exception if user is not found with username and password credentials
     */
    fun attemptLogin(username: String, password: String): User? {

        var currentUser: User? = null

        this.readableDatabase.select("User", "username","password","security_question","security_answer")
                .whereArgs("(username = {userName}) and (password = {pass})",
                        "userName" to username,
                        "pass" to password)
                .exec {
                    val rowParser = classParser<User>()
                    val result = parseSingle(rowParser)
                    currentUser = User(result.username, result.password, result.securityQuestion, result.securityAnswer)
                }
        return currentUser
    }

    /**
     * Update currently logged in user's password
     *
     * @param currentUser current user's username
     * @param newPass password to be updated
     */
    fun updatePassForCurrentUser(currentUser: String, newPass: String) {

        this.writableDatabase.update("User", "password" to newPass)
                .whereArgs("username = {userName}", "userName" to currentUser)
                .exec()
    }
}

val Context.database: DatabaseHelper
    get() = DatabaseHelper(applicationContext)