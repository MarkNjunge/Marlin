package com.marknjunge.marlin.data.model

import com.squareup.moshi.Json

data class Image(

	@field:Json(name="min_disk_size")
	val minDiskSize: Int,

	@field:Json(name="regions")
	val regions: List<String>,

	@field:Json(name="public")
	val jsonMemberPublic: Boolean,

	@field:Json(name="name")
	val name: String,

	@field:Json(name="created_at")
	val createdAt: String,

	@field:Json(name="id")
	val id: Int,

	@field:Json(name="distribution")
	val distribution: String,

	@field:Json(name="type")
	val type: String,

	@field:Json(name="size_gigabytes")
	val sizeGigabytes: Double,

	@field:Json(name="slug")
	val slug: String?
)