package com.marknjunge.marlin

import android.app.Application
import com.marknjunge.marlin.data.local.PreferencesStorage
import com.marknjunge.marlin.data.local.PreferencesStorageImpl
import com.marknjunge.marlin.data.network.*
import timber.log.Timber

class App : Application() {

    companion object {
        lateinit var preferencesStorage: PreferencesStorage
        lateinit var apiService: ApiService
        lateinit var oauthService: OauthService
        lateinit var digitalOceanConfig: DigitalOceanConfig
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String {
                return "Timber/${element.fileName.substringBefore(".")}.${element.methodName}(Ln${element.lineNumber})"
            }
        })

        preferencesStorage = PreferencesStorageImpl(this)

        val networkProvider = NetworkProvider()
        apiService = networkProvider.apiService
        oauthService = networkProvider.oauthService

        val redirectUrl = "https://marlin"
        digitalOceanConfig = DigitalOceanConfigImpl(BuildConfig.digitalOceanClientId, BuildConfig.digitalOceanClientSecret, redirectUrl)
    }
}