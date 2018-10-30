package com.marknjunge.marlin.data.network

import com.marknjunge.marlin.data.models.RevokeResponse
import com.marknjunge.marlin.data.models.TokenResponse
import io.reactivex.Single
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface OauthService {
    @POST("token")
    fun getToken(
            @Query("code") code: String,
            @Query("client_id") clientId: String,
            @Query("client_secret") clientSecret: String,
            @Query("redirect_uri") redirectUri: String,
            @Query("grant_type") grantType: String = "authorization_code"
    ): Single<TokenResponse>

    @POST("token")
    fun refreshToken(@Query("refresh_token") refreshToken: String, @Query("grant_type") grantType: String = "refresh_token"): Single<TokenResponse>

    @POST("revoke")
    fun revokeToken(@Header("Authorization") authHeader: String, @Query("token") token: String): Single<RevokeResponse>

}