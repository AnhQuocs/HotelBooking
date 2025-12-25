package com.example.hotelbooking.features.room.presentation.ui.detail

import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tomn_test.features.room.domain.model.RoomType
import com.example.tomn_test.features.room.presentation.viewmodel.RoomState
import com.example.tomn_test.features.room.presentation.viewmodel.RoomViewModel

@Composable
fun RoomDetailSection(
    roomId: String,
    roomViewModel: RoomViewModel = hiltViewModel(),
    navController: NavController,
    onBackClick: () -> Unit
) {
    val roomDetailState by roomViewModel.roomDetailState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(roomId) {
        roomViewModel.loadRoomDetail(roomId)
    }

    Log.d("RoomDetailSection", "ID: $roomId")

    when(roomDetailState) {
        is RoomState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is RoomState.Success -> {
            RoomDetailScreen(
                room = (roomDetailState as RoomState.Success<RoomType>).data,
                context = context,
                onBackClick = { onBackClick() },
                navController = navController
            )
        }

        is RoomState.Error -> {
            Text(text = "Error: ${(roomDetailState as RoomState.Error).message}")
        }
    }
}