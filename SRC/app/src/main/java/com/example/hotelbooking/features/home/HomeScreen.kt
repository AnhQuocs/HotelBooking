package com.example.hotelbooking.features.home

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.features.auth.presentation.viewmodel.AuthViewModel
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.presentation.ui.HotelSection
import com.example.hotelbooking.features.hotel.presentation.ui.details.HotelDetailActivity
import com.example.hotelbooking.features.hotel.presentation.ui.recommended.RecommendedSection
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelState
import com.example.hotelbooking.features.map.MapPreviewCard
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    cameraPositionState: CameraPositionState,
    hotelState: HotelState<List<Hotel>>,
    recommendedState: HotelState<List<Hotel>>,
//    list: Int
) {
    val currentUser = authViewModel.currentUser.collectAsState()
    val user = currentUser.value
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                user?.let {
                    UserInfo(user) {
//                        val intent = Intent(context, NotificationActivity::class.java)
//                        context.startActivity(intent)
                    }
                }
            }
        },
        containerColor = Color.White,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            item {
                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))
                HotelSection(
                    state = hotelState,
                    onClick = { hotelId ->
                        val intent = Intent(context, HotelDetailActivity::class.java)
                            .putExtra("hotelId", hotelId)
                        context.startActivity(intent)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))
            }

            item {
                RecommendedSection(
                    recommendedState = recommendedState
                )
            }

            item {
                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

                MapPreviewCard(
                    cameraPositionState = cameraPositionState,
                    modifier = Modifier.padding(16.dp),
                    location = LatLng(1.35, 103.87),
                    onClick = {
                        // Navigate sang MapScreen full
                    }
                )
            }
        }
    }
}