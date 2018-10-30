package com.marknjunge.marlin.data.models

data class AccessToken(
        val accessToken: String,
        val refreshToken: String,
        val scope: String,
        val createdAt: Int,
        val tokenType: String,
        val expiresIn: Int,
        val expires: Long
)