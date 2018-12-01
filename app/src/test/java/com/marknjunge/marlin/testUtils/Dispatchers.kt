package com.marknjunge.marlin.testUtils

import com.marknjunge.marlin.data.CoroutineDispatcherProvider
import kotlinx.coroutines.Dispatchers

fun provideFakeDispatchers() = CoroutineDispatcherProvider(Dispatchers.Unconfined, Dispatchers.Unconfined)
