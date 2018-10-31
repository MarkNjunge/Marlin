package com.marknjunge.marlin.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.marknjunge.marlin.R
import com.marknjunge.marlin.data.local.PreferencesStorage
import com.marknjunge.marlin.data.model.AccessToken
import com.marknjunge.marlin.data.model.User
import com.marknjunge.marlin.data.api.service.ApiService
import com.marknjunge.marlin.data.api.service.OauthService
import com.marknjunge.marlin.ui.account.AccountActivity
import com.marknjunge.marlin.ui.droplets.DropletsActivity
import com.marknjunge.marlin.ui.login.LoginActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import timber.log.Timber

class MainActivity : AppCompatActivity(), KodeinAware {
    override val kodein by closestKodein()
    private val prefs: PreferencesStorage by instance()
    private val oauthService: OauthService by instance()
    private val apiService: ApiService by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "Droplets"

        val accessToken = prefs.accessToken

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

        btnDroplets.setOnClickListener {
            startActivity(Intent(this, DropletsActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.actionbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_account -> startActivity(Intent(this@MainActivity, AccountActivity::class.java))
            R.id.menu_settings -> Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun refreshToken() {
        prefs.accessToken?.let { accessToken ->
            val disposable = oauthService.refreshToken(accessToken.refreshToken)
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

                                    prefs.user = user
                                    prefs.accessToken = newToken
                                }
                            },
                            onError = { throwable ->
                                Timber.e(throwable)
                            }
                    )
        }
    }

    private fun revokeToken() {
        prefs.accessToken?.let { accessToken ->
            val disposable = oauthService.revokeToken("Bearer ${accessToken.accessToken}", accessToken.accessToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onSuccess = {
                                prefs.user = null
                                prefs.accessToken = null

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
        prefs.accessToken?.let { accessToken ->

            apiService.getAccount("Bearer ${accessToken.accessToken}")
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
