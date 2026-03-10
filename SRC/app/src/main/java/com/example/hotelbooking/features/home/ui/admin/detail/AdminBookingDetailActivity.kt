package com.example.hotelbooking.features.home.ui.admin.detail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.presentation.viewmodel.admin.AdminBookingDetailState
import com.example.hotelbooking.features.booking.presentation.viewmodel.admin.AdminBookingDetailViewModel
import com.example.hotelbooking.features.room.presentation.viewmodel.user.RoomState
import com.example.hotelbooking.features.room.presentation.viewmodel.user.RoomViewModel
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.BlueNavy
import dagger.hilt.android.AndroidEntryPoint

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
                title = {
                    Text(
                        stringResource(id = R.string.booking_detail_title),
                        style = AfacadTypography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                modifier = Modifier.shadow(4.dp)
            )
        },
        bottomBar = {
            if (uiState is AdminBookingDetailState.Success) {
                val booking = (uiState as AdminBookingDetailState.Success).bookingWithHotel.booking
                AdminActionBottomBar(booking, adminBookingDetailViewModel, isProcessing)
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (isProcessing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = BlueNavy)
            }

            when (val state = uiState) {
                is AdminBookingDetailState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BlueNavy)
                    }
                }

                is AdminBookingDetailState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = Color.Red, textAlign = TextAlign.Center)
                    }
                }

                is AdminBookingDetailState.Success -> {
                    val booking = state.bookingWithHotel.booking
                    val hotel = state.bookingWithHotel.hotel
                    val roomDetailState by roomViewModel.roomDetailState.collectAsState()

                    LaunchedEffect(booking.roomTypeId) {
                        roomViewModel.loadRoomDetail(roomId = booking.roomTypeId)
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(Dimen.PaddingM),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.MediumLarge)
                    ) {
                        item {
                            AdminBookingSummaryCard(booking = booking, hotel = hotel)
                        }

                        item {
                            when (val rState = roomDetailState) {
                                is RoomState.Success -> AdminRoomPaymentCard(
                                    booking = booking,
                                    room = rState.data
                                )

                                is RoomState.Loading -> Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(Dimen.HeightXL),
                                    Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(Dimen.SizeM))
                                }

                                is RoomState.Error -> {
                                    Text(rState.message)
                                }
                            }
                        }

                        item {
                            AdminGuestInfoCard(booking = booking)
                        }

                        item { Spacer(modifier = Modifier.height(Dimen.PaddingXL)) }
                    }
                }
            }
        }
    }
}