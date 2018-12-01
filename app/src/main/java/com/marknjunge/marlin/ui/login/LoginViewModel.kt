package com.marknjunge.marlin.ui.login

import androidx.lifecycle.*
import com.marknjunge.marlin.data.local.PreferencesStorage
import com.marknjunge.marlin.data.model.AccessToken
import com.marknjunge.marlin.data.model.Resource
import com.marknjunge.marlin.data.api.DigitalOceanConfig
import com.marknjunge.marlin.data.api.service.ApiService
import com.marknjunge.marlin.data.api.service.OauthService
import com.marknjunge.marlin.data.model.DigitalOceanError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class LoginViewModel(private val oauthService: OauthService,
                     private val apiService: ApiService,
                     private val preferencesStorage: PreferencesStorage,
                     private val digitalOceanConfig: DigitalOceanConfig)
    : ViewModel() {

    val login: MutableLiveData<Resource<AccessToken>> = MutableLiveData()

    private val viewmodelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewmodelJob)

    override fun onCleared() {
        super.onCleared()
        viewmodelJob.cancel()
    }

    fun getToken(code: String) {
        uiScope.launch {
            try {
                login.value = Resource.loading()

                val tokenResponse = oauthService.getToken(code, digitalOceanConfig.clientId, digitalOceanConfig.clientSecret, digitalOceanConfig.redirectUrl).await()
                tokenResponse.run {
                    val expires = System.currentTimeMillis() / 1000 + expiresIn

                    val accessToken = AccessToken(accessToken, refreshToken, scope, createdAt, tokenType, expiresIn, true, expires)
                    preferencesStorage.accessToken = accessToken

                    login.value = Resource.success(accessToken)
                }
            } catch (e: Exception) {
                Timber.e(e)
                login.value = Resource.error(e.localizedMessage)
            }
        }
    }

    fun savePersonalToken(token: String, canWrite: Boolean) {
        uiScope.launch {
            try {
                login.value = Resource.loading()

                apiService.getAccount("Bearer $token").await()
                val scope = if (canWrite) "read,write" else "read"
                val accessToken = AccessToken(token, "", scope, 0, "bearer", 0, false, 0)
                preferencesStorage.accessToken = accessToken

                login.value = Resource.success(accessToken)
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        val errorString = e.response().errorBody()!!.string()
                        DigitalOceanError.parse(errorString)?.let { digitalOceanError ->
                            Timber.e(digitalOceanError.toString())
                            if (digitalOceanError.id == "unauthorized") {
                                login.value = Resource.error("Invalid token")
                            } else {

                                login.value = Resource.error(errorString)
                            }
                        }
                    }
                    else -> {
                        Timber.e(e)
                        login.value = Resource.error(e.localizedMessage)
                    }
                }
            }
        }
    }
}