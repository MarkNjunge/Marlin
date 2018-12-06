package com.marknjunge.marlin.data.repository

import com.marknjunge.marlin.data.api.service.ApiService
import com.marknjunge.marlin.data.local.PreferencesStorage
import com.marknjunge.marlin.data.model.AccountResponse
import com.marknjunge.marlin.data.model.DropletResponse

interface DataRepository {
    suspend fun getAccount(): AccountResponse

    suspend fun getAllDroplets(page: Int = 1, perPage: Int = 10, tag: String = ""): DropletResponse
}

class DataRepositoryImpl(private val apiService: ApiService, private val prefs: PreferencesStorage) : DataRepository {
    override suspend fun getAccount(): AccountResponse {
        // TODO Save locally
        return apiService.getAccount("Bearer ${prefs.accessToken!!.accessToken}").await()
    }

    override suspend fun getAllDroplets(page: Int, perPage: Int, tag: String): DropletResponse {
        // TODO Save locally
        return apiService.getAllDroplets("Bearer ${prefs.accessToken!!.accessToken}", page, perPage, tag).await()
    }
}