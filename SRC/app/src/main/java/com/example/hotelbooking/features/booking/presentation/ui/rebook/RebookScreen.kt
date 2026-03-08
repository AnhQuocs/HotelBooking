package com.example.hotelbooking.features.booking.presentation.ui.rebook

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.presentation.ui.checkout.PaymentMethodBottomSheet
import com.example.hotelbooking.features.booking.presentation.ui.checkout.VoucherSelectionBottomSheet
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.BookingHistoryState
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.BookingHistoryViewModel
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.BookingUiState
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.BookingViewModel
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.UpdateBookingState
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.UpdateBookingViewModel
import com.example.hotelbooking.features.profile.feature.payment_card.domain.model.PaymentBrand
import com.example.hotelbooking.features.profile.feature.payment_card.domain.model.PaymentCard
import com.example.hotelbooking.features.profile.feature.payment_card.presentation.viewmodel.PaymentCardState
import com.example.hotelbooking.features.profile.feature.payment_card.presentation.viewmodel.PaymentCardViewModel
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.ui.toMillis
import com.example.hotelbooking.features.room.presentation.viewmodel.user.RoomState
import com.example.hotelbooking.features.room.presentation.viewmodel.user.RoomViewModel
import com.example.hotelbooking.features.vouchers.domain.model.Voucher
import com.example.hotelbooking.features.vouchers.presentation.viewmodel.UserVoucherState
import com.example.hotelbooking.features.vouchers.presentation.viewmodel.UserVoucherViewModel
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.google.firebase.auth.FirebaseAuth

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
    paymentCardViewModel: PaymentCardViewModel = hiltViewModel(),
    voucherViewModel: UserVoucherViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val bookingState by bookingHistoryViewModel.bookingDetailState.collectAsState()
    val cardsState by paymentCardViewModel.cardsState.collectAsState()
    val roomDetailState by roomViewModel.roomDetailState.collectAsState()
    val uiState by bookingViewModel.uiState.collectAsState()
    val updateState by updateBookingViewModel.updateState.collectAsState()

    val voucherState by voucherViewModel.uiState.collectAsState()
    var isShowPromoBottomSheet by remember { mutableStateOf(false) }
    var selectedVoucher by remember { mutableStateOf<Voucher?>(null) }

    val start = bookingViewModel.checkInDate
    val end = bookingViewModel.checkOutDate
    var finalTotalPrice by remember { mutableDoubleStateOf(0.0) }

    var isShowBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(bookingId) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        bookingHistoryViewModel.loadBookingById(bookingId)
        paymentCardViewModel.loadPaymentCards(userId)

        if (userId.isNotEmpty()) {
            voucherViewModel.loadUserVouchers(userId)
        }
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
        val bookingData =
            (bookingState as? BookingHistoryState.Success<BookingWithHotel>)?.data?.booking
                ?: return@LaunchedEffect

        val availableRoomNumbers =
            (uiState as? BookingUiState.Available)?.roomNumbers ?: return@LaunchedEffect

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
                    text = stringResource(id = R.string.rebook), onBackClick = onBackClick
                )
            }, bottomBar = {
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
            }, containerColor = Color.White
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (val currentBooking = bookingState) {
                    is BookingHistoryState.Loading, is BookingHistoryState.Idle -> {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
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
                                originalPrice = finalTotalPrice,
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
                                },
                                appliedVoucher = selectedVoucher,
                                onPromoClick = { isShowPromoBottomSheet = true }
                            )
                        }
                    }

                    is BookingHistoryState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
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

                    val roomNumber = selectedRoomNumber ?: data.booking.roomNumber
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
                        })
                }
            }

            if (isShowPromoBottomSheet && voucherState is UserVoucherState.Success) {
                val hotelId = (bookingState as? BookingHistoryState.Success<BookingWithHotel>)?.data?.hotel?.id

                val availableVouchers = (voucherState as UserVoucherState.Success).vouchers.filter { voucher ->
                    voucher.hotelId == hotelId &&
                            !voucher.isUsed &&
                            finalTotalPrice >= voucher.minOrderValue
                }

                VoucherSelectionBottomSheet(
                    vouchers = availableVouchers,
                    selectedVoucher = selectedVoucher,
                    onDismiss = { isShowPromoBottomSheet = false },
                    onSelect = { voucher ->
                        selectedVoucher = voucher
                        isShowPromoBottomSheet = false
                    }
                )
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