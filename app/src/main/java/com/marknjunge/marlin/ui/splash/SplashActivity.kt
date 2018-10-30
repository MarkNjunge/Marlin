package com.marknjunge.marlin.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.marknjunge.marlin.data.local.PreferencesStorage
import com.marknjunge.marlin.data.models.AccessToken
import com.marknjunge.marlin.data.models.User
import com.marknjunge.marlin.data.network.OauthService
import com.marknjunge.marlin.ui.login.LoginActivity
import com.marknjunge.marlin.ui.main.MainActivity
import com.marknjunge.marlin.utils.DateTime
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import timber.log.Timber

class SplashActivity : AppCompatActivity(), KodeinAware {
    override val kodein by closestKodein()
    private val prefs: PreferencesStorage by instance()
    private val oauthService: OauthService by instance()

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If the accessToken is null, they are not logged in. Go to LoginActivity
        if (prefs.accessToken == null) {
            Timber.d("User is not logged in")
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
            return
        }

        // Check if the token has expired. If it has, refresh it
        prefs.accessToken?.let {
            if (DateTime.now.timestamp >= it.expires) {
                Timber.d("Token has expired")

                // Get a refresh token
                val disposable = oauthService.refreshToken(it.refreshToken)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onSuccess = { tokenResponse ->
                                    // Save the new details on the device
                                    tokenResponse.run {
                                        val expires = System.currentTimeMillis() / 1000 + expiresIn
                                        val newToken = AccessToken(accessToken, refreshToken, scope, createdAt, tokenType, expiresIn, expires)
                                        val user = User(info.name, info.email, info.uuid)

                                        prefs.user = user
                                        prefs.accessToken = newToken
                                    }

                                    // Go to MainActivity
                                    Toast.makeText(this@SplashActivity, "Logged in!", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                    finish()
                                },
                                onError = { throwable ->
                                    Timber.e(throwable)
                                    Toast.makeText(this, throwable.localizedMessage, Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                        )

                compositeDisposable.add(disposable)

                return
            }
        }

        // All checks have passed. The user can use the app
        Timber.d("User already logged in")
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }
}
