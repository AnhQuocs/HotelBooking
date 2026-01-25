package com.example.hotelbooking.features.booking.presentation.ui.history.detail

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.domain.model.CancelReason
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryState
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryViewModel
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingViewModel
import com.example.hotelbooking.features.main.BookingRefreshEvent
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.viewmodel.RoomState
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.PrimaryBlue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BookingDetailMainContent(
    modifier: Modifier = Modifier,
    currentState: BookingHistoryState<BookingWithHotel>,
    roomDetailState: RoomState<RoomType>,
    seconds: Int,
    bookingId: String,
    onRefreshRequest: () -> Unit,
    onBackClick: () -> Unit,
    bookingViewModel: BookingViewModel,
    bookingHistoryViewModel: BookingHistoryViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimen.PaddingM),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (currentState) {
            is BookingHistoryState.Idle, is BookingHistoryState.Loading -> {
                LoadingStateUI()
            }

            is BookingHistoryState.Success<*> -> {
                val data = currentState.data as BookingWithHotel

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        BookingDetailItem(
                            hotel = data.hotel,
                            booking = data.booking,
                            roomDetailState = roomDetailState,
                            seconds = seconds,
                            onTimeOut = {
                                if (data.booking.status == BookingStatus.PENDING) {
                                    handleTimeout(
                                        scope, bookingViewModel, bookingHistoryViewModel,
                                        bookingId, context, onRefreshRequest, onBackClick
                                    )
                                }
                            }
                        )
                    }
                }
            }

            is BookingHistoryState.Error -> {
                ErrorStateUI(
                    message = currentState.message,
                    onRetry = { bookingHistoryViewModel.loadBookingById(bookingId) }
                )
            }
        }
    }
}

@Composable
private fun LoadingStateUI() {
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

@Composable
private fun ErrorStateUI(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(Dimen.PaddingS))
        Text(text = message, textAlign = TextAlign.Center)
        TextButton(onClick = onRetry) {
            Text(stringResource(id = R.string.retry), color = PrimaryBlue)
        }
    }
}

private fun handleTimeout(
    scope: CoroutineScope,
    bookingViewModel: BookingViewModel,
    bookingHistoryViewModel: BookingHistoryViewModel,
    bookingId: String,
    context: Context,
    onRefreshRequest: () -> Unit,
    onBackClick: () -> Unit
) {
    scope.launch {
        bookingViewModel.onTimeout()
        val isCancelled = bookingHistoryViewModel.cancelBooking(
            bookingId = bookingId,
            reason = CancelReason.TIMEOUT
        )
        if (isCancelled) {
            BookingRefreshEvent.triggerRefresh()
            Toast.makeText(context, context.getString(R.string.payment_time_expired), Toast.LENGTH_LONG).show()
            onRefreshRequest()
            onBackClick()
        }
    }
}