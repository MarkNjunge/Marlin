package com.marknjunge.marlin.data.api

import com.marknjunge.marlin.data.api.service.ApiService
import com.marknjunge.marlin.data.api.service.OauthService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class NetworkProvider {
    private val apiUrl = "https://api.digitalocean.com/v2/"
    private val oAuthUrl = "https://cloud.digitalocean.com/v1/oauth/"

    val apiService: ApiService
    val oauthService: OauthService

    init {
        val okHttpClient = OkHttpClient.Builder().build()

        val oauthRetrofit = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .baseUrl(oAuthUrl)
                .build()

        oauthService = oauthRetrofit.create(OauthService::class.java)

        val apiRetrofit = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .baseUrl(apiUrl)
                .build()


        apiService = apiRetrofit.create(ApiService::class.java)
    }

}