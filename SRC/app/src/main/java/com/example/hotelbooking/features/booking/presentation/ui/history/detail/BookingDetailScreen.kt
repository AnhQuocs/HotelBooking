package com.example.hotelbooking.features.booking.presentation.ui.history.detail

import android.content.Context
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.domain.model.CancelReason
import com.example.hotelbooking.features.booking.presentation.ui.checkout.PaymentMethodBottomSheet
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryState
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryViewModel
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingViewModel
import com.example.hotelbooking.features.main.BookingRefreshEvent
import com.example.hotelbooking.features.room.presentation.viewmodel.RoomViewModel
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
    bookingId: String,
    roomId: String,
    onRebook: () -> Unit,
    onRefreshRequest: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val roomDetailState by roomViewModel.roomDetailState.collectAsState()
    val bookingDetailState by bookingHistoryViewModel.bookingDetailState.collectAsState()
    val transactionActionState by transactionViewModel.actionState.collectAsState()
    val createdId by transactionViewModel.createdTransactionId.collectAsState()

    val isStayProcessing by bookingHistoryViewModel.isProcessing.collectAsState()
    val isCancelling by bookingHistoryViewModel.isCancelling.collectAsState()

    val isGlobalLoading = isStayProcessing || isCancelling || transactionActionState is TransactionState.Loading

    var showDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val timeLeft by bookingHistoryViewModel.timeLeft.collectAsState()
    val seconds = (timeLeft % 60).toInt()

    DetailSideEffects(
        bookingId = bookingId,
        roomId = roomId,
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
                BookingDetailTopBar(
                    onBackClick = onBackClick,
                    bookingStatus = bookingStatus,
                    onCancelClick = { showDialog = true },
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
                                amountPaid = 0.0,
                                paymentMethod = null,
                                createdAt = now,
                                updatedAt = now,
                                refundedAt = null
                            )

                            transactionViewModel.prepareTransaction(template)
                            showBottomSheet = true
                        },
                        onAction = { status ->
                            bookingHistoryViewModel.updateStayStatus(bookingWithHotel.booking.bookingId, status)
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
                seconds = seconds,
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
            PaymentMethodBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                onNextClick = {
                    showBottomSheet = false

                    createdId?.let {
                        transactionViewModel.confirmPayment(
                            bookingId = bookingId,
                            transactionId = it,
                            title = context.getString(R.string.booking_success_title),
                            message = context.getString(R.string.booking_success_message, hotelName, bookingId)
                        )
                    }
                }
            )
        }

        if (showDialog && bookingDetailState is BookingHistoryState.Success) {
            val hotelName = (bookingDetailState as BookingHistoryState.Success<BookingWithHotel>).data.hotel?.name ?: ""
            ConfirmCancelDialog(
                onDismiss = { showDialog = false },
                onConfirm = {
                    showDialog = false
                    scope.launch {
                        val isCancelled = bookingHistoryViewModel.cancelBooking(
                            bookingId = bookingId,
                            reason = CancelReason.USER,
                            title = context.getString(R.string.cancel_success_title),
                            message = context.getString(R.string.cancel_success_message, hotelName)
                        )
                        if (isCancelled) BookingRefreshEvent.triggerRefresh()
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
    transactionState: TransactionState<TransactionAction>,
    bookingDetailState: BookingHistoryState<BookingWithHotel>,
    roomViewModel: RoomViewModel,
    bookingHistoryViewModel: BookingHistoryViewModel,
    transactionViewModel: TransactionViewModel,
    context: Context
) {
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
                        Toast.makeText(context, context.getString(R.string.payment_complete), Toast.LENGTH_LONG).show()

                        BookingRefreshEvent.triggerRefresh()
                        bookingHistoryViewModel.loadBookingById(bookingId)

                        transactionViewModel.resetActionState()
                    }
                    TransactionAction.INITIALIZE -> {
                        Log.d("DetailSideEffects", "Transaction ID ready: ${transactionViewModel.createdTransactionId.value}")
                    }
                    else -> {}
                }
            }
            is TransactionState.Error -> {
                Toast.makeText(context, transactionState.message, Toast.LENGTH_LONG).show()
                // Reset về Idle để User có thể nhấn thử lại
                transactionViewModel.resetActionState()
            }
            else -> {}
        }
    }

    LaunchedEffect(bookingDetailState) {
        if (bookingDetailState is BookingHistoryState.Error) {
            Toast.makeText(context, bookingDetailState.message, Toast.LENGTH_LONG).show()
        }
    }
}