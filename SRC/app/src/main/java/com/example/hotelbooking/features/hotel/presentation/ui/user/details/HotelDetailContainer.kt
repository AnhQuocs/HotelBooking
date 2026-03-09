package com.example.hotelbooking.features.hotel.presentation.ui.user.details

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
import com.example.hotelbooking.features.hotel.presentation.viewmodel.user.HotelState
import com.example.hotelbooking.features.hotel.presentation.viewmodel.user.HotelViewModel
import com.example.hotelbooking.features.review.presentation.viewmodel.ReviewViewModel
import com.example.hotelbooking.features.room.presentation.viewmodel.user.RoomViewModel
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun HotelDetailContainer(
    hotelId: String,
    onOpenMap: (Double, Double) -> Unit,
    onBackClick: () -> Unit,
    onRoomClick: (String, String) -> Unit,
    onChatClick: (String, String, String, String) -> Unit,
    hotelViewModel: HotelViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    reviewViewModel: ReviewViewModel = hiltViewModel()
) {
    val hotelDetailState by hotelViewModel.hotelDetailState.collectAsState()
    val roomState by roomViewModel.roomsState.collectAsState()
    val reviewState by reviewViewModel.reviewState.collectAsState()

    LaunchedEffect(hotelId) {
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
            val hotel = (hotelDetailState as HotelState.Success<Hotel>).data

            HotelDetailScreen(
                hotel = hotel,
                roomState = roomState,
                reviewState = reviewState,
                onOpenMap = { lat, lgn ->
                    onOpenMap(lat, lgn)
                },
                onBackClick = onBackClick,
                onRoomClick = onRoomClick,
                onChatClick = { hotelId, adminId, hotelName, shortAddress ->
                    onChatClick(hotelId, adminId, hotelName, shortAddress)
                }
            )
        }

        is HotelState.Error -> {
            Text("Error: ${(hotelDetailState as HotelState.Error).message}")
        }
    }
}