package com.marknjunge.marlin.data.repository

import com.marknjunge.marlin.data.api.DigitalOceanConfig
import com.marknjunge.marlin.data.api.service.ApiService
import com.marknjunge.marlin.data.api.service.OauthService
import com.marknjunge.marlin.data.local.PreferencesStorage
import com.marknjunge.marlin.data.model.AccessToken
import com.marknjunge.marlin.utils.DateTime

interface AuthRepository {
    suspend fun getToken(code: String): AccessToken

    suspend fun savePersonalToken(token: String, canWrite: Boolean): AccessToken

    suspend fun refreshToken(): AccessToken

    suspend fun revokeToken()
}

class AuthRepositoryImpl(private val oauthService: OauthService,
                         private val apiService: ApiService,
                         private val prefs: PreferencesStorage,
                         private val doConfig: DigitalOceanConfig
) : AuthRepository {
    override suspend fun getToken(code: String): AccessToken {
        val tokenResponse = oauthService.getToken(code, doConfig.clientId, doConfig.clientSecret, doConfig.redirectUrl).await()

        // Save the access token locally
        val accessToken = tokenResponse.run {
            val expires = System.currentTimeMillis() / 1000 + expiresIn
            AccessToken(accessToken, refreshToken, scope, createdAt, tokenType, expiresIn, true, expires)
        }
        prefs.accessToken = accessToken

        return accessToken
    }

    override suspend fun savePersonalToken(token: String, canWrite: Boolean): AccessToken {
        // Check if the token works
        apiService.getAccount("Bearer $token").await()

        val scope = if (canWrite) "read,write" else "read"
        val accessToken = AccessToken(token, "", scope, 0, "bearer", 0, false, 0)
        prefs.accessToken = accessToken

        return accessToken
    }

    override suspend fun refreshToken(): AccessToken {
        return if (prefs.accessToken!!.canExpire && DateTime.now.timestamp >= prefs.accessToken!!.expires) {
            val tokenResponse = oauthService.refreshToken(prefs.accessToken!!.refreshToken).await()

            // Save the new access token locally
            val newToken = tokenResponse.run {
                val expires = System.currentTimeMillis() / 1000 + expiresIn
                AccessToken(accessToken, refreshToken, scope, createdAt, tokenType, expiresIn, true, expires)
            }
            prefs.accessToken = newToken

            return newToken
        } else {
            prefs.accessToken!!
        }
    }

    override suspend fun revokeToken() {
        if (prefs.accessToken!!.canExpire) {
            oauthService.revokeToken("Bearer ${prefs.accessToken!!.accessToken}", prefs.accessToken!!.accessToken).await()
            prefs.accessToken = null
        }
    }
}