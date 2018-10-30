package com.marknjunge.marlin.data.model

import com.squareup.moshi.Json

data class TokenResponse(
        @field:Json(name = "access_token")
        val accessToken: String,

        @field:Json(name = "refresh_token")
        val refreshToken: String,

        @field:Json(name = "scope")
        val scope: String,

        @field:Json(name = "created_at")
        val createdAt: Int,

        @field:Json(name = "token_type")
        val tokenType: String,

        @field:Json(name = "expires_in")
        val expiresIn: Int,

        @field:Json(name = "info")
        val info: Info
)

data class Info(
        @field:Json(name = "name")
        val name: String,

        @field:Json(name = "uuid")
        val uuid: String,

        @field:Json(name = "email")
        val email: String
)