package com.dev.jordan.webrootpasswordmanager.Model

data class User(val username: String,
                val password: String,
                val securityQuestion: String,
                val securityAnswer: String)