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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
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
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val allChatDetails: Flow<List<AdminChatWithDetails>> = _adminId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else getAdminChatWithDetailsUseCase(id)
    }.distinctUntilChanged()

    @OptIn(FlowPreview::class)
    val filteredChats: StateFlow<List<AdminChatWithDetails>> = combine(
        allChatDetails,
        _searchQuery.debounce(300)
    ) { chats, query ->
        if (query.isBlank()) chats
        else {
            chats.filter { detail ->
                detail.user?.username?.contains(query, ignoreCase = true) == true ||
                        detail.hotel?.name?.contains(query, ignoreCase = true) == true
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _chatState = MutableStateFlow<AdminChatState<List<ChatMessage>>>(AdminChatState.Loading)
    val chatState: StateFlow<AdminChatState<List<ChatMessage>>> = _chatState.asStateFlow()

    private var listeningJob: Job? = null
    private var currentChatId: String? = null

    // --- CÁC HÀM XỬ LÝ ---

    fun load(adminId: String) {
        _adminId.value = adminId
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun startListening(chatId: String) {
        if (currentChatId == chatId) return
        currentChatId = chatId

        listeningJob?.cancel()
        listeningJob = viewModelScope.launch {
            _chatState.value = AdminChatState.Loading

            combine(
                chatUseCases.listenMessagesUseCase(chatId),
                allChatDetails
            ) { messages, details ->
                val detail = details.find { it.chat.chatId == chatId }
                AdminChatState.Success(
                    data = messages,
                    user = detail?.user,
                    hotel = detail?.hotel
                )
            }.catch { e ->
                _chatState.value = AdminChatState.Error(e.message ?: "Unknown Error")
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