package com.marknjunge.marlin.data.api.service

import com.marknjunge.marlin.data.model.AccountResponse
import com.marknjunge.marlin.data.model.DropletResponse
import io.reactivex.Single
import retrofit2.http.*

interface ApiService {
    @GET("account")
    @Headers("Accept: application/json")
    fun getAccount(@Header("Authorization") authHeader: String): Single<AccountResponse>

    @GET("droplets?page=1&per_page=10")
    @Headers("Accept: application/json")
    fun getAllDroplets(@Header("Authorization") authHeader: String,
                       @Query("pag") page: Int = 1,
                       @Query("per_page") perPage: Int = 10,
                       @Query("tag_name") tag: String = ""
    ): Single<DropletResponse>
}