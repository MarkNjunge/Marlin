package com.marknjunge.marlin.data.model

import com.squareup.moshi.Json

data class NetworkItem(

	@field:Json(name="netmask")
	val netmask: String,

	@field:Json(name="ip_address")
	val ipAddress: String,

	@field:Json(name="type")
	val type: String,

	@field:Json(name="gateway")
	val gateway: String
)