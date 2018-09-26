package com.dev.jordan.webrootpasswordmanager

import com.dev.jordan.webrootpasswordmanager.Utility.SecurityHelper
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for Security
 * methods, including password creation
 * and Security Question verification.
 */

class SecurityHelperUnitTest {

    @Test
    fun test_confirm_password() {
        val password1 = "password1"
        val password2 = "password1"
        val confirmed = SecurityHelper.confirmPassword(password1,password2)
        assertEquals(true,confirmed)
    }
}