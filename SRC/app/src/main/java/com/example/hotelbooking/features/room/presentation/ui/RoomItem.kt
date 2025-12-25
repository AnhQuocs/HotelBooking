package com.example.hotelbooking.features.room.presentation.ui

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun RoomInfoItem(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.Black.copy(alpha = 0.6f),
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun AmenityItem(
    iconUrl: String,
    text: String,
    context: Context
) {
    Row(
        modifier = Modifier
            .wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(iconUrl)
                .crossfade(true)
                .crossfade(200)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
fun PolicyItem(
    icon: ImageVector,
    text: String,
    color: Color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}
//
//@Composable
//fun BottomBookingBar(
//    pricePerNight: Int,
//    totalPrice: Long,
//    uiState: BookingUiState,
//    onBookNowClick: () -> Unit
//) {
//    Surface(
//        shadowElevation = 16.dp,
//        color = Color.White
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//                .navigationBarsPadding(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column {
//                if (uiState is BookingUiState.Available) {
//                    Text(
//                        text = "$$totalPrice",
//                        style = MaterialTheme.typography.headlineSmall,
//                        fontWeight = FontWeight.Bold,
//                        color = Color(0xFF4CAF50)
//                    )
//                    Text(
//                        text = "Total price",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = Color.Gray
//                    )
//                } else {
//                    Text(
//                        text = "$$pricePerNight",
//                        style = MaterialTheme.typography.headlineSmall,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text(
//                        text = "/ night",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = Color.Gray
//                    )
//                }
//            }
//
//            Button(
//                onClick = onBookNowClick,
//                shape = RoundedCornerShape(8.dp),
//                enabled = uiState is BookingUiState.Available,
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = if (uiState is BookingUiState.Available) Color(0xFF0A3A7A) else Color.Gray
//                )
//            ) {
//                Text(
//                    text = if (uiState is BookingUiState.SoldOut) "Sold Out" else "Book Now",
//                    color = Color.White,
//                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
//                )
//            }
//        }
//    }
//}

fun LocalDate.toMillis(): Long {
    return this.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
}

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
}

fun Long.toDisplayString(): String {
    return this.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}