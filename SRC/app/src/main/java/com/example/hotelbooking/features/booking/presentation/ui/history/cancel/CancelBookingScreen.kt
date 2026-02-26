package com.example.hotelbooking.features.booking.presentation.ui.history.cancel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppButton
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.CancelReason
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.BookingHistoryState
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.BookingHistoryViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch

@Composable
fun CancelBookingScreen(
    bookingId: String,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    bookingHistoryViewModel: BookingHistoryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val uiState by bookingHistoryViewModel.bookingDetailState.collectAsState()
    val currentState = uiState
    val isCancelling by bookingHistoryViewModel.isCancelling.collectAsState()

    var selectedReason by remember { mutableStateOf<CancelReason?>(null) }
    var otherReasonText by remember { mutableStateOf("") }

    val isConfirmEnabled =
        selectedReason != null &&
                (selectedReason != CancelReason.OTHER || otherReasonText.isNotBlank())

    LaunchedEffect(bookingId) {
        bookingHistoryViewModel.loadBookingById(bookingId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                AppTopBar(
                    text = stringResource(id = R.string.cancel_booking_title),
                    onBackClick = onBackClick
                )
            },
            bottomBar = {
                if (currentState is BookingHistoryState.Success) {
                    val booking = currentState.data.booking
                    val hotelName = currentState.data.hotel?.name
                    val buttonText = if (booking.status == BookingStatus.PENDING)
                        stringResource(id = R.string.cancel)
                    else stringResource(id = R.string.cancel_refund)

                    AppButton(
                        text = buttonText,
                        shape = AppShape.ShapeL,
                        color = PrimaryBlue,
                        enabled = isConfirmEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimen.PaddingM),
                        onClick = {
                            selectedReason?.let {
                                scope.launch {
                                    val isCancelled = bookingHistoryViewModel.cancelBooking(
                                        bookingId = bookingId,
                                        reason = it,
                                        cancelNote = otherReasonText,
                                        title = context.getString(R.string.cancel_success_title),
                                        message = context.getString(
                                            R.string.cancel_success_message,
                                            hotelName
                                        )
                                    )

                                    if (isCancelled) {
                                        onSuccess()
                                    }
                                }
                            }
                        }
                    )
                }
            },
            containerColor = Color.White
        ) {
            when (currentState) {
                is BookingHistoryState.Loading, BookingHistoryState.Idle -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }

                is BookingHistoryState.Success -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(Dimen.PaddingM)
                        ) {
                            CancelReasonSelector(
                                selectedReason = selectedReason,
                                otherText = otherReasonText,
                                onReasonSelected = { newValue -> selectedReason = newValue },
                                onOtherTextChanged = { newValue -> otherReasonText = newValue }
                            )
                        }
                    }
                }

                is BookingHistoryState.Error -> {
                    ErrorStateUI(currentState)
                }
            }
        }

        if (isCancelling) {
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

@Composable
fun CancelReasonSelector(
    selectedReason: CancelReason?,
    otherText: String,
    onReasonSelected: (CancelReason) -> Unit,
    onOtherTextChanged: (String) -> Unit
) {
    val reasons = CancelReason.entries.filter { it != CancelReason.TIMEOUT }

    Column(
        verticalArrangement = Arrangement.spacedBy(AppSpacing.S),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.cancel_reason_label),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = Dimen.PaddingS)
        )

        reasons.forEach { reason ->
            val isSelected = selectedReason == reason

            Surface(
                onClick = { onReasonSelected(reason) },
                shape = RoundedCornerShape(AppShape.ShapeM),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) PrimaryBlue else Color.LightGray.copy(alpha = 0.4f)
                ),
                color = if (isSelected) PrimaryBlue.copy(alpha = 0.05f) else Color.Transparent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(Dimen.PaddingM)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onReasonSelected(reason) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = PrimaryBlue,
                                unselectedColor = Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.width(AppSpacing.S))
                        Text(
                            text = stringResource(reason.toUserLabelRes()),
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isSelected) PrimaryBlue else Color.Black
                        )
                    }

                    if (reason == CancelReason.OTHER && isSelected) {
                        OutlinedTextField(
                            value = otherText,
                            onValueChange = onOtherTextChanged,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Dimen.PaddingS, horizontal = Dimen.PaddingSM),
                            shape = RoundedCornerShape(AppShape.ShapeM),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Color.LightGray,
                                cursorColor = PrimaryBlue
                            ),
                            maxLines = 3,
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorStateUI(state: BookingHistoryState.Error) {
    val message = state.fallbackMessage
        ?: stringResource(id = state.messageRes)

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
        Text(text = message, textAlign = TextAlign.Center)
    }
}