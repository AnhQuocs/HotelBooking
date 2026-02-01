package com.example.hotelbooking.features.booking.presentation.ui.rebook

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.presentation.ui.checkout.CheckoutSummaryCard
import com.example.hotelbooking.features.booking.presentation.ui.checkout.HotelInfo
import com.example.hotelbooking.features.booking.presentation.ui.checkout.PromoUI
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingUiState
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingViewModel
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.ui.CheckAvailabilitySection
import com.example.hotelbooking.features.room.presentation.ui.DateSelectionSection
import com.example.hotelbooking.features.room.presentation.ui.toMillis
import com.example.hotelbooking.features.room.presentation.viewmodel.RoomState
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.PrimaryBlue
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun RebookBottomBar(
    pricePerNight: Int,
    startDate: LocalDate?,
    endDate: LocalDate?,
    uiState: BookingUiState,
    onBookClick: () -> Unit,
    onTotalPriceChange: (Long) -> Unit
) {
    val nights = remember(startDate, endDate) {
        if (startDate != null && endDate != null && endDate.isAfter(startDate)) {
            ChronoUnit.DAYS.between(startDate, endDate)
        } else {
            1L
        }
    }

    val nightText = if(nights.toInt() == 1) stringResource(id = R.string.nights) else stringResource(id = R.string.night)

    val totalPrice = pricePerNight * nights
    val isAvailable = uiState is BookingUiState.Available

    LaunchedEffect(totalPrice) {
        onTotalPriceChange(totalPrice)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(Dimen.PaddingM)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.total_price),
                    style = AfacadTypography.bodyMedium.copy(color = Color.Gray)
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$${totalPrice.toInt()}",
                        style = AfacadTypography.titleLarge.copy(
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )
                    Text(
                        text = "/$nightText",
                        style = AfacadTypography.bodySmall.copy(color = Color.Gray),
                        modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
                    )
                }
            }

            Button(
                onClick = onBookClick,
                enabled = isAvailable,
                modifier = Modifier
                    .width(150.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(AppShape.ShapeM),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueNavy,
                    disabledContainerColor = Color.LightGray
                )
            ) {
                Text(
                    text = stringResource(R.string.book_now),
                    style = AfacadTypography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun RebookContent(
    booking: Booking,
    hotel: Hotel?,
    room: RoomType,
    start: LocalDate,
    end: LocalDate,
    finalTotalPrice: Double,
    uiState: BookingUiState,
    roomState: RoomState<RoomType>,
    bookingViewModel: BookingViewModel,
    context: Context,
    onEditClick: (Int) -> Unit,
    selectedRoomNumber: String?,
    onRoomSelected: (String) -> Unit
) {
    val dateStr = "${start.dayOfMonth}-${end.dayOfMonth} ${
        start.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    } ${start.year}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = Dimen.PaddingM)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Dimen.PaddingM)) {
            hotel?.let { HotelInfo(hotel = it, context = context) }
            Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))
            CheckoutSummaryCard(
                date = dateStr,
                numberOfGuest = booking.numberOfGuests,
                guestName = booking.guest.name,
                roomName = room.name,
                phone = booking.guest.phone,
                totalPrice = "$${finalTotalPrice.toInt()}",
                isEdit = true,
                onEditClick = { onEditClick(room.capacity) }
            )

            Spacer(modifier = Modifier.height(AppSpacing.M))

            PromoUI()

            Spacer(modifier = Modifier.height(AppSpacing.M))

            RebookRoomSelectorSection(
                state = roomState,
                roomSelected = selectedRoomNumber ?: booking.roomNumber,
                onRoomSelected = onRoomSelected
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

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
                bookingViewModel.checkRoomAvailability(
                    hotelId = room.hotelId,
                    currentRoomType = room,
                    startDate = start,
                    endDate = end
                )
            }
        )
    }
}