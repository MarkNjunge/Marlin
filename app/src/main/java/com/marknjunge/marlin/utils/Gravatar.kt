package com.marknjunge.marlin.utils

import java.security.MessageDigest

object Gravatar {
    fun generateAvatarUrl(email: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digested = md.digest(email.toByteArray())
        val hash = digested.joinToString("") {
            String.format("%02x", it)
        }
        return "https://www.gravatar.com/avatar/$hash?s=200"
    }
}