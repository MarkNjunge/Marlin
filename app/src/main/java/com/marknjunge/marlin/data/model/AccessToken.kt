package com.marknjunge.marlin.data.model

data class AccessToken(
        val accessToken: String,
        val refreshToken: String,
        val scope: String,
        val createdAt: Int,
        val tokenType: String,
        val expiresIn: Int,
        val canExpire: Boolean, // Personal access tokens don't expire
        val expires: Long
)