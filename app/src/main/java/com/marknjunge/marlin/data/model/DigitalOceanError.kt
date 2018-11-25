package com.marknjunge.marlin.data.model

import com.squareup.moshi.Moshi

data class DigitalOceanError(val id: String, val message: String) {
    companion object {
        private val moshi = Moshi.Builder().build()
        private val adapter by lazy { moshi.adapter(DigitalOceanError::class.java) }

        fun parse(json: String): DigitalOceanError? {
            return adapter.fromJson(json)
        }
    }
}