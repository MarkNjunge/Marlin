package com.marknjunge.marlin.data

import kotlinx.coroutines.CoroutineDispatcher

data class CoroutineDispatcherProvider(val main: CoroutineDispatcher, val io: CoroutineDispatcher)