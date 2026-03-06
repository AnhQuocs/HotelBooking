package com.example.hotelbooking.features.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.auth.domain.model.AuthUser
import com.example.hotelbooking.features.chat.domain.model.AdminChatWithDetails
import com.example.hotelbooking.features.chat.domain.model.ChatMessage
import com.example.hotelbooking.features.chat.domain.usecase.ChatUseCases
import com.example.hotelbooking.features.chat.domain.usecase.GetAdminChatWithDetailsUseCase
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AdminChatState<out T> {
    object Loading : AdminChatState<Nothing>()
    data class Success<T>(
        val data: T,
        val user: AuthUser? = null,
        val hotel: Hotel? = null
    ) : AdminChatState<T>()
    data class Error(val message: String) : AdminChatState<Nothing>()
}

@HiltViewModel
class AdminChatViewModel @Inject constructor(
    private val getAdminChatWithDetailsUseCase: GetAdminChatWithDetailsUseCase,
    private val chatUseCases: ChatUseCases
) : ViewModel() {

    private val _adminId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val chatDetailsList: StateFlow<List<AdminChatWithDetails>> = _adminId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else getAdminChatWithDetailsUseCase(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun load(adminId: String) {
        _adminId.value = adminId
    }

    private val _chatState = MutableStateFlow<AdminChatState<List<ChatMessage>>>(AdminChatState.Loading)
    val chatState: StateFlow<AdminChatState<List<ChatMessage>>> = _chatState

    private var listeningJob: Job? = null
    private var currentChatId: String? = null

    fun startListening(chatId: String) {
        if (currentChatId == chatId) return
        currentChatId = chatId

        listeningJob?.cancel()
        listeningJob = viewModelScope.launch {
            _chatState.value = AdminChatState.Loading

            combine(
                chatUseCases.listenMessagesUseCase(chatId),
                chatDetailsList
            ) { messages, details ->
                val detail = details.find { it.chat.chatId == chatId }

                AdminChatState.Success(
                    data = messages,
                    user = detail?.user,
                    hotel = detail?.hotel
                )
            }.collect { newState ->
                _chatState.value = newState
            }
        }
    }

    fun stopListening() {
        listeningJob?.cancel()
        currentChatId = null
        _chatState.value = AdminChatState.Loading
    }
}