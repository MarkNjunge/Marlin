package com.marknjunge.marlin.data.model

import com.squareup.moshi.Json

data class DropletResponse(
        @field:Json(name = "droplets")
        val droplets: List<Droplet>
)