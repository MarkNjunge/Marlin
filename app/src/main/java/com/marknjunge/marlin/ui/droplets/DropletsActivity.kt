package com.marknjunge.marlin.ui.droplets

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marknjunge.marlin.R
import com.marknjunge.marlin.data.api.service.ApiService
import com.marknjunge.marlin.data.local.PreferencesStorage
import com.marknjunge.marlin.data.model.Droplet
import com.marknjunge.marlin.data.model.Resource
import com.marknjunge.marlin.data.model.Status
import kotlinx.android.synthetic.main.activity_droplet_activity.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class DropletsActivity : AppCompatActivity(), KodeinAware {
    override val kodein by closestKodein()
    private val apiService: ApiService by instance()
    private val prefs: PreferencesStorage by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_droplet_activity)
        supportActionBar?.title = "Droplets"

        rvDroplets.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val adapter = DropletsAdapter { droplet ->
            Toast.makeText(this, droplet.name, Toast.LENGTH_SHORT).show()
        }
        rvDroplets.adapter = adapter

        val viewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return DropletsViewModel(apiService, prefs) as T
            }
        }).get(DropletsViewModel::class.java)

        val dropletsLiveData = viewModel.getDroplets()
        dropletsLiveData.observe(this, Observer<Resource<List<Droplet>>> { dropletResource ->
            when {
                dropletResource.status == Status.LOADING -> {
                    pbLoading.visibility = View.VISIBLE
                }
                dropletResource.status == Status.SUCCESS -> {
                    pbLoading.visibility = View.GONE
                    adapter.setItems(dropletResource.data!!)
                }
                dropletResource.status == Status.ERROR -> {
                    pbLoading.visibility = View.GONE
                    Toast.makeText(this, dropletResource.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        })


    }
}
