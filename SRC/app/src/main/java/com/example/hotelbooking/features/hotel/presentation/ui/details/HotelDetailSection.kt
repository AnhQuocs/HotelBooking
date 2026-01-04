package com.example.hotelbooking.features.hotel.presentation.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelState
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelViewModel
import com.example.hotelbooking.features.review.presentation.viewmodel.ReviewViewModel
import com.example.hotelbooking.features.room.presentation.viewmodel.RoomViewModel
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun HotelDetailSection(
    hotelId: String,
    onBackClick: () -> Unit,
    onRoomClick: (String) -> Unit,
    onChatClick: (String, String, String) -> Unit,
    hotelViewModel: HotelViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    reviewViewModel: ReviewViewModel = hiltViewModel()
) {
    val hotelDetailState by hotelViewModel.hotelDetailState.collectAsState()
    val roomState by roomViewModel.roomsState.collectAsState()
    val reviewState by reviewViewModel.reviewState.collectAsState()

    LaunchedEffect(Unit) {
        hotelViewModel.loadHotelById(hotelId)
        roomViewModel.loadRooms(hotelId)
        reviewViewModel.loadReviews(hotelId)
    }

    when (hotelDetailState) {
        is HotelState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }

        is HotelState.Success<*> -> {
            val hotel = (hotelDetailState as HotelState.Success<*>).data
            HotelDetailScreen(
                hotel as Hotel,
                roomState,
                reviewState,
                onBackClick,
                onRoomClick,
                onChatClick = { hotelId, hotelName, shortAddress ->
                    onChatClick(hotelId, hotelName, shortAddress)
                }
            )
        }

        is HotelState.Error -> {
            Text("Error: ${(hotelDetailState as HotelState.Error).message}")
        }
    }
}