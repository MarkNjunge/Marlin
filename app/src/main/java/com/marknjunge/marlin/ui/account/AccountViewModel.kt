package com.marknjunge.marlin.ui.account

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marknjunge.marlin.data.api.service.ApiService
import com.marknjunge.marlin.data.local.PreferencesStorage
import com.marknjunge.marlin.data.model.Account
import com.marknjunge.marlin.data.model.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import timber.log.Timber

class AccountViewModel(private val apiService: ApiService, private val prefs: PreferencesStorage) : ViewModel() {
    val account: MutableLiveData<Resource<Account>> = MutableLiveData()
    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun getAccount() {
        prefs.accessToken?.let { accessToken ->
            val disposable = apiService.getAccount("Bearer ${accessToken.accessToken}")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        account.value = Resource.loading()
                    }
                    .subscribeBy(
                            onSuccess = { accountResponse ->
                                account.value = Resource.success(accountResponse.account)
                            },
                            onError = { throwable ->
                                when (throwable) {
                                    is HttpException -> {
                                        val errorString = throwable.response().errorBody()!!.string()
                                        Timber.e(errorString)
                                        account.value = Resource.error(errorString)
                                    }
                                    else -> {
                                        Timber.e(throwable)
                                        account.value = Resource.error(throwable.localizedMessage)
                                    }
                                }

                            }
                    )

            compositeDisposable.add(disposable)
        }
    }
}