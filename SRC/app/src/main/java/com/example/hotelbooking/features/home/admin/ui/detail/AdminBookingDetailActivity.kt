package com.example.hotelbooking.features.home.admin.ui.detail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.presentation.viewmodel.admin.AdminBookingDetailState
import com.example.hotelbooking.features.booking.presentation.viewmodel.admin.AdminBookingDetailViewModel
import com.example.hotelbooking.features.home.admin.ui.dashboard.adminFormatTimestamp
import com.example.hotelbooking.features.room.presentation.viewmodel.RoomState
import com.example.hotelbooking.features.room.presentation.viewmodel.RoomViewModel
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.BrightBlue
import com.example.hotelbooking.ui.theme.NearBlack
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class AdminBookingDetailActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val bookingId = intent.getStringExtra("BOOKING_ID")

        if (bookingId.isNullOrBlank()) {
            Toast.makeText(
                this,
                getString(R.string.booking_id_not_found),
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        setContent {
            AdminBookingDetailScreen(
                bookingId = bookingId,
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingDetailScreen(
    bookingId: String,
    onBackClick: () -> Unit,
    roomViewModel: RoomViewModel = hiltViewModel(),
    adminBookingDetailViewModel: AdminBookingDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(bookingId) {
        adminBookingDetailViewModel.loadBookingDetails(bookingId)
    }

    val uiState by adminBookingDetailViewModel.uiState.collectAsState()
    val isProcessing by adminBookingDetailViewModel.isProcessing.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (uiState is AdminBookingDetailState.Success) {
                val booking = (uiState as AdminBookingDetailState.Success).bookingWithHotel.booking
                AdminActionBottomBar(booking, adminBookingDetailViewModel, isProcessing)
            }
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        if (isProcessing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
            )
        }

        when (val state = uiState) {
            is AdminBookingDetailState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is AdminBookingDetailState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = Color.Red)
                }
            }

            is AdminBookingDetailState.Success -> {
                val booking = state.bookingWithHotel.booking
                val hotel = state.bookingWithHotel.hotel

                val roomDetailState by roomViewModel.roomDetailState.collectAsState()

                LaunchedEffect(booking) {
                    roomViewModel.loadRoomDetail(roomId = booking.roomTypeId)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "ID: ${booking.bookingId}",
                                    style = AfacadTypography.labelMedium,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    hotel?.name ?: "",
                                    style = AfacadTypography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
                                ) {
                                    Text("Check-in: ${adminFormatTimestamp(booking.startDate)}")
                                    Text("Check-out: ${adminFormatTimestamp(booking.endDate)}")
                                }
                            }
                        }
                    }

                    when (val state = roomDetailState) {
                        is RoomState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(Dimen.HeightXL),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        is RoomState.Success -> {
                            item {
                                val room = state.data

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            "Room & Payment",
                                            style = AfacadTypography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                                        Text("Room Type: ${room.name}")
                                        Spacer(modifier = Modifier.height(AppSpacing.S))
                                        Text("Room Number: ${booking.roomNumber}")
                                        Spacer(modifier = Modifier.height(AppSpacing.S))
                                        Text(
                                            "Total Price: $${booking.totalPrice}",
                                            color = AvailableGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        is RoomState.Error -> {
                            item { Text(stringResource(id = R.string.error, state.message)) }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Guest Information (${booking.numberOfGuests})",
                                    style = AfacadTypography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Divider(modifier = Modifier.padding(vertical = 8.dp))

                                booking.guests.forEachIndexed { index, guest ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(Dimen.SizeL).align(Alignment.Top)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(AppSpacing.XS)
                                        ) {
                                            val displayName = StringBuilder("Guest ${index + 1}: ${guest.fullName}")
                                            if (guest.isRepresentative) {
                                                displayName.append(" (Representative)")
                                            }

                                            Text(
                                                text = displayName.toString(),
                                                fontWeight = if (guest.isRepresentative) FontWeight.Bold else FontWeight.Medium,
                                                color = if (guest.isRepresentative) BrightBlue else NearBlack
                                            )

                                            guest.dayOfBirth?.let {
                                                Text(
                                                    text = "Day Of Birth: ${formatDate(it)}",
                                                    style = AfacadTypography.bodySmall,
                                                    color = Color.Gray
                                                )
                                            }
                                            guest.email?.let {
                                                Text(
                                                    text = "Email: $it",
                                                    style = AfacadTypography.bodySmall,
                                                    color = Color.Gray
                                                )
                                            }
                                            guest.phone?.let {
                                                Text(
                                                    text = "Phone Number: $it",
                                                    style = AfacadTypography.bodySmall,
                                                    color = Color.Gray
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatDate(timestamp: Timestamp): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(timestamp.toDate())
}