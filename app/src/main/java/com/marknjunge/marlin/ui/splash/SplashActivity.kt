package com.marknjunge.marlin.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.marknjunge.marlin.data.local.PreferencesStorage
import com.marknjunge.marlin.data.model.AccessToken
import com.marknjunge.marlin.data.api.service.OauthService
import com.marknjunge.marlin.ui.login.LoginActivity
import com.marknjunge.marlin.ui.main.MainActivity
import com.marknjunge.marlin.utils.DateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import timber.log.Timber
import java.lang.Exception

class SplashActivity : AppCompatActivity(), KodeinAware {
    override val kodein by closestKodein()
    private val prefs: PreferencesStorage by instance()
    private val oauthService: OauthService by instance()

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

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
            if (it.canExpire && DateTime.now.timestamp >= it.expires) {
                Timber.d("Token has expired")

                uiScope.launch {
                    try {

                        // Get a refresh token
                        val tokenResponse = oauthService.refreshToken(it.refreshToken).await()
                        // Save the new details on the device
                        tokenResponse.run {
                            val expires = System.currentTimeMillis() / 1000 + expiresIn
                            val newToken = AccessToken(accessToken, refreshToken, scope, createdAt, tokenType, expiresIn, true, expires)
                            prefs.accessToken = newToken
                        }

                        // Go to MainActivity
                        Toast.makeText(this@SplashActivity, "Logged in!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    } catch (e: Exception) {
                        Timber.e(e)
                        Toast.makeText(this@SplashActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

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
        job.cancel()
    }
}
