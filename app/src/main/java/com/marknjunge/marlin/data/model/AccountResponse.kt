package com.marknjunge.marlin.data.model

import com.squareup.moshi.Json

data class AccountResponse(
        @field:Json(name = "account")
        val account: Account
)