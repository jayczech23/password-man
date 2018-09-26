package com.dev.jordan.webrootpasswordmanager.Utility

class SecurityHelper {

    companion object {
        fun confirmPassword(pass1: String, pass2: String): Boolean {
            return pass1 == pass2
        }
    }
}