package com.marknjunge.marlin.data.model

import com.squareup.moshi.Json

data class Kernel(

	@field:Json(name="name")
	val name: String,

	@field:Json(name="id")
	val id: Int,

	@field:Json(name="version")
	val version: String
)