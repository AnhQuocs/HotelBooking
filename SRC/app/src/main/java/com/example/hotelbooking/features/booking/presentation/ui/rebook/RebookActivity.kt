package com.example.hotelbooking.features.booking.presentation.ui.rebook

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.presentation.ui.checkout.CheckoutSummaryCard
import com.example.hotelbooking.features.booking.presentation.ui.checkout.HotelInfo
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryState
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryViewModel
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingUiState
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingViewModel
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.ui.CheckAvailabilitySection
import com.example.hotelbooking.features.room.presentation.ui.DateSelectionSection
import com.example.hotelbooking.features.room.presentation.ui.toMillis
import com.example.hotelbooking.features.room.presentation.viewmodel.RoomState
import com.example.hotelbooking.features.room.presentation.viewmodel.RoomViewModel
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class RebookActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val bookingId = intent.getStringExtra("bookingId") ?: ""

        setContent {
            RebookScreen(
                bookingId = bookingId,
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
fun RebookScreen(
    bookingId: String,
    onBackClick: () -> Unit,
    bookingViewModel: BookingViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    bookingHistoryViewModel: BookingHistoryViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val bookingState by bookingHistoryViewModel.bookingDetailState.collectAsState()
    val roomState by roomViewModel.roomDetailState.collectAsState()
    val uiState by bookingViewModel.uiState.collectAsState()

    val start = bookingViewModel.checkInDate
    val end = bookingViewModel.checkOutDate
    var finalTotalPrice by remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(bookingId) {
        bookingHistoryViewModel.loadBookingById(bookingId)
    }

    LaunchedEffect(bookingState) {
        if (bookingState is BookingHistoryState.Success<*>) {
            val booking = (bookingState as BookingHistoryState.Success<BookingWithHotel>).data.booking
            roomViewModel.loadRoomDetail(booking.roomTypeId)
        }
    }

    Scaffold(
        topBar = { AppTopBar(text = stringResource(id = R.string.rebook), onBackClick = onBackClick) },
        bottomBar = {
            if (roomState is RoomState.Success) {
                RebookBottomBar(
                    pricePerNight = (roomState as RoomState.Success<RoomType>).data.pricePerNight,
                    startDate = start,
                    endDate = end,
                    uiState = uiState,
                    onBookClick = {

                    },
                    onTotalPriceChange = { newPrice -> finalTotalPrice = newPrice.toDouble() }
                )
            }
        },
        containerColor = Color.White
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val currentBooking = bookingState) {
                is BookingHistoryState.Loading, is BookingHistoryState.Idle -> {

                }

                is BookingHistoryState.Success<*> -> {
                    val data = currentBooking.data as BookingWithHotel
                    val currentRoom = roomState

                    if (currentRoom is RoomState.Success) {
                        RebookContent(
                            booking = data.booking,
                            hotel = data.hotel,
                            room = currentRoom.data,
                            start = start,
                            end = end,
                            finalTotalPrice = finalTotalPrice,
                            uiState = uiState,
                            bookingViewModel = bookingViewModel,
                            context = context
                        )
                    }
                }

                is BookingHistoryState.Error -> {
                    Text(currentBooking.message)
                }
            }
        }
    }
}

@Composable
private fun RebookContent(
    booking: Booking,
    hotel: Hotel?,
    room: RoomType,
    start: LocalDate,
    end: LocalDate,
    finalTotalPrice: Double,
    uiState: BookingUiState,
    bookingViewModel: BookingViewModel,
    context: Context
) {
    val dateStr = "${start.dayOfMonth}-${end.dayOfMonth} ${
        start.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    } ${start.year}"

    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = Dimen.PaddingM)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Dimen.PaddingM)) {
            hotel?.let { HotelInfo(hotel = it, context = context) }
            Spacer(modifier = Modifier.height(AppSpacing.SPlus))
            CheckoutSummaryCard(
                date = dateStr,
                numberOfGuest = booking.numberOfGuests,
                guestName = booking.guest.name,
                roomName = room.name,
                phone = booking.guest.phone,
                totalPrice = "$${finalTotalPrice.toInt()}"
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        DateSelectionSection(
            modifier = Modifier.padding(horizontal = Dimen.PaddingM),
            checkInMillis = start.toMillis(),
            checkOutMillis = end.toMillis(),
            onDateConfirm = { newStart, newEnd ->
                bookingViewModel.onDateSelected(newStart, newEnd)
            }
        )

        Spacer(modifier = Modifier.height(AppSpacing.SPlus))

        CheckAvailabilitySection(
            uiState = uiState,
            startDate = start,
            endDate = end,
            onCheckClick = {
                bookingViewModel.checkRoomAvailability(room.hotelId, room.id, room.totalRoom)
            }
        )
    }
}