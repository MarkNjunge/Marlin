package com.marknjunge.marlin.ui.login

import androidx.lifecycle.*
import com.marknjunge.marlin.data.model.AccessToken
import com.marknjunge.marlin.data.model.Resource
import com.marknjunge.marlin.data.model.DigitalOceanError
import com.marknjunge.marlin.data.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class LoginViewModel(private val authRepo: AuthRepository): ViewModel() {

    val login: MutableLiveData<Resource<AccessToken>> = MutableLiveData()

    private val viewmodelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewmodelJob)

    override fun onCleared() {
        super.onCleared()
        viewmodelJob.cancel()
    }

    fun getToken(code: String) {
        uiScope.launch {
            try {
                login.value = Resource.loading()

                val accessToken = authRepo.getToken(code)

                login.value = Resource.success(accessToken)
            } catch (e: Exception) {
                Timber.e(e)
                login.value = Resource.error(e.localizedMessage)
            }
        }
    }

    fun savePersonalToken(token: String, canWrite: Boolean) {
        uiScope.launch {
            try {
                login.value = Resource.loading()

                val accessToken = authRepo.savePersonalToken(token, canWrite)

                login.value = Resource.success(accessToken)
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        val errorString = e.response().errorBody()!!.string()
                        DigitalOceanError.parse(errorString)?.let { digitalOceanError ->
                            Timber.e(digitalOceanError.toString())
                            if (digitalOceanError.id == "unauthorized") {
                                login.value = Resource.error("Invalid token")
                            } else {

                                login.value = Resource.error(errorString)
                            }
                        }
                    }
                    else -> {
                        Timber.e(e)
                        login.value = Resource.error(e.localizedMessage)
                    }
                }
            }
        }
    }
}