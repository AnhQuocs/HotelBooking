package com.example.hotelbooking.features.chat.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.chat.domain.model.ChatMessage
import com.example.hotelbooking.features.chat.domain.repository.ChatRepository
import com.example.hotelbooking.features.chat.domain.usecase.ChatUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChatState<out T> {
    data object Loading : ChatState<Nothing>()
    data class Success<T>(val data: T) : ChatState<T>()
    data class Error(val message: String) : ChatState<Nothing>()
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases,
    private val chatRepository: ChatRepository
) : ViewModel() {

    var chatId by mutableStateOf<String?>(null)
        private set

    private val _chatState =
        MutableStateFlow<ChatState<List<ChatMessage>>>(ChatState.Loading)
    val chatState: StateFlow<ChatState<List<ChatMessage>>> = _chatState

    private var listeningJob: Job? = null

    fun startListening(chatId: String) {
        this.chatId = chatId

        listeningJob?.cancel()
        listeningJob = viewModelScope.launch {
            chatUseCases.listenMessagesUseCase(chatId)
                .collect { list ->
                    _chatState.value = ChatState.Success(list)
                }
        }
    }

    fun loadExistingChat(userId: String, hotelId: String) {
        viewModelScope.launch {
            _chatState.value = ChatState.Loading
            try {
                val existing = chatUseCases.getExistingUseCase(userId, hotelId)
                if (existing != null) {
                    startListening(existing.chatId)
                } else {
                    _chatState.value = ChatState.Success(emptyList())
                }
            } catch (e: Exception) {
                _chatState.value = ChatState.Error(e.message ?: "Load chat failed")
            }
        }
    }

    fun sendMessage(
        userId: String,
        hotelId: String,
        adminId: String,
        senderId: String,
        content: String
    ) {
        viewModelScope.launch {
            try {
                val id = chatUseCases.sendMessageUseCase(
                    userId = userId,
                    hotelId = hotelId,
                    adminId = adminId,
                    senderId = senderId,
                    content = content
                )

                if (chatId == null) {
                    startListening(id)
                }
            } catch (e: Exception) {
                _chatState.value = ChatState.Error(e.message ?: "Send message failed")
            }
        }
    }

    fun sendAdminMessage(
        chatId: String,
        adminId: String,
        content: String
    ) {
        viewModelScope.launch {
            try {
                chatRepository.sendMessage(
                    chatId = chatId,
                    senderId = adminId,
                    content = content
                )
            } catch (e: Exception) {
                _chatState.value = ChatState.Error(e.message ?: "Send admin message failed")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listeningJob?.cancel()
    }
}