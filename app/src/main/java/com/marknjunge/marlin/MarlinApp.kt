package com.marknjunge.marlin

import android.app.Application
import com.marknjunge.marlin.data.local.PreferencesStorage
import com.marknjunge.marlin.data.local.PreferencesStorageImpl
import com.marknjunge.marlin.data.network.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import timber.log.Timber

@Suppress("unused")
class MarlinApp : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        bind<PreferencesStorage>() with singleton { PreferencesStorageImpl(this@MarlinApp) }
        bind<NetworkProvider>() with singleton { NetworkProvider() }
        bind<ApiService>() with singleton { instance<NetworkProvider>().apiService }
        bind<OauthService>() with singleton { instance<NetworkProvider>().oauthService }
        bind<DigitalOceanConfig>() with singleton {
            DigitalOceanConfigImpl(BuildConfig.digitalOceanClientId, BuildConfig.digitalOceanClientSecret, "https://marlin")
        }
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String {
                return "Timber/${element.fileName.substringBefore(".")}.${element.methodName}(Ln${element.lineNumber})"
            }
        })
    }
}