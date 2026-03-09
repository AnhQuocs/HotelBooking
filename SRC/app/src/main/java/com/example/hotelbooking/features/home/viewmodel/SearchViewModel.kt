package com.example.hotelbooking.features.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.usecase.read.SearchHotelsUseCase
import com.example.hotelbooking.features.hotel.domain.usecase.read.SearchManagedHotelsUseCase
import com.example.hotelbooking.features.hotel.presentation.viewmodel.user.HotelState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchHotelsUseCase: SearchHotelsUseCase,
    private val searchManagedHotelsUseCase: SearchManagedHotelsUseCase
) : ViewModel() {

    private var isAdminMode: Boolean = false

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResultState =
        MutableStateFlow<HotelState<List<Hotel>>>(HotelState.Success(emptyList()))
    val searchResultState = _searchResultState.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isNotBlank()) {
                        performSearch(query)
                    } else {
                        _searchResultState.value = HotelState.Success(emptyList())
                    }
                }
        }
    }

    fun initSearchMode(isAdmin: Boolean) {
        this.isAdminMode = isAdmin
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun performSearch(query: String) {
        val adminId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (adminId.isEmpty()) {
            _searchResultState.value = HotelState.Error("User not logged in")
            return
        }

        viewModelScope.launch {
            _searchResultState.value = HotelState.Loading

            val result = runCatching {
                searchManagedHotelsUseCase(adminId, query)
            }

            result.onSuccess { list ->
                _searchResultState.value = HotelState.Success(list)
            }.onFailure { e ->
                _searchResultState.value = HotelState.Error(e.message ?: "Search failed")
            }
        }
    }
}