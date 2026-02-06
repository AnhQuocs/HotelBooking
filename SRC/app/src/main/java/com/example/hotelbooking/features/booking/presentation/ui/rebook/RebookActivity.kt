package com.example.hotelbooking.features.booking.presentation.ui.rebook

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.hotelbooking.features.booking.presentation.ui.history.toLocalDate
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryState
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryViewModel
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingUiState
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingViewModel
import com.example.hotelbooking.features.booking.presentation.viewmodel.UpdateBookingState
import com.example.hotelbooking.features.booking.presentation.viewmodel.UpdateBookingViewModel
import com.example.hotelbooking.features.main.BookingRefreshEvent
import com.example.hotelbooking.features.main.MainActivity
import com.example.hotelbooking.features.profile.payment_card.domain.model.PaymentBrand
import com.example.hotelbooking.features.profile.payment_card.domain.model.PaymentCard
import com.example.hotelbooking.features.profile.payment_card.presentation.viewmodel.PaymentCardState
import com.example.hotelbooking.features.profile.payment_card.presentation.viewmodel.PaymentCardViewModel
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.ui.toMillis
import com.example.hotelbooking.features.room.presentation.viewmodel.RoomState
import com.example.hotelbooking.features.room.presentation.viewmodel.RoomViewModel
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

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
                            Toast.makeText(
                                context,
                                (updateState as UpdateBookingState.Success).message,
                                Toast.LENGTH_SHORT
                            ).show()
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
                        updateBookingViewModel = updateViewModel,
                        onUpdateBooking = { booking, newStart, newEnd, newTotalPrice, roomSelected, brand ->
                            updateViewModel.confirmRebook(
                                currentBooking = booking,
                                newCheckIn = newStart,
                                newCheckOut = newEnd,
                                newTotalPrice = newTotalPrice,
                                roomSelected = roomSelected,
                                brand = brand
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
                            Toast.makeText(
                                context,
                                (updateState as UpdateBookingState.Success).message,
                                Toast.LENGTH_SHORT
                            ).show()
                            updateViewModel.resetState()
                            navController.popBackStack()
                        }
                    }

                    if (booking != null) {
                        val mainGuest = booking.guests.firstOrNull { it.isRepresentative }
                        mainGuest?.let {
                            val dobLocalDate: LocalDate =
                                mainGuest.dayOfBirth?.toLocalDate() ?: LocalDate.now()

                            UpdateGuestInfoScreen(
                                name = it.fullName,
                                email = it.email ?: "",
                                phone = it.phone ?: "",
                                dob = dobLocalDate,
                                numberOfGuest = booking.numberOfGuests,
                                capacity = capacity,
                                isUpdating = updateState is UpdateBookingState.Loading,
                                onBackClick = { navController.popBackStack() },
                                onUpdate = { newName, newEmail, newPhone, newDob, newCount ->
                                    val dobTimestamp = Timestamp(
                                        Date(
                                            newDob
                                                .atStartOfDay(ZoneId.systemDefault())
                                                .toInstant()
                                                .toEpochMilli()
                                        )
                                    )

                                    updateViewModel.updateGuestInfo(
                                        newName = newName,
                                        newEmail = newEmail,
                                        newPhone = newPhone,
                                        newDob = dobTimestamp,
                                        newNumberOfGuest = newCount
                                    )
                                }
                            )
                        }
                    }
                }

                composable("payment_complete") {
                    val scope = rememberCoroutineScope()

                    PaymentCompleteScreen(
                        onBackClick = {
                            scope.launch {
                                BookingRefreshEvent.triggerRefresh()

                                val intent = Intent(this@RebookActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                        },
                        onHomeClick = {
                            scope.launch {
                                BookingRefreshEvent.triggerRefresh()

                                val intent = Intent(this@RebookActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
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
    onUpdateBooking: (Booking, Long, Long, Double, String, PaymentBrand) -> Unit,
    bookingViewModel: BookingViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    updateBookingViewModel: UpdateBookingViewModel,
    bookingHistoryViewModel: BookingHistoryViewModel = hiltViewModel(),
    paymentCardViewModel: PaymentCardViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val bookingState by bookingHistoryViewModel.bookingDetailState.collectAsState()
    val cardsState by paymentCardViewModel.cardsState.collectAsState()
    val roomDetailState by roomViewModel.roomDetailState.collectAsState()
    val uiState by bookingViewModel.uiState.collectAsState()
    val updateState by updateBookingViewModel.updateState.collectAsState()

    val start = bookingViewModel.checkInDate
    val end = bookingViewModel.checkOutDate
    var finalTotalPrice by remember { mutableDoubleStateOf(0.0) }

    var isShowBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(bookingId) {
        bookingHistoryViewModel.loadBookingById(bookingId)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        paymentCardViewModel.loadPaymentCards(userId)
    }

    LaunchedEffect(bookingState) {
        if (bookingState is BookingHistoryState.Success<*>) {
            val booking =
                (bookingState as BookingHistoryState.Success<BookingWithHotel>).data.booking
            roomViewModel.loadRoomDetail(booking.roomTypeId)
        }
    }

    var selectedRoomNumber by rememberSaveable { mutableStateOf<String?>(null) }
    LaunchedEffect(uiState, roomDetailState, bookingState) {
        val bookingData = (bookingState as? BookingHistoryState.Success<BookingWithHotel>)
            ?.data?.booking ?: return@LaunchedEffect

        val availableRoomNumbers = (uiState as? BookingUiState.Available)
            ?.roomNumbers ?: return@LaunchedEffect

        if (selectedRoomNumber == null) {
            val isOldRoomStillFree = availableRoomNumbers.contains(bookingData.roomNumber)

            if (isOldRoomStillFree) {
                selectedRoomNumber = bookingData.roomNumber
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    text = stringResource(id = R.string.rebook),
                    onBackClick = onBackClick
                )
            },
            bottomBar = {
                if (roomDetailState is RoomState.Success) {
                    RebookBottomBar(
                        pricePerNight = (roomDetailState as RoomState.Success<RoomType>).data.pricePerNight,
                        startDate = start,
                        endDate = end,
                        uiState = uiState,
                        onBookClick = {
                            if (selectedRoomNumber != null) {
                                isShowBottomSheet = true
                            }
                        },
                        onTotalPriceChange = { newPrice -> finalTotalPrice = newPrice.toDouble() },
                        isEnabled = !selectedRoomNumber.isNullOrEmpty()
                    )
                }
            },
            containerColor = Color.White
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
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
                        val currentRoom = roomDetailState

                        if (currentRoom is RoomState.Success) {
                            RebookContent(
                                booking = data.booking,
                                hotel = data.hotel,
                                room = currentRoom.data,
                                start = start,
                                end = end,
                                finalTotalPrice = finalTotalPrice,
                                uiState = uiState,
                                roomState = roomDetailState,
                                bookingViewModel = bookingViewModel,
                                context = context,
                                onEditClick = { capacity ->
                                    onEditGuestClick(data.booking, capacity)
                                },
                                selectedRoomNumber = selectedRoomNumber,
                                onRoomSelected = {
                                    selectedRoomNumber = it
                                }
                            )
                        }
                    }

                    is BookingHistoryState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                currentBooking.fallbackMessage
                                    ?: stringResource(id = currentBooking.messageRes)
                            )
                        }
                    }
                }
            }

            if (isShowBottomSheet) {
                if (bookingState is BookingHistoryState.Success && cardsState is PaymentCardState.Success) {
                    val data = (bookingState as BookingHistoryState.Success<BookingWithHotel>).data
                    val cards = (cardsState as PaymentCardState.Success<List<PaymentCard>>).data

//                    val title = stringResource(R.string.booking_success_title)
//                    val message = data.hotel?.let {
//                        stringResource(
//                            R.string.booking_success_message,
//                            it.name,
//                            bookingId
//                        )
//                    }

                    val roomNumber = selectedRoomNumber
                        ?: data.booking.roomNumber
                    PaymentMethodBottomSheet(
                        cards = cards,
                        onDismissRequest = { isShowBottomSheet = false },
                        onNextClick = { brand ->
                            isShowBottomSheet = false
                            onUpdateBooking(
                                data.booking,
                                start.toMillis(),
                                end.toMillis(),
                                finalTotalPrice,
                                roomNumber,
                                brand
                            )
                        }
                    )
                }
            }
        }

        if (updateState is UpdateBookingState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.25f))
                    .pointerInput(Unit) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }
    }
}