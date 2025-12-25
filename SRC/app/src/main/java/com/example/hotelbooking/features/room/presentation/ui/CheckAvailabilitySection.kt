package com.example.hotelbooking.features.room.presentation.ui
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.CheckCircle
//import androidx.compose.material.icons.filled.Error
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.tomn_test.features.booking.presentation.viewmodel.BookingUiState
//import java.time.LocalDate
//
//@Composable
//fun CheckAvailabilitySection(
//    uiState: BookingUiState,
//    startDate: LocalDate?,
//    endDate: LocalDate?,
//    onCheckClick: () -> Unit
//) {
//    val isValidDateRange = remember(startDate, endDate) {
//        startDate != null && endDate != null && !endDate.isBefore(startDate)
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Button(
//            onClick = onCheckClick,
//            modifier = Modifier.fillMaxWidth().height(50.dp),
//            shape = RoundedCornerShape(12.dp),
//            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A3A7A)),
//            enabled = isValidDateRange && uiState !is BookingUiState.Loading
//        ) {
//            if (uiState is BookingUiState.Loading) {
//                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
//                Spacer(modifier = Modifier.width(8.dp))
//                Text("Checking...", color = Color.White)
//            } else {
//                Text("Check", color = Color.White)
//            }
//        }
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        when (uiState) {
//            is BookingUiState.Available -> {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        "Rooms available! See total price below.",
//                        fontSize = 15.sp,
//                        color = Color(0xFF4CAF50),
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//            }
//            is BookingUiState.SoldOut -> {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red)
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        uiState.message,
//                        color = Color.Red,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//            }
//            is BookingUiState.Error -> {
//                Text(uiState.message, color = Color.Red)
//            }
//            else -> {}
//        }
//    }
//}