package com.marknjunge.marlin.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.marknjunge.marlin.R
import com.marknjunge.marlin.data.local.PreferencesStorage
import com.marknjunge.marlin.data.model.AccessToken
import com.marknjunge.marlin.data.api.service.ApiService
import com.marknjunge.marlin.data.api.service.OauthService
import com.marknjunge.marlin.data.model.Droplet
import com.marknjunge.marlin.data.model.Resource
import com.marknjunge.marlin.data.model.Status
import com.marknjunge.marlin.data.repository.AuthRepository
import com.marknjunge.marlin.data.repository.DataRepository
import com.marknjunge.marlin.ui.account.AccountActivity
import com.marknjunge.marlin.ui.droplets.DropletsActivity
import com.marknjunge.marlin.ui.droplets.DropletsViewModel
import com.marknjunge.marlin.ui.login.LoginActivity
import com.marknjunge.marlin.utils.getViewModel
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
    private val dataRepo: DataRepository by instance()

    private val viewModel by lazy { MainViewModel(dataRepo) }

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

        btnDroplets.setOnClickListener {
            startActivity(Intent(this, DropletsActivity::class.java))
        }

        val adapter = DropletsPreviewAdapter {}
        rvDroplets.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        rvDroplets.adapter = adapter

        viewModel.droplets.observe(this, Observer<Resource<List<Droplet>>> { dropletResource ->
            when {
                dropletResource.status == Status.LOADING -> {
                    pbDropletsLoading.visibility = View.VISIBLE
                }
                dropletResource.status == Status.SUCCESS -> {
                    pbDropletsLoading.visibility = View.GONE
                    adapter.setItems(dropletResource.data!!)
                }
                dropletResource.status == Status.ERROR -> {
                    pbDropletsLoading.visibility = View.GONE
                    Toast.makeText(this, dropletResource.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.getDroplets()
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
}
