package com.example.hotelbooking.features.booking.presentation.ui.history.detail

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.presentation.ui.checkout.PaymentMethodBottomSheet
import com.example.hotelbooking.features.booking.presentation.ui.history.cancel.CancelBookingActivity
import com.example.hotelbooking.features.booking.presentation.ui.history.stay.CheckInActivity
import com.example.hotelbooking.features.booking.presentation.ui.history.stay.StayCheckOutActivity
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.BookingHistoryState
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.BookingHistoryViewModel
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.BookingViewModel
import com.example.hotelbooking.features.main.BookingRefreshEvent
import com.example.hotelbooking.features.profile.feature.payment_card.domain.model.PaymentCard
import com.example.hotelbooking.features.profile.feature.payment_card.presentation.viewmodel.PaymentCardState
import com.example.hotelbooking.features.profile.feature.payment_card.presentation.viewmodel.PaymentCardViewModel
import com.example.hotelbooking.features.room.presentation.viewmodel.user.RoomViewModel
import com.example.hotelbooking.features.transaction.domain.model.Transaction
import com.example.hotelbooking.features.transaction.domain.model.TransactionStatus
import com.example.hotelbooking.features.transaction.presentation.viewmodel.TransactionAction
import com.example.hotelbooking.features.transaction.presentation.viewmodel.TransactionState
import com.example.hotelbooking.features.transaction.presentation.viewmodel.TransactionViewModel
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun BookingDetailScreen(
    onBackClick: () -> Unit,
    bookingHistoryViewModel: BookingHistoryViewModel = hiltViewModel(),
    bookingViewModel: BookingViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    paymentCardViewModel: PaymentCardViewModel = hiltViewModel(),
    bookingId: String,
    roomId: String,
    onRebook: () -> Unit,
    onRefreshRequest: () -> Unit
) {
    val context = LocalContext.current

    val roomDetailState by roomViewModel.roomDetailState.collectAsState()
    val bookingDetailState by bookingHistoryViewModel.bookingDetailState.collectAsState()
    val transactionActionState by transactionViewModel.actionState.collectAsState()
    val cardsState by paymentCardViewModel.cardsState.collectAsState()
    val createdId by transactionViewModel.createdTransactionId.collectAsState()

    val isStayProcessing by bookingHistoryViewModel.isProcessing.collectAsState()
    val isCancelling by bookingHistoryViewModel.isCancelling.collectAsState()

    val isGlobalLoading =
        isStayProcessing || isCancelling || transactionActionState is TransactionState.Loading

    var showBottomSheet by remember { mutableStateOf(false) }

    val timeLeft by bookingHistoryViewModel.timeLeft.collectAsState()
    val totalSeconds = timeLeft.toInt()

    val scope = rememberCoroutineScope()

    DetailSideEffects(
        bookingId = bookingId,
        roomId = roomId,
        paymentCardViewModel = paymentCardViewModel,
        transactionState = transactionActionState,
        bookingDetailState = bookingDetailState,
        roomViewModel = roomViewModel,
        bookingHistoryViewModel = bookingHistoryViewModel,
        transactionViewModel = transactionViewModel,
        context = context
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                val bookingStatus = (bookingDetailState as? BookingHistoryState.Success<*>)
                    ?.let { (it.data as? BookingWithHotel)?.booking?.status }
                val stayStatus = (bookingDetailState as? BookingHistoryState.Success<*>)
                    ?.let { (it.data as? BookingWithHotel)?.booking?.stayStatus }
                BookingDetailTopBar(
                    onBackClick = onBackClick,
                    bookingStatus = bookingStatus,
                    stayStatus = stayStatus,
                    onCancelClick = {
                        val intent = Intent(context, CancelBookingActivity::class.java)
                            .putExtra("bookingId", bookingId)
                        context.startActivity(intent)
                    },
                    onRebookClick = { onRebook() }
                )
            },
            bottomBar = {
                if (bookingDetailState is BookingHistoryState.Success<*>) {
                    val data = bookingDetailState as BookingHistoryState.Success<*>
                    val bookingWithHotel = data.data as BookingWithHotel

                    BookingDetailBottomBar(
                        booking = bookingWithHotel.booking,
                        hotel = bookingWithHotel.hotel!!,
                        isStayProcessing = isStayProcessing,
                        onShowBottomSheetClick = {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                            val now = System.currentTimeMillis()

                            val template = Transaction(
                                bookingId = bookingId,
                                userId = userId,
                                status = TransactionStatus.PENDING,
                                totalPrice = bookingWithHotel.booking.totalPrice,
                                amountPaid = bookingWithHotel.booking.totalPrice,
                                paymentMethod = null,
                                createdAt = now,
                                updatedAt = now,
                                refundedAt = null
                            )

                            transactionViewModel.prepareTransaction(template)
                            showBottomSheet = true
                        },
                        onCheckIn = {
                            val intent = Intent(context, CheckInActivity::class.java)
                                .putExtra("bookingId", bookingWithHotel.booking.bookingId)
                            context.startActivity(intent)
                        },
                        onCheckOut = {
                            scope.launch {
                                val success = bookingHistoryViewModel.processCheckOut(
                                    booking = bookingWithHotel.booking
                                )
                                if(success) {
                                    val intent = Intent(context, StayCheckOutActivity::class.java)
                                        .putExtra("hotelId", bookingWithHotel.hotel.id)
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.check_out_failed),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        },
                        onRebookClick = { onRebook() }
                    )
                }
            },
            containerColor = Color.White
        ) { paddingValues ->
            BookingDetailMainContent(
                modifier = Modifier.padding(paddingValues),
                currentState = bookingDetailState,
                roomDetailState = roomDetailState,
                seconds = totalSeconds,
                bookingId = bookingId,
                onRefreshRequest = onRefreshRequest,
                onBackClick = onBackClick,
                bookingViewModel = bookingViewModel,
                bookingHistoryViewModel = bookingHistoryViewModel
            )
        }

        if (isGlobalLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .pointerInput(Unit) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }

        if (showBottomSheet && bookingDetailState is BookingHistoryState.Success) {
            val hotelName = (bookingDetailState as BookingHistoryState.Success<BookingWithHotel>).data.hotel?.name ?: ""

            val cards = if (cardsState is PaymentCardState.Success) {
                (cardsState as PaymentCardState.Success<List<PaymentCard>>).data
            } else {
                emptyList()
            }

            PaymentMethodBottomSheet(
                cards = cards,
                onDismissRequest = { showBottomSheet = false },
                onAddCardClick = {
                    showBottomSheet = false
                    // Chuyển sang màn hình thêm thẻ
                },
                onNextClick = { brand ->
                    showBottomSheet = false
                    createdId?.let { txId ->
                        transactionViewModel.confirmPayment(
                            bookingId = bookingId,
                            transactionId = txId,
                            brand = brand,
                            title = context.getString(R.string.booking_success_title),
                            message = context.getString(R.string.booking_success_message, hotelName, bookingId)
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun DetailSideEffects(
    bookingId: String,
    roomId: String,
    paymentCardViewModel: PaymentCardViewModel,
    transactionState: TransactionState<TransactionAction>,
    bookingDetailState: BookingHistoryState<BookingWithHotel>,
    roomViewModel: RoomViewModel,
    bookingHistoryViewModel: BookingHistoryViewModel,
    transactionViewModel: TransactionViewModel,
    context: Context
) {
    val userId = remember { FirebaseAuth.getInstance().currentUser?.uid ?: ""}

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            paymentCardViewModel.loadPaymentCards(userId)
        }
    }

    LaunchedEffect(Unit) {
        BookingRefreshEvent.refreshTrigger.collect {
            bookingHistoryViewModel.loadBookingById(bookingId)
            transactionViewModel.recoverTransactionId(bookingId)
        }
    }

    LaunchedEffect(bookingId) {
        bookingHistoryViewModel.loadBookingById(bookingId)
        transactionViewModel.recoverTransactionId(bookingId)
    }

    LaunchedEffect(bookingDetailState, roomId) {
        if (roomId.isNotEmpty()) {
            roomViewModel.loadRoomDetail(roomId)
        } else if (bookingDetailState is BookingHistoryState.Success<*>) {
            val bookingData = (bookingDetailState.data as? BookingWithHotel)
            bookingData?.booking?.roomTypeId?.let { roomViewModel.loadRoomDetail(it) }
        }
    }

    LaunchedEffect(transactionState) {
        when (transactionState) {
            is TransactionState.Success -> {
                when (transactionState.data) {
                    TransactionAction.CONFIRM -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.payment_complete),
                            Toast.LENGTH_LONG
                        ).show()

                        BookingRefreshEvent.triggerRefresh()
                        bookingHistoryViewModel.loadBookingById(bookingId)

                        transactionViewModel.resetActionState()
                    }

                    TransactionAction.INITIALIZE -> {
                        Log.d(
                            "DetailSideEffects",
                            "Transaction ID ready: ${transactionViewModel.createdTransactionId.value}"
                        )
                    }

                    else -> {}
                }
            }

            is TransactionState.Error -> {
                Toast.makeText(context, transactionState.message, Toast.LENGTH_LONG).show()
                transactionViewModel.resetActionState()
            }

            else -> {}
        }
    }

    val errorMessage = when (bookingDetailState) {
        is BookingHistoryState.Error -> {
            bookingDetailState.fallbackMessage
                ?: stringResource(id = bookingDetailState.messageRes)
        }

        else -> null
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }
}