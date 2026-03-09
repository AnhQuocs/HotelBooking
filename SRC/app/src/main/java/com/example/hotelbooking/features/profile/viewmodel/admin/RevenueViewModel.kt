package com.example.hotelbooking.features.profile.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.booking.domain.usecase.read.GetTotalRevenueUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

enum class RevenuePeriod {
    DAY, WEEK, MONTH, ALL
}

data class RevenueUiState(
    val totalRevenue: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedPeriod: RevenuePeriod = RevenuePeriod.MONTH,
    val selectedHotelId: String? = null,
    val baseDate: LocalDate = LocalDate.now()
)

@HiltViewModel
class RevenueViewModel @Inject constructor(
    private val getTotalRevenueUseCase: GetTotalRevenueUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val adminId: String get() = auth.currentUser?.uid ?: ""

    private val _uiState = MutableStateFlow(RevenueUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.map { Triple(it.selectedPeriod, it.selectedHotelId, it.baseDate) }
                .distinctUntilChanged()
                .collect { (period, hotelId, date) ->
                    fetchRevenue(period, hotelId, date)
                }
        }
    }

    fun onPeriodChange(period: RevenuePeriod) {
        _uiState.update { it.copy(selectedPeriod = period) }
    }

    fun onHotelChange(hotelId: String?) {
        _uiState.update { it.copy(selectedHotelId = hotelId) }
    }

    fun onDateChange(newDate: LocalDate) {
        _uiState.update { it.copy(baseDate = newDate) }
    }

    private fun fetchRevenue(period: RevenuePeriod, hotelId: String?, baseDate: LocalDate) {
        if (adminId.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val (start, end) = calculateTimeRange(period, baseDate)

            val result = runCatching {
                getTotalRevenueUseCase(adminId, start, end, hotelId)
            }

            result.onSuccess { total ->
                _uiState.update { it.copy(totalRevenue = total, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun calculateStartDate(period: RevenuePeriod): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return when (period) {
            RevenuePeriod.DAY -> calendar.timeInMillis
            RevenuePeriod.WEEK -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.timeInMillis
            }

            RevenuePeriod.MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.timeInMillis
            }

            RevenuePeriod.ALL -> 0L
        }
    }

    private fun calculateTimeRange(period: RevenuePeriod, baseDate: LocalDate): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            time = Date.from(baseDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return when (period) {
            RevenuePeriod.DAY -> {
                val start = calendar.timeInMillis
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                start to calendar.timeInMillis
            }
            RevenuePeriod.WEEK -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                val start = calendar.timeInMillis
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
                start to calendar.timeInMillis
            }
            RevenuePeriod.MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val start = calendar.timeInMillis
                calendar.add(Calendar.MONTH, 1)
                start to calendar.timeInMillis
            }
            RevenuePeriod.ALL -> 0L to Long.MAX_VALUE
        }
    }
}