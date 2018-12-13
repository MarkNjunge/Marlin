package com.marknjunge.marlin.ui.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.marknjunge.marlin.R
import com.marknjunge.marlin.data.model.Account
import com.marknjunge.marlin.data.model.Resource
import com.marknjunge.marlin.data.model.Status
import com.marknjunge.marlin.data.repository.AuthRepository
import com.marknjunge.marlin.data.repository.DataRepository
import com.marknjunge.marlin.ui.login.LoginActivity
import com.marknjunge.marlin.utils.GlideApp
import com.marknjunge.marlin.utils.Gravatar
import kotlinx.android.synthetic.main.activity_account.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import timber.log.Timber

class AccountActivity : AppCompatActivity(), KodeinAware {
    override val kodein by closestKodein()
    private val repository: DataRepository by instance()
    private val authRepo: AuthRepository by instance()

    private val viewModel by lazy {
        AccountViewModel(repository, authRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        supportActionBar?.title = "Account"

        viewModel.account.observe(this, Observer<Resource<Account>> { accountResource ->
            when {
                accountResource.status == Status.LOADING -> {
                    Timber.d("Loading account")
                    pbLoading.visibility = View.VISIBLE
                }
                accountResource.status == Status.SUCCESS -> {
                    Timber.d("Loaded account")
                    pbLoading.visibility = View.GONE

                    accountResource.data?.run {
                        tvEmail.text = email
                        tvStatus.text = status
                        tvDropletLimit.text = dropletLimit.toString()
                        tvFloatingIpLimit.text = floatingIpLimit.toString()
                        tvEmailVerified.text = if (emailVerified) "Yes" else "No"
                        tvStatusMessage.text = if (statusMessage.isNotEmpty()) statusMessage else "None"

                        val avatarUrl = Gravatar.generateAvatarUrl(email)
                        GlideApp.with(this@AccountActivity)
                                .load(avatarUrl)
                                .placeholder(R.drawable.ic_logo_blue)
                                .into(imgAvatar)
                    }
                }
                accountResource.status == Status.ERROR -> {
                    Timber.d("Error loading account")
                    pbLoading.visibility = View.GONE
                    Toast.makeText(this, accountResource.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.getAccount()

        btnLogout.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(this@AccountActivity, LoginActivity::class.java))
            finish()
        }
    }
}
