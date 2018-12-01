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
import com.marknjunge.marlin.data.api.service.ApiService
import com.marknjunge.marlin.data.api.service.OauthService
import com.marknjunge.marlin.ui.account.AccountActivity
import com.marknjunge.marlin.ui.droplets.DropletsActivity
import com.marknjunge.marlin.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import timber.log.Timber
import java.lang.Exception

class MainActivity : AppCompatActivity(), KodeinAware {
    override val kodein by closestKodein()
    private val prefs: PreferencesStorage by instance()
    private val oauthService: OauthService by instance()
    private val apiService: ApiService by instance()

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val ioScope = CoroutineScope(Dispatchers.IO)

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
            uiScope.launch {
                try {
                    val tokenResponse = oauthService.refreshToken(accessToken.refreshToken).await()
                    Toast.makeText(this@MainActivity, "Token refreshed", Toast.LENGTH_SHORT).show()
                    Timber.d(tokenResponse.toString())
                    tokenResponse.run {
                        val expires = System.currentTimeMillis() / 1000 + expiresIn
                        val newToken = AccessToken(tokenResponse.accessToken, refreshToken, scope, createdAt, tokenType, expiresIn, true, expires)

                        prefs.accessToken = newToken
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }

    private fun revokeToken() {
        prefs.accessToken?.let { accessToken ->
            // If the token can expire, revoke it
            if (accessToken.canExpire) {
                ioScope.launch {
                    oauthService.revokeToken("Bearer ${accessToken.accessToken}", accessToken.accessToken).await()
                    Timber.i("Token revoked")
                    prefs.accessToken = null
                }
            } else {
                prefs.accessToken = null
            }
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun getAccount() {
        prefs.accessToken?.let { accessToken ->
            uiScope.launch {
                try {
                    apiService.getAccount("Bearer ${accessToken.accessToken}").await()
                    tvEmail.text = "Token valid"
                } catch (e: Exception) {
                    Timber.e(e)
                    tvEmail.text = "Error: ${e.localizedMessage}"
                }
            }
        }
    }
}
