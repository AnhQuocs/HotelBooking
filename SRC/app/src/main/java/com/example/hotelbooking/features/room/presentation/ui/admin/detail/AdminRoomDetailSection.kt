package com.example.hotelbooking.features.room.presentation.ui.admin.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.hotelbooking.features.room.presentation.viewmodel.admin.AdminRoomTypeViewModel
import com.example.hotelbooking.features.room.presentation.viewmodel.admin.RoomDetailState
import com.example.hotelbooking.ui.theme.ErrorRed
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun AdminRoomDetailSection(
    hotelId: String,
    state: RoomDetailState,
    onBackClick: () -> Unit,
    onEditClick: (String, String) -> Unit,
    viewModel: AdminRoomTypeViewModel
) {
    when (state) {
        is RoomDetailState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }

        is RoomDetailState.Success -> {
            AdminRoomDetailScreen(
                hotelId = hotelId,
                room = state.room,
                onBackClick = onBackClick,
                onEditClick = onEditClick,
                viewModel = viewModel
            )
        }

        is RoomDetailState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.message, color = ErrorRed)
            }
        }
    }
}