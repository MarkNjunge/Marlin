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

        val clientId = "c4fb47ac4b8cf02f20c3b9dc7e31790d8eea30022f1dbc791d2588e8c2e3e76b"
        val clientSecret = "4fb7ca2d3b303c1a46e79b4a7b533aba7460bc868308903ebfbac20f5833e520"
        val redirectUrl = "https://marlin"
        digitalOceanConfig = DigitalOceanConfigImpl(clientId, clientSecret, redirectUrl)
    }
}