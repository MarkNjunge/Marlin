package com.marknjunge.marlin.data.model

import com.squareup.moshi.Json

data class Region(

	@field:Json(name="features")
	val features: List<String>,

	@field:Json(name="sizes")
	val sizes: List<String>,

	@field:Json(name="name")
	val name: String,

	@field:Json(name="available")
	val available: Boolean,

	@field:Json(name="slug")
	val slug: String
)