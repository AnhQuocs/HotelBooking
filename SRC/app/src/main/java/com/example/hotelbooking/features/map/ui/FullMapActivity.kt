package com.example.hotelbooking.features.map.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.features.chat.presentation.ui.ChatActivity
import com.example.hotelbooking.features.hotel.presentation.ui.user.details.HotelDetailActivity
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelViewModel
import com.example.hotelbooking.features.map.util.bitmapFromVector
import com.example.hotelbooking.ui.dimens.Dimen
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.rememberCameraPositionState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FullMapActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FullMapScreen(
                onBackClick = { finish() },
                latLng = LatLng(20.9611, 105.74746)
            )
        }
    }
}

@Composable
fun FullMapScreen(
    onBackClick: () -> Unit,
    latLng: LatLng,
    hotelViewModel: HotelViewModel = hiltViewModel()
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val uiState by hotelViewModel.hotelsState.collectAsState()
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 6f)
    }
    val hotelBitmap = remember {
        bitmapFromVector(context, R.drawable.ic_hotel_marker, 80)
    }

    LaunchedEffect(Unit) {
        hotelViewModel.loadHotels()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FullMapState(
            uiState = uiState,
            onBookingClick = {
                val intent = Intent(context, HotelDetailActivity::class.java)
                    .putExtra("hotelId",  it)
                context.startActivity(intent)
            },
            onContactClick = { hotelId, hotelName, shortAddress ->
                val intent = Intent(context, ChatActivity::class.java)
                    .putExtra("hotelId", hotelId)
                    .putExtra("hotelName", hotelName)
                    .putExtra("shortAddress", shortAddress)
                    .putExtra("userId", userId)
                context.startActivity(intent)
            },
            latLng = latLng,
            hotelBitmap = hotelBitmap,
            cameraPositionState = cameraPositionState
        )

        MapTopBar(
            onBackClick = { onBackClick() },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .zIndex(1f)
                .padding(top = Dimen.PaddingXXL)
        )
    }
}