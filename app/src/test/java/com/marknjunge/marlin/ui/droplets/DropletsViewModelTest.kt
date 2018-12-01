package com.marknjunge.marlin.ui.droplets

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.marknjunge.marlin.data.api.service.ApiService
import com.marknjunge.marlin.data.local.PreferencesStorage
import com.marknjunge.marlin.data.model.AccessToken
import com.marknjunge.marlin.data.model.Droplet
import com.marknjunge.marlin.data.model.DropletResponse
import com.marknjunge.marlin.data.model.Resource
import com.marknjunge.marlin.testUtils.mock
import com.marknjunge.marlin.testUtils.provideFakeDispatchers
import kotlinx.coroutines.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DropletsViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val apiService = Mockito.mock(ApiService::class.java)
    private val preferencesStorage = Mockito.mock(PreferencesStorage::class.java)
    private val dispatcherProvider = provideFakeDispatchers()
    private val viewModel = DropletsViewModel(apiService, preferencesStorage, dispatcherProvider)

    @Test
    fun sendDropletsToUI() {
        val droplets = listOf<Droplet>()
        val deferred = GlobalScope.async { DropletResponse(droplets) }
        whenGetAllDroplets(deferred)

        whenGetAccessToken()

        val observer = mock<Observer<Resource<List<Droplet>>>>()
        viewModel.droplets.observeForever(observer)

        viewModel.getDroplets()
        Mockito.verify(observer).onChanged(Resource.loading())
        Mockito.verify(observer).onChanged(Resource.success(droplets))
    }

    @Test
    fun sendErrorToUI() {
        val exception = Exception("error")
        val deferred = GlobalScope.async { throw  exception }
        whenGetAllDroplets(deferred)

        whenGetAccessToken()

        val observer = mock<Observer<Resource<List<Droplet>>>>()
        viewModel.droplets.observeForever(observer)

        viewModel.getDroplets()
        Mockito.verify(observer).onChanged(Resource.loading())
        Mockito.verify(observer).onChanged(Resource.error(exception.localizedMessage))
    }

    private fun whenGetAllDroplets(deferred: Deferred<DropletResponse>) {
        Mockito.`when`(apiService.getAllDroplets(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString())).thenReturn(deferred)
    }

    private fun whenGetAccessToken(){
        val accessToken = AccessToken("", "", "", 0, "", 0, false, 0)
        Mockito.`when`(preferencesStorage.accessToken).thenReturn(accessToken)
    }
}