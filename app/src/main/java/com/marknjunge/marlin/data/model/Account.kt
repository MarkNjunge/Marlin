package com.marknjunge.marlin.data.model

import com.squareup.moshi.Json

data class Account(
        @field:Json(name = "status_message")
        val statusMessage: String,

        @field:Json(name = "email_verified")
        val emailVerified: Boolean,

        @field:Json(name = "droplet_limit")
        val dropletLimit: Int,

        @field:Json(name = "volume_limit")
        val volumeLimit: Int,

        @field:Json(name = "floating_ip_limit")
        val floatingIpLimit: Int,

        @field:Json(name = "uuid")
        val uuid: String,

        @field:Json(name = "email")
        val email: String,

        @field:Json(name = "status")
        val status: String
)