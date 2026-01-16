package com.example.hotelbooking.features.main

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object BookingRefreshEvent {
    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 0)
    val refreshTrigger = _refreshTrigger.asSharedFlow()

    suspend fun triggerRefresh() {
        _refreshTrigger.emit(Unit)
    }
}