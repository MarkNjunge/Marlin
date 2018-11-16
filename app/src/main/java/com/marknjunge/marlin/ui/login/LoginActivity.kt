package com.marknjunge.marlin.ui.login

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.marknjunge.marlin.R
import com.marknjunge.marlin.data.local.PreferencesStorage
import com.marknjunge.marlin.data.model.Resource
import com.marknjunge.marlin.data.model.Status
import com.marknjunge.marlin.data.model.User
import com.marknjunge.marlin.data.api.DigitalOceanConfig
import com.marknjunge.marlin.data.api.service.OauthService
import timber.log.Timber
import com.marknjunge.marlin.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class LoginActivity : AppCompatActivity(), KodeinAware {
    override val kodein by closestKodein()
    private val prefs: PreferencesStorage by instance()
    private val oauthService: OauthService by instance()
    private val digitalOceanConfig: DigitalOceanConfig by instance()

    private val viewModel by lazy { LoginViewModel(oauthService, prefs, digitalOceanConfig) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel.user.observe(this, Observer<Resource<User>> { userResource ->
            when {
                userResource.status == Status.LOADING -> {
                    Timber.d("Loading user")
                    enterLoadingState()
                }
                userResource.status == Status.SUCCESS -> {
                    Timber.d("Loaded user")
                    exitLoadingState()

                    // If a user object was returned, take the user to the main activity
                    userResource.data?.let { user ->
                        Toast.makeText(this, "Logged in as ${user.name}", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                }
                userResource.status == Status.ERROR -> {
                    Timber.d("Error loading user")
                    exitLoadingState()

                    // If an error was returned, display the message
                    userResource.errorMessage?.let { errorMessage ->
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()

                        btnLogin.visibility = View.GONE
                        pbLoading.visibility = View.VISIBLE
                    }
                }
            }
        })

        btnLogin.setOnClickListener {
            // Open the browser at allow the user to login
            val url = "https://cloud.digitalocean.com/v1/oauth/authorize?client_id=${digitalOceanConfig.clientId}&redirect_uri=${digitalOceanConfig.redirectUrl}&response_type=code&scope=read"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        val uri = intent.data
        if (uri != null) {
            Timber.d(uri.toString())
            val code = uri.getQueryParameter("code")
            viewModel.getToken(code!!)
        }
    }

    private fun enterLoadingState() {
        btnLogin.visibility = View.GONE
        pbLoading.visibility = View.VISIBLE
    }

    private fun exitLoadingState() {
        btnLogin.visibility = View.VISIBLE
        pbLoading.visibility = View.GONE
    }
}
