package com.example.hotelbooking.features.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.chat.domain.usecase.GetChatListWithHotelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ConversationListViewModel @Inject constructor(
    private val getChatListWithHotelUseCase: GetChatListWithHotelUseCase
) : ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)

    val conversations = _userId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else getChatListWithHotelUseCase(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun load(userId: String) {
        _userId.value = userId
    }
}