package com.marknjunge.marlin

import android.app.Application
import com.marknjunge.marlin.data.CoroutineDispatcherProvider
import com.marknjunge.marlin.data.local.PreferencesStorage
import com.marknjunge.marlin.data.local.PreferencesStorageImpl
import com.marknjunge.marlin.data.api.*
import com.marknjunge.marlin.data.repository.AuthRepository
import com.marknjunge.marlin.data.repository.AuthRepositoryImpl
import com.marknjunge.marlin.data.repository.DataRepositoryImpl
import com.marknjunge.marlin.data.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import timber.log.Timber

@Suppress("unused")
class MarlinApp : Application(), KodeinAware {
    private val networkProvider by lazy { NetworkProvider() }

    override val kodein = Kodein.lazy {
        bind<PreferencesStorage>() with singleton { PreferencesStorageImpl(this@MarlinApp) }
        bind<DigitalOceanConfig>() with singleton {
            DigitalOceanConfigImpl(BuildConfig.digitalOceanClientId, BuildConfig.digitalOceanClientSecret, "https://marlin")
        }
        bind<CoroutineDispatcherProvider>() with singleton { CoroutineDispatcherProvider(Dispatchers.Main, Dispatchers.IO) }
        bind<DataRepository>() with singleton { DataRepositoryImpl(networkProvider.apiService, instance()) }
        bind<AuthRepository>() with singleton { AuthRepositoryImpl(networkProvider.oauthService, networkProvider.apiService, instance(), instance()) }
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