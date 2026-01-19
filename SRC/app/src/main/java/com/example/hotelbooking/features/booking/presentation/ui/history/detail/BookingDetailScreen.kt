package com.example.hotelbooking.features.booking.presentation.ui.history.detail

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.domain.model.CancelReason
import com.example.hotelbooking.features.booking.presentation.ui.checkout.PaymentMethodBottomSheet
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryState
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryViewModel
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingUiState
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingViewModel
import com.example.hotelbooking.features.main.BookingRefreshEvent
import com.example.hotelbooking.features.room.presentation.viewmodel.RoomViewModel
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch

@Composable
fun BookingDetailScreen(
    onBackClick: () -> Unit,
    bookingHistoryViewModel: BookingHistoryViewModel = hiltViewModel(),
    bookingViewModel: BookingViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    bookingId: String,
    roomId: String,
    onRebook: () -> Unit,
    onRefreshRequest: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val bookingState by bookingViewModel.uiState.collectAsState()
    val roomDetailState by roomViewModel.roomDetailState.collectAsState()
    val bookingDetailState by bookingHistoryViewModel.bookingDetailState.collectAsState()

    val isStayProcessing by bookingHistoryViewModel.isProcessing.collectAsState()
    val isCancelling by bookingHistoryViewModel.isCancelling.collectAsState()
    val showOverlay = isStayProcessing || isCancelling

    var showDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val timeLeft by bookingHistoryViewModel.timeLeft.collectAsState()
    val seconds = (timeLeft % 60).toInt()

    val currentState = bookingDetailState

    DetailSideEffects(
        bookingId = bookingId,
        roomId = roomId,
        bookingState = bookingState,
        bookingDetailState = bookingDetailState,
        onBackClick = onBackClick,
        roomViewModel = roomViewModel,
        bookingHistoryViewModel = bookingHistoryViewModel,
        bookingViewModel = bookingViewModel,
        context = context
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                val bookingStatus = (currentState as? BookingHistoryState.Success<*>)
                    ?.let { (it.data as? BookingWithHotel)?.booking?.status }
                BookingDetailTopBar(
                    onBackClick = onBackClick,
                    bookingStatus = bookingStatus,
                    onCancelClick = { showDialog = true },
                    onRebookClick = { onRebook() }
                )
            },
            bottomBar = {
                if (currentState is BookingHistoryState.Success<*>) {
                    val data = currentState.data as BookingWithHotel
                    val hotel = data.hotel
                    val booking = data.booking

                    if (hotel != null) {
                        BookingDetailBottomBar(
                            booking = booking,
                            hotel = hotel,
                            isStayProcessing = isStayProcessing,
                            onShowBottomSheetClick = { showBottomSheet = true },
                            onAction = { status ->
                                bookingHistoryViewModel.updateStayStatus(
                                    booking.bookingId,
                                    status
                                )
                            },
                            onRebookClick = { onRebook() }
                        )
                    }
                }
            },
            containerColor = Color.White
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Dimen.PaddingM),
                contentAlignment = Alignment.TopCenter
            ) {
                when (currentState) {
                    is BookingHistoryState.Idle, is BookingHistoryState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(Dimen.SizeML),
                                color = PrimaryBlue
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.S))
                            Text(stringResource(id = R.string.booking_loading))
                        }
                    }

                    is BookingHistoryState.Success<*> -> {
                        val data = currentState.data as BookingWithHotel
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                BookingDetailItem(
                                    data.hotel,
                                    data.booking,
                                    roomDetailState,
                                    seconds,
                                    onTimeOut = {
                                        if (data.booking.status == BookingStatus.PENDING) {
                                            scope.launch {
                                                bookingViewModel.onTimeout()

                                                val isCancelled =
                                                    bookingHistoryViewModel.cancelBooking(
                                                        bookingId = bookingId,
                                                        reason = CancelReason.TIMEOUT
                                                    )

                                                if (isCancelled) {
                                                    BookingRefreshEvent.triggerRefresh()

                                                    Toast.makeText(
                                                        context,
                                                        context.getString(R.string.payment_time_expired),
                                                        Toast.LENGTH_LONG
                                                    ).show()

                                                    onRefreshRequest()

                                                    onBackClick()
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }

                    is BookingHistoryState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(Dimen.PaddingS))
                            Text(text = currentState.message, textAlign = TextAlign.Center)
                            TextButton(onClick = { bookingHistoryViewModel.loadBookingById(bookingId) }) {
                                Text(stringResource(id = R.string.retry), color = PrimaryBlue)
                            }
                        }
                    }
                }
            }
        }

        BookingProcessingOverlay(showOverlay, isCancelling)

        if (showBottomSheet) {
            if (currentState is BookingHistoryState.Success) {
                val data = currentState.data

                val title = stringResource(R.string.booking_success_title)
                val message = data.hotel?.let {
                    stringResource(
                        R.string.booking_success_message,
                        it.name,
                        bookingId
                    )
                }

                PaymentMethodBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    onNextClick = {
                        showBottomSheet = false
                        bookingViewModel.updateStatus(
                            bookingId = bookingId,
                            status = BookingStatus.CONFIRMED,
                            title = title,
                            message = message
                        )
                    }
                )
            }
        }

        if (currentState is BookingHistoryState.Success<*>) {
            val data = currentState.data as BookingWithHotel
            val hotel = data.hotel

            val title = stringResource(R.string.cancel_success_title)
            val message = hotel?.let {
                stringResource(R.string.cancel_success_message, it.name)
            }

            if (showDialog) {
                ConfirmCancelDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = {
                        scope.launch {
                            val isCancelled =
                                bookingHistoryViewModel.cancelBooking(
                                    bookingId = bookingId,
                                    reason = CancelReason.USER,
                                    title = title,
                                    message = message,
                                )
                            if (isCancelled) {
                                BookingRefreshEvent.triggerRefresh()
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.cancel_success),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        showDialog = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DetailSideEffects(
    bookingId: String,
    roomId: String,
    bookingState: BookingUiState,
    bookingDetailState: BookingHistoryState<BookingWithHotel>,
    onBackClick: () -> Unit,
    roomViewModel: RoomViewModel,
    bookingHistoryViewModel: BookingHistoryViewModel,
    bookingViewModel: BookingViewModel,
    context: Context
) {
    LaunchedEffect(roomId, bookingId) {
        bookingHistoryViewModel.loadBookingById(bookingId)
    }

    LaunchedEffect(bookingDetailState, roomId) {
        if (roomId.isNotEmpty()) {
            roomViewModel.loadRoomDetail(roomId)
        } else if (bookingDetailState is BookingHistoryState.Success<*>) {
            val bookingData = (bookingDetailState.data as? BookingWithHotel)
            bookingData?.booking?.roomTypeId?.let { roomViewModel.loadRoomDetail(it) }
        }
    }

    LaunchedEffect(bookingState) {
        if (bookingState is BookingUiState.BookingSuccess) {
            BookingRefreshEvent.triggerRefresh()
            Toast.makeText(context, context.getString(R.string.payment_complete), Toast.LENGTH_LONG)
                .show()
            bookingViewModel.resetState()
            onBackClick()
        }
    }

    LaunchedEffect(bookingDetailState) {
        if (bookingDetailState is BookingHistoryState.Error) {
            Toast.makeText(context, bookingDetailState.message, Toast.LENGTH_LONG).show()
        }
    }
}