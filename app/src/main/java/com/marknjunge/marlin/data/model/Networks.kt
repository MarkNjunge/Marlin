package com.marknjunge.marlin.data.model

import com.squareup.moshi.Json

data class Networks(

	@field:Json(name="v6")
	val v6: List<NetworkItem>,

	@field:Json(name="v4")
	val v4: List<NetworkItem>
)