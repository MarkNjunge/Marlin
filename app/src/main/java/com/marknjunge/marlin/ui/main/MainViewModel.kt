package com.marknjunge.marlin.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marknjunge.marlin.data.model.Droplet
import com.marknjunge.marlin.data.model.Resource
import com.marknjunge.marlin.data.repository.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.lang.Exception

class MainViewModel(private val dataRepo: DataRepository) : ViewModel() {
    val droplets: MutableLiveData<Resource<List<Droplet>>> = MutableLiveData()

    private val viewmodelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewmodelJob)

    override fun onCleared() {
        super.onCleared()
        viewmodelJob.cancel()
    }

    fun getDroplets() {
        uiScope.launch {
            try {
                droplets.value = Resource.loading()
                val dropletResponse = dataRepo.getAllDroplets()
                Timber.d("Returned ${dropletResponse.droplets.size} droplets")
                droplets.value = Resource.success(dropletResponse.droplets)
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        val errorString = e.response().errorBody()!!.string()
                        Timber.e(errorString)
                        droplets.value = Resource.error(errorString)
                    }
                    else -> {
                        Timber.e(e)
                        droplets.value = Resource.error(e.localizedMessage)
                    }
                }
            }

        }

    }
}