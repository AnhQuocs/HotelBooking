package com.example.hotelbooking.features.booking.presentation.ui.rebook

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.presentation.ui.checkout.PaymentCompleteScreen
import com.example.hotelbooking.features.booking.presentation.ui.checkout.PaymentMethodBottomSheet
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryState
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryViewModel
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingViewModel
import com.example.hotelbooking.features.booking.presentation.viewmodel.UpdateBookingState
import com.example.hotelbooking.features.booking.presentation.viewmodel.UpdateBookingViewModel
import com.example.hotelbooking.features.main.BookingRefreshEvent
import com.example.hotelbooking.features.main.MainActivity
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.ui.toMillis
import com.example.hotelbooking.features.room.presentation.viewmodel.RoomState
import com.example.hotelbooking.features.room.presentation.viewmodel.RoomViewModel
import com.example.hotelbooking.ui.theme.PrimaryBlue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RebookActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val bookingId = intent.getStringExtra("bookingId") ?: ""

        setContent {
            val context = LocalContext.current

            val updateViewModel: UpdateBookingViewModel = hiltViewModel()
            val updateState by updateViewModel.updateState.collectAsState()

            val navController = rememberNavController()
            val start = "rebook_screen"

            NavHost(
                navController = navController,
                startDestination = start
            ) {
                composable("rebook_screen") {
                    LaunchedEffect(updateState) {
                        if (updateState is UpdateBookingState.Success) {
                            Toast.makeText(context, (updateState as UpdateBookingState.Success).message, Toast.LENGTH_SHORT).show()
                            updateViewModel.resetState()
                            navController.navigate("payment_complete") {
                                popUpTo("rebook_screen") {
                                    inclusive = true
                                }
                            }
                        }
                    }

                    RebookScreen(
                        bookingId = bookingId,
                        onBackClick = { finish() },
                        onEditGuestClick = { booking, capacity ->
                            updateViewModel.selectedBooking = booking
                            navController.navigate("update_guest_info/$capacity")
                        },
                        onUpdateBooking = { booking, newStart, newEnd, newTotalPrice ->
                            updateViewModel.confirmRebook(
                                currentBooking = booking,
                                newCheckIn = newStart,
                                newCheckOut = newEnd,
                                newTotalPrice = newTotalPrice
                            )
                        }
                    )
                }

                composable(
                    "update_guest_info/{capacity}",
                    arguments = listOf(navArgument("capacity") { type = NavType.IntType })
                ) { backStackEntry ->
                    val capacity = backStackEntry.arguments?.getInt("capacity") ?: 0
                    val booking = updateViewModel.selectedBooking

                    LaunchedEffect(updateState) {
                        if (updateState is UpdateBookingState.Success) {
                            Toast.makeText(context, (updateState as UpdateBookingState.Success).message, Toast.LENGTH_SHORT).show()
                            updateViewModel.resetState()
                            navController.popBackStack()
                        }
                    }

                    if (booking != null) {
                        UpdateGuestInfoScreen(
                            name = booking.guest.name,
                            email = booking.guest.email,
                            phone = booking.guest.phone,
                            age = booking.guest.age.toString(),
                            numberOfGuest = booking.numberOfGuests,
                            capacity = capacity,
                            isUpdating = updateState is UpdateBookingState.Loading,
                            onBackClick = { navController.popBackStack() },
                            onUpdate = { newName, newEmail, newPhone, newAge, newCount ->
                                updateViewModel.updateGuestInfo(
                                    newName = newName,
                                    newEmail = newEmail,
                                    newAge = newAge.toInt(),
                                    newPhone = newPhone,
                                    newNumberOfGuest = newCount
                                )
                            }
                        )
                    }
                }

                composable("payment_complete") {
                    val scope = rememberCoroutineScope()

                    PaymentCompleteScreen(
                        onBackClick = {
                            scope.launch {
                                BookingRefreshEvent.triggerRefresh()

                                val intent = Intent(this@RebookActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                        },
                        onHomeClick = {
                            scope.launch {
                                BookingRefreshEvent.triggerRefresh()

                                val intent = Intent(this@RebookActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RebookScreen(
    bookingId: String,
    onBackClick: () -> Unit,
    onEditGuestClick: (Booking, Int) -> Unit,
    onUpdateBooking: (Booking, Long, Long, Double) -> Unit,
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

    var isShowBottomSheet by remember { mutableStateOf(false) }

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
                    onBookClick = { isShowBottomSheet = true },
                    onTotalPriceChange = { newPrice -> finalTotalPrice = newPrice.toDouble() }
                )
            }
        },
        containerColor = Color.White
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val currentBooking = bookingState) {
                is BookingHistoryState.Loading, is BookingHistoryState.Idle -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
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
                            context = context,
                            onEditClick = { capacity ->
                                onEditGuestClick(data.booking, capacity)
                            }
                        )
                    }
                }

                is BookingHistoryState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(currentBooking.message)
                    }
                }
            }
        }

        if (isShowBottomSheet) {
            if (bookingState is BookingHistoryState.Success) {
                val data = (bookingState as BookingHistoryState.Success<BookingWithHotel>).data

                val title = stringResource(R.string.booking_success_title)
                val message = data.hotel?.let {
                    stringResource(
                        R.string.booking_success_message,
                        it.name,
                        bookingId
                    )
                }

                PaymentMethodBottomSheet(
                    onDismissRequest = { isShowBottomSheet = false },
                    onNextClick = {
                        isShowBottomSheet = false
                        onUpdateBooking(
                            data.booking,
                            start.toMillis(),
                            end.toMillis(),
                            finalTotalPrice
                        )
                    }
                )
            }
        }
    }
}