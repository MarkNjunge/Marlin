package com.marknjunge.marlin.ui.droplets

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marknjunge.marlin.data.api.service.ApiService
import com.marknjunge.marlin.data.local.PreferencesStorage
import com.marknjunge.marlin.data.model.Droplet
import com.marknjunge.marlin.data.model.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import timber.log.Timber

class DropletsViewModel(private val apiService: ApiService, private val prefs: PreferencesStorage) : ViewModel() {
    val droplets: MutableLiveData<Resource<List<Droplet>>> = MutableLiveData()
    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun getDroplets() {
        val disposable = apiService.getAllDroplets("Bearer ${prefs.accessToken!!.accessToken}")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    droplets.value = Resource.loading()
                }
                .subscribeBy(
                        onSuccess = { dropletResponse ->
                            Timber.d("Returned ${dropletResponse.droplets.size} droplets")
                            droplets.value = Resource.success(dropletResponse.droplets)
                        },
                        onError = { throwable ->
                            when (throwable) {
                                is HttpException -> {
                                    val errorString = throwable.response().errorBody()!!.string()
                                    Timber.e(errorString)
                                    droplets.value = Resource.error(errorString)
                                }
                                else -> {
                                    Timber.e(throwable)
                                    droplets.value = Resource.error(throwable.localizedMessage)
                                }
                            }
                        }

                )

        compositeDisposable.add(disposable)
    }
}