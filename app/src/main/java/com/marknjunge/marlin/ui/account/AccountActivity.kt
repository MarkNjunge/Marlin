package com.marknjunge.marlin.ui.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.marknjunge.marlin.R
import com.marknjunge.marlin.data.api.service.ApiService
import com.marknjunge.marlin.data.local.PreferencesStorage
import com.marknjunge.marlin.data.model.Account
import com.marknjunge.marlin.data.model.Resource
import com.marknjunge.marlin.data.model.Status
import kotlinx.android.synthetic.main.activity_account.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import timber.log.Timber

class AccountActivity : AppCompatActivity(), KodeinAware {
    override val kodein by closestKodein()
    private val apiService: ApiService by instance()
    private val prefs: PreferencesStorage by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        supportActionBar?.title = "Account"

        val viewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AccountViewModel(apiService, prefs) as T
            }
        }).get(AccountViewModel::class.java)

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
                        tvName.text = prefs.user!!.name
                        tvEmail.text = email
                        tvDropletLimit.text = dropletLimit.toString()
                        tvFloatingIpLimit.text = floatingIpLimit.toString()
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
    }
}
