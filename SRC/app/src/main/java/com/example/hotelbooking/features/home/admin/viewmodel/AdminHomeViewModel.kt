package com.example.hotelbooking.features.home.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.auth.domain.usecase.AuthUseCases
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.StayStatus
import com.example.hotelbooking.features.booking.domain.repository.BookingRepository
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.usecase.AdminHotelUseCases
import com.example.hotelbooking.features.review.domain.model.Review
import com.example.hotelbooking.features.review.domain.repository.ReviewRepository
import com.example.hotelbooking.features.room.domain.usecase.RoomUseCases
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AdminHomeViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val reviewRepository: ReviewRepository,
    private val roomUseCases: RoomUseCases,
    private val adminHotelUseCases: AdminHotelUseCases,
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _allManagedHotels = MutableStateFlow<List<Hotel>>(emptyList())
    val allManagedHotels = _allManagedHotels.asStateFlow()

    private val _currentHotel = MutableStateFlow<Hotel?>(null)
    val currentHotel = _currentHotel.asStateFlow()

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    private val _totalRooms = MutableStateFlow(0)

    val todayRevenue = _bookings.map { list ->
        list.filter { isToday(it.createdAt) && it.status == BookingStatus.CONFIRMED }
            .sumOf { it.totalPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val newBookingsCount = _bookings.map { list ->
        list.count { isToday(it.createdAt) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val occupiedRoomsCount = _bookings.map { list ->
        list.count { it.stayStatus == StayStatus.CHECK_IN }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalRooms = _totalRooms.asStateFlow()

    val todayArrivals = _bookings.map { list ->
        list.filter {
            isToday(it.startDate) &&
                    it.status == BookingStatus.CONFIRMED &&
                    (it.stayStatus == StayStatus.NONE || it.stayStatus == StayStatus.NO_SHOW)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayDepartures = _bookings.map { list ->
        list.filter { isToday(it.endDate) && it.stayStatus == StayStatus.CHECK_IN }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val revenueChartData = _bookings.map { list ->
        calculate7DaysRevenue(list)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentReviews = _reviews.map { list ->
        list.sortedByDescending { it.timestamp }.take(3)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            val user = authUseCases.getCurrentUserUseCase()
            val adminId = user?.uid

            adminId?.let {
                if (it.isBlank()) return@launch

                val hotels = adminHotelUseCases.getHotelsByAdminIdUseCase(adminId)
                _allManagedHotels.value = hotels

                if (hotels.isNotEmpty()) {
                    switchHotel(hotels.first())
                } else {
                    _currentHotel.value = null
                    _isLoading.value = false
                }
            }
        }
    }

    fun switchHotel(hotel: Hotel) {
        viewModelScope.launch {
            _currentHotel.value = hotel
            _isLoading.value = true

            fetchHotelDetails(hotel.id)

            _isLoading.value = false
        }
    }

    private suspend fun fetchHotelDetails(hotelId: String) = coroutineScope {
        val bookingsDeferred = async { bookingRepository.getAllBookingsByHotelId(hotelId) }
        val reviewsDeferred = async { reviewRepository.getReviewsByServiceId(hotelId) }
        val roomsDeferred = async { roomUseCases.getRoomsByHotelIdUseCase(hotelId) }

        _bookings.value = bookingsDeferred.await()
        _reviews.value = reviewsDeferred.await()

        val roomTypes = roomsDeferred.await()
        _totalRooms.value = roomTypes.sumOf { it.totalRoom }
    }

    private fun isToday(timestamp: Timestamp): Boolean {
        val zoneId = ZoneId.systemDefault()
        val date = timestamp.toDate().toInstant().atZone(zoneId).toLocalDate()
        return date == LocalDate.now()
    }

    private fun calculate7DaysRevenue(bookings: List<Booking>): List<Pair<String, Double>> {
        val today = LocalDate.now()
        val result = mutableListOf<Pair<String, Double>>()
        val formatter = DateTimeFormatter.ofPattern("dd/MM")

        for (i in 6 downTo 0) {
            val date = today.minusDays(i.toLong())
            val dateStr = date.format(formatter)

            val dailyTotal = bookings.filter {
                it.status == BookingStatus.CONFIRMED &&
                        it.createdAt.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() == date
            }.sumOf { it.totalPrice }

            result.add(dateStr to dailyTotal)
        }
        return result
    }
}