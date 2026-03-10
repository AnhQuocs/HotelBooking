package com.example.hotelbooking.features.booking.presentation.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.domain.usecase.read.SearchBookingsWithHotelUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchBookingsViewModel @Inject constructor(
    private val searchBookingsWithHotelUseCase: SearchBookingsWithHotelUseCase
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery

    private val _searchResultState = MutableStateFlow<BookingHistoryState<List<BookingWithHotel>>>(
        BookingHistoryState.Success(emptyList())
    )
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
                        _searchResultState.value = BookingHistoryState.Success(emptyList())
                    }
                }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private var searchJob: Job? = null

    private fun performSearch(query: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            searchBookingsWithHotelUseCase(userId, query)
                .onStart {
                    _searchResultState.value = BookingHistoryState.Loading
                }
                .catch { e ->
                    _searchResultState.value = BookingHistoryState.Error(
                        messageRes = R.string.error_search_failed,
                        fallbackMessage = e.message
                    )
                }
                .collect { list ->
                    _searchResultState.value = BookingHistoryState.Success(list)
                }
        }
    }
}