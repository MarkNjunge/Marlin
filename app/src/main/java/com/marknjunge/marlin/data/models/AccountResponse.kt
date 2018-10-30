package com.marknjunge.marlin.data.models

import com.squareup.moshi.Json

data class AccountResponse(
        @field:Json(name = "account")
        val account: Account
)