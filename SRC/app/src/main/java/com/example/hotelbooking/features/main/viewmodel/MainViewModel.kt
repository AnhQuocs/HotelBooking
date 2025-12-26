package com.example.hotelbooking.features.main.viewmodel

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.usecase.read.CheckExpiredBookingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiText {
    data class DynamicString(val value: String) : UiText()

    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : UiText()

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val checkExpirePendingBookingsUseCase: CheckExpiredBookingsUseCase
) : ViewModel() {
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    sealed class UiEvent {
        data class ShowToast(val message: UiText) : UiEvent()
    }

    fun checkGlobalExpiration(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = checkExpirePendingBookingsUseCase(userId)

                result.onSuccess { count ->
                    if (count > 0) {
                        _uiEvent.send(UiEvent.ShowToast(
                            UiText.StringResource(R.string.msg_auto_cancel_expired, count)
                        ))
                    }
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}