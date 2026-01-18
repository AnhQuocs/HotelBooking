package com.example.hotelbooking.features.booking.presentation.ui.history.detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.domain.model.CancelReason
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryState
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryViewModel
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
    roomViewModel: RoomViewModel = hiltViewModel(),
    bookingId: String,
    roomId: String,
    onRebook: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val roomDetailState by roomViewModel.roomDetailState.collectAsState()
    val bookingDetailState by bookingHistoryViewModel.bookingDetailState.collectAsState()

    val isStayProcessing by bookingHistoryViewModel.isProcessing.collectAsState()
    val isCancelling by bookingHistoryViewModel.isCancelling.collectAsState()
    val showOverlay = isStayProcessing || isCancelling

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(roomId, bookingId) {
        bookingHistoryViewModel.loadBookingById(bookingId)
    }

    LaunchedEffect(bookingDetailState, roomId) {
        if (roomId.isNotEmpty()) {
            roomViewModel.loadRoomDetail(roomId)
        } else if (bookingDetailState is BookingHistoryState.Success<*>) {
            val bookingData =
                (bookingDetailState as BookingHistoryState.Success<*>).data as? BookingWithHotel
            bookingData?.booking?.roomTypeId?.let { roomIdFromBooking ->
                roomViewModel.loadRoomDetail(roomIdFromBooking)
            }
        }
    }

    val currentState = bookingDetailState
    LaunchedEffect(currentState) {
        if (currentState is BookingHistoryState.Error) {
            Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
        }
    }

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
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shadowElevation = 8.dp,
                            color = Color.White
                        ) {
                            Box(modifier = Modifier.padding(Dimen.PaddingM)) {
                                BookingActions(
                                    booking = booking,
                                    hotel = hotel,
                                    isProcessing = isStayProcessing,
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
                            CircularProgressIndicator(modifier = Modifier.size(Dimen.SizeML), color = PrimaryBlue)
                            Spacer(modifier = Modifier.height(AppSpacing.S))
                            Text(stringResource(id = R.string.booking_loading))
                        }
                    }

                    is BookingHistoryState.Success<*> -> {
                        val data = currentState.data as BookingWithHotel
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                BookingDetailItem(data.hotel, data.booking, roomDetailState)
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

        if (showOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(Dimen.PaddingS))
                    Text(
                        text = if (isCancelling) stringResource(R.string.loading_cancelling) else stringResource(
                            R.string.loading_processing
                        ),
                        color = Color.White
                    )
                }
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