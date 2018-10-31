package com.marknjunge.marlin.data.model

import com.squareup.moshi.Json

data class Size(

	@field:Json(name="price_monthly")
	val priceMonthly: Int,

	@field:Json(name="disk")
	val disk: Int,

	@field:Json(name="memory")
	val memory: Int,

	@field:Json(name="transfer")
	val transfer: Int,

	@field:Json(name="regions")
	val regions: List<String>,

	@field:Json(name="available")
	val available: Boolean,

	@field:Json(name="vcpus")
	val vcpus: Int,

	@field:Json(name="slug")
	val slug: String,

	@field:Json(name="price_hourly")
	val priceHourly: Double
)