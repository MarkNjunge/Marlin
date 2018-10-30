package com.marknjunge.marlin.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.marknjunge.marlin.App
import com.marknjunge.marlin.R
import com.marknjunge.marlin.data.models.AccessToken
import com.marknjunge.marlin.data.models.User
import com.marknjunge.marlin.ui.login.LoginActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val accessToken = App.preferencesStorage.accessToken

        if (accessToken == null) {
            Timber.d("No access token")
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        } else {
            Timber.d("Access token present: $accessToken")
        }

        btnRefresh.setOnClickListener {
            refreshToken()
        }

        btnLogout.setOnClickListener {
            revokeToken()
        }

        btnTest.setOnClickListener {
            getAccount()
        }
    }

    private fun refreshToken() {
        App.preferencesStorage.accessToken?.let { accessToken ->
            val disposable = App.oauthService.refreshToken(accessToken.refreshToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onSuccess = { tokenResponse ->
                                Toast.makeText(this@MainActivity, "Logged in!", Toast.LENGTH_SHORT).show()
                                Timber.d(tokenResponse.toString())
                                tokenResponse.run {
                                    val expires = System.currentTimeMillis() / 1000 + expiresIn
                                    val newToken = AccessToken(tokenResponse.accessToken, refreshToken, scope, createdAt, tokenType, expiresIn, expires)
                                    val user = User(info.name, info.email, info.uuid)

                                    App.preferencesStorage.user = user
                                    App.preferencesStorage.accessToken = newToken
                                }
                            },
                            onError = { throwable ->
                                Timber.e(throwable)
                            }
                    )
        }
    }

    private fun revokeToken() {
        App.preferencesStorage.accessToken?.let { accessToken ->
            val disposable = App.oauthService.revokeToken("Bearer ${accessToken.accessToken}", accessToken.accessToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onSuccess = {
                                App.preferencesStorage.user = null
                                App.preferencesStorage.accessToken = null

                                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                finish()
                            },
                            onError = { throwable ->
                                Timber.e(throwable)
                            }
                    )
        }
    }

    private fun getAccount() {
        App.preferencesStorage.accessToken?.let { accessToken ->

            App.apiService.getAccount("Bearer ${accessToken.accessToken}")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onSuccess = {
                                tvEmail.text = "Token valid"
                            },
                            onError = { throwable ->
                                Timber.e(throwable)
                                tvEmail.text = "Error: ${throwable.localizedMessage}"
                            }
                    )
        }
    }
}
