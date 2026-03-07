package com.example.hotelbooking.features.booking.presentation.viewmodel.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.auth.domain.usecase.AuthUseCases
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.StayStatus
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.example.hotelbooking.features.home.admin.SyncBookingAutoUseCase
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.usecase.AdminHotelUseCases
import com.example.hotelbooking.features.review.domain.model.Review
import com.example.hotelbooking.features.review.domain.repository.ReviewRepository
import com.example.hotelbooking.features.room.domain.usecase.RoomUseCases
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    object NoHotels : DashboardUiState()
    data class Success(
        val currentHotel: Hotel,
        val allHotels: List<Hotel>,
        val selectedDate: LocalDate,
        val stats: DashboardStats,
        val recentReviews: List<Review>,
        val chartData: List<Pair<String, Double>>
    ) : DashboardUiState()

    data class Error(val message: String) : DashboardUiState()
}

data class DashboardStats(
    val arrivalsCount: Int,
    val departuresCount: Int,
    val occupiedCount: Int,
    val newBookingsCount: Int,
    val totalRooms: Int,
    val todayRevenue: Double
)

@HiltViewModel
class AdminHomeViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val reviewRepository: ReviewRepository,
    private val roomUseCases: RoomUseCases,
    private val adminHotelUseCases: AdminHotelUseCases,
    private val syncBookingAutoUseCase: SyncBookingAutoUseCase,
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    private val _allManagedHotels = MutableStateFlow<List<Hotel>>(emptyList())
    private val _currentHotel = MutableStateFlow<Hotel?>(null)
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    private val _totalRooms = MutableStateFlow(0)

    val uiState: StateFlow<DashboardUiState> = combine(
        _isLoading,
        _currentHotel,
        _allManagedHotels,
        _selectedDate,
        _bookings,
        _reviews,
        _totalRooms
    ) { flows ->
        @Suppress("UNCHECKED_CAST")
        val loading = flows[0] as Boolean

        @Suppress("UNCHECKED_CAST")
        val currentHotel = flows[1] as Hotel?

        @Suppress("UNCHECKED_CAST")
        val allHotels = flows[2] as List<Hotel>

        @Suppress("UNCHECKED_CAST")
        val date = flows[3] as LocalDate

        @Suppress("UNCHECKED_CAST")
        val bookings = flows[4] as List<Booking>

        @Suppress("UNCHECKED_CAST")
        val reviews = flows[5] as List<Review>
        val totalRooms = flows[6] as Int

        when {
            loading -> DashboardUiState.Loading
            allHotels.isEmpty() -> DashboardUiState.NoHotels
            currentHotel != null -> {
                DashboardUiState.Success(
                    currentHotel = currentHotel,
                    allHotels = allHotels,
                    selectedDate = date,
                    recentReviews = reviews.sortedByDescending { it.timestamp }.take(3),
                    chartData = calculate7DaysRevenue(bookings, date),
                    stats = DashboardStats(
                        arrivalsCount = bookings.count {
                            isTargetDate(
                                it.startDate,
                                date
                            ) && it.status == BookingStatus.CONFIRMED &&
                                    it.stayStatus == StayStatus.NONE
                        },
                        departuresCount = bookings.count {
                            isTargetDate(it.endDate, date) &&
                                    it.status == BookingStatus.CONFIRMED &&
                                    it.stayStatus == StayStatus.CHECK_IN
                        },
                        occupiedCount = bookings.count {
                            val start =
                                it.startDate.toDate().toInstant().atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            val end = it.endDate.toDate().toInstant().atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            !date.isBefore(start) && date.isBefore(end) && it.status == BookingStatus.CONFIRMED
                        },
                        newBookingsCount = bookings.count { isTargetDate(it.createdAt, date) },
                        totalRooms = totalRooms,
                        todayRevenue = bookings.filter {
                            isTargetDate(
                                it.createdAt,
                                date
                            ) && it.status == BookingStatus.CONFIRMED
                        }
                            .sumOf { it.totalPrice }
                    )
                )
            }

            else -> DashboardUiState.Error("Something went wrong")
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState.Loading)

    init {
        loadDashboardData()
    }

    fun updateSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true

            authUseCases.getCurrentUserUseCase()
                .flatMapLatest { user ->
                    val adminId = user?.uid
                    if (adminId.isNullOrBlank()) {
                        _isLoading.value = false
                        flowOf(emptyList())
                    } else {
                        adminHotelUseCases.getHotelsByAdminIdUseCase(adminId)
                    }
                }
                .collect { hotels ->
                    _allManagedHotels.value = hotels

                    if (hotels.isNotEmpty()) {
                        switchHotel(hotels.first())
                    }

                    _isLoading.value = false
                }
        }
    }

    fun switchHotel(hotel: Hotel) {
        viewModelScope.launch {
            _currentHotel.value = hotel
            _isLoading.value = true

            fetchHotelDetails(hotel.id)
            _isLoading.value = false

            launch {
                try {
                    syncBookingAutoUseCase(_bookings.value)
                    fetchHotelDetails(hotel.id)
                } catch (e: Exception) {
                    Log.e("SyncLog", "Dọn rác thất bại: ${e.message}")
                }
            }
        }
    }

    private var bookingsJob: Job? = null

    private fun fetchHotelDetails(hotelId: String) {
        bookingsJob?.cancel()

        bookingsJob = viewModelScope.launch {
            bookingRepository.getAllBookingsByHotelId(hotelId).collect { latestBookings ->
                _bookings.value = latestBookings
            }
        }

        viewModelScope.launch {
            try {
                val reviewsDeferred = async {
                    reviewRepository.getReviewsByServiceId(hotelId)
                }

                val roomsDeferred = async {
                    roomUseCases
                        .getRoomsByHotelIdUseCase(hotelId)
                        .first()
                }

                _reviews.value = reviewsDeferred.await()

                val rooms = roomsDeferred.await()
                _totalRooms.value = rooms.sumOf { it.totalRoom }

            } catch (e: Exception) {
                Log.e("AdminHome", "Error fetching extra details: ${e.message}")
            }
        }
    }

    private fun isTargetDate(timestamp: Timestamp, targetDate: LocalDate): Boolean {
        val zoneId = ZoneId.systemDefault()
        return timestamp.toDate().toInstant().atZone(zoneId).toLocalDate() == targetDate
    }

    private fun calculate7DaysRevenue(
        bookings: List<Booking>,
        targetDate: LocalDate
    ): List<Pair<String, Double>> {
        val formatter = DateTimeFormatter.ofPattern("dd/MM")
        return (6 downTo 0).map { i ->
            val date = targetDate.minusDays(i.toLong())
            val dailyTotal = bookings.filter {
                it.status == BookingStatus.CONFIRMED &&
                        it.createdAt.toDate().toInstant().atZone(ZoneId.systemDefault())
                            .toLocalDate() == date
            }.sumOf { it.totalPrice }
            date.format(formatter) to dailyTotal
        }
    }
}