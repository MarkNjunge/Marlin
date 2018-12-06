package com.marknjunge.marlin.ui.account

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marknjunge.marlin.data.model.Account
import com.marknjunge.marlin.data.model.Resource
import com.marknjunge.marlin.data.repository.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class AccountViewModel(private val repository: DataRepository) : ViewModel() {
    val account: MutableLiveData<Resource<Account>> = MutableLiveData()

    private val viewmodelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewmodelJob)

    override fun onCleared() {
        super.onCleared()
        viewmodelJob.cancel()
    }

    fun getAccount() {
        uiScope.launch {
            try {
                account.value = Resource.loading()
                val accountResponse = repository.getAccount()
                account.value = Resource.success(accountResponse.account)
            } catch (e: HttpException) {
                val errorString = e.response().errorBody()!!.string()
                Timber.e(errorString)
                account.value = Resource.error(errorString)
            }
        }
    }
}