package com.dev.jordan.webrootpasswordmanager

import android.content.Context
import com.dev.jordan.webrootpasswordmanager.Utility.DatabaseHelper
import com.nhaarman.mockitokotlin2.mock
import org.jetbrains.anko.db.insert
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before


class DatabaseHelperUnitTest {

    private lateinit var dbHelper: DatabaseHelper
    private val mockContext: Context = mock()

    @Before
    fun setup() {
        dbHelper = DatabaseHelper(mockContext)
    }

    @Test
    fun test_check_existing_username() {
        // 1. get readable db
        // 2. check all rows for 'username' column
        // 3. compare with input to see if name is taken.
        val writeDb = dbHelper.writableDatabase

        // insert a user for test
        writeDb.insert("User",
                "id" to 1,
                "username" to "Tester",
                "password" to "test_pass",
                "security_question" to "Is this test?",
                "security_answer" to "It is!")


        val existingFound = DatabaseHelper.getInstance(mockContext).checkIfUsernameExists("Tester")
        assertEquals(true, existingFound)
    }
}