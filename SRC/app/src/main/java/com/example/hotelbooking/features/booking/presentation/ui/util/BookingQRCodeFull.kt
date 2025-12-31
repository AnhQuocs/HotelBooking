package com.example.hotelbooking.features.booking.presentation.ui.util

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingWithHotel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BookingQRCodeFull(bookingWithHotel: BookingWithHotel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val prettyData = createPrettyQRData(context, bookingWithHotel)

    val qrBitmap = remember(prettyData) {
        generateQRCodeBitmap(prettyData)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            bitmap = qrBitmap.asImageBitmap(),
            contentDescription = null,
            modifier = modifier.size(150.dp)
        )

        OutlinedButton(
            onClick = {
                val fileName = "QR_Booking_${bookingWithHotel.booking.bookingId}"
                saveBitmapToGallery(context, qrBitmap, fileName)
            },
            border = BorderStroke(1.dp, PrimaryBlue),
            shape = RoundedCornerShape(AppShape.ShapeXL),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = PrimaryBlue
            ),
            modifier = Modifier.width(145.dp)
        ) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(AppSpacing.S))
            Text(stringResource(id = R.string.download))
        }
    }
}

fun createPrettyQRData(
    context: Context,
    booking: BookingWithHotel
): String {
    val hotel = booking.hotel
        ?: return context.getString(R.string.qr_invalid)

    return buildString {
        appendLine("- " + context.getString(R.string.qr_hotel, hotel.name))
        appendLine("- " + context.getString(R.string.qr_address, hotel.shortAddress))
        appendLine("- " + context.getString(R.string.qr_price, hotel.pricePerNightMin))
        appendLine("- " + context.getString(R.string.qr_rating, hotel.averageRating))
        appendLine("- " + context.getString(
            R.string.qr_check_in,
            formatTimestamp(booking.booking.startDate.seconds)
        ))
        appendLine("- " + context.getString(
            R.string.qr_check_out,
            formatTimestamp(booking.booking.endDate.seconds)
        ))
        appendLine("- " + context.getString(
            R.string.qr_guests,
            booking.booking.numberOfGuests
        ))
    }
}

fun formatTimestamp(seconds: Long): String {
    val date = Date(seconds * 1000)
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(date)
}