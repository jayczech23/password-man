package com.dev.jordan.webrootpasswordmanager

import com.dev.jordan.webrootpasswordmanager.Model.User
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for Model layer.
 */

class DataModelUnitTest {

    @Test
    fun test_create_user() {
        val user = User("test", "12345", "Is This test?", "Yes")
        assertEquals("test", user.username)
        assertEquals("12345", user.password)
        assertEquals("Is This test?", user.securityQuestion)
        assertEquals("Yes",user.securityAnswer)
    }
}