package com.marknjunge.marlin.data.network

import com.marknjunge.marlin.data.models.AccountResponse
import io.reactivex.Single
import retrofit2.http.*

interface ApiService {
    @GET("account")
    @Headers("Accept: application/json")
    fun getAccount(@Header("Authorization") authHeader: String): Single<AccountResponse>
}