package com.example.hotelbooking.features.home.admin.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.features.booking.domain.model.Booking
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.StayStatus
import com.example.hotelbooking.features.booking.presentation.viewmodel.admin.AdminBookingDetailViewModel
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.BrightBlue
import com.example.hotelbooking.ui.theme.CancelledRed
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun AdminActionBottomBar(
    booking: Booking,
    viewModel: AdminBookingDetailViewModel,
    isProcessing: Boolean
) {
    val zoneId = ZoneId.systemDefault()
    val now = LocalDate.now()
    val checkOutDate = booking.endDate.toDate().toInstant().atZone(zoneId).toLocalDate()

    val isEarlyCheckOut = now.isBefore(checkOutDate)

    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (booking.stayStatus == StayStatus.NONE && booking.status == BookingStatus.CONFIRMED) {
                OutlinedButton(
                    onClick = { viewModel.markAsNoShow(booking.bookingId) },
                    modifier = Modifier.weight(1f),
                    enabled = !isProcessing,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CancelledRed)
                ) {
                    Text("No-Show")
                }
            }

            else if (booking.stayStatus == StayStatus.CHECK_IN) {
                Button(
                    onClick = { viewModel.processCheckOut(booking) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isProcessing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEarlyCheckOut) Color(0xFFFFA000) else AvailableGreen
                    )
                ) {
                    Text(if (isEarlyCheckOut) "Early Check-Out" else "Confirm Check-Out")
                }
            }

            else {
                Text(
                    text = "Status: ${booking.stayStatus.name}",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}