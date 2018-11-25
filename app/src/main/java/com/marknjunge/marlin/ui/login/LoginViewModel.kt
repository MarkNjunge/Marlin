package com.marknjunge.marlin.ui.login

import androidx.lifecycle.*
import com.marknjunge.marlin.data.local.PreferencesStorage
import com.marknjunge.marlin.data.model.AccessToken
import com.marknjunge.marlin.data.model.Resource
import com.marknjunge.marlin.data.api.DigitalOceanConfig
import com.marknjunge.marlin.data.api.service.OauthService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class LoginViewModel(private val oauthService: OauthService,
                     private val preferencesStorage: PreferencesStorage,
                     private val digitalOceanConfig: DigitalOceanConfig)
    : ViewModel() {

    val login: MutableLiveData<Resource<AccessToken>> = MutableLiveData()
    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun getToken(code: String) {
        val disposable = oauthService.getToken(code, digitalOceanConfig.clientId, digitalOceanConfig.clientSecret, digitalOceanConfig.redirectUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    login.value = Resource.loading()
                }
                .subscribeBy(
                        onSuccess = { tokenResponse ->
                            tokenResponse.run {
                                val expires = System.currentTimeMillis() / 1000 + expiresIn

                                val accessToken = AccessToken(accessToken, refreshToken, scope, createdAt, tokenType, expiresIn, expires)
                                preferencesStorage.accessToken = accessToken

                                login.value = Resource.success(accessToken)
                            }
                        },
                        onError = { throwable ->
                            Timber.e(throwable)
                            login.value = Resource.error(throwable.localizedMessage)
                        }
                )

        compositeDisposable.add(disposable)
    }
}