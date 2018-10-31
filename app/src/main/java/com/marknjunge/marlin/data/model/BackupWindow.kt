package com.marknjunge.marlin.data.model

import com.squareup.moshi.Json

data class BackupWindow(
	@field:Json(name="start")
	val start: String,

	@field:Json(name="end")
	val end: String
)