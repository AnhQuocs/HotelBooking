package com.example.hotelbooking.features.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.features.auth.presentation.viewmodel.AuthViewModel
import com.example.hotelbooking.features.home.search.ui.SearchActivity
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.presentation.ui.HotelSection
import com.example.hotelbooking.features.hotel.presentation.ui.details.HotelDetailActivity
import com.example.hotelbooking.features.hotel.presentation.ui.recommended.RecommendedSection
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelState
import com.example.hotelbooking.features.map.MapPreviewCard
import com.example.hotelbooking.features.notification.presentation.ui.NotificationActivity
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    cameraPositionState: CameraPositionState,
    hotelState: HotelState<List<Hotel>>,
    recommendedState: HotelState<List<Hotel>>,
    list: Int
) {
    val currentUser = authViewModel.currentUser.collectAsState()
    val user = currentUser.value
    val context = LocalContext.current

    val scrollState = rememberLazyListState()
    val hasScrolled by remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex > 0 || scrollState.firstVisibleItemScrollOffset > 0
        }
    }

    Scaffold(
        topBar = {
            Surface(
                tonalElevation = if (hasScrolled) 12.dp else 0.dp,
                shadowElevation = if (hasScrolled) 12.dp else 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .height(90.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    user?.let {
                        UserInfo(
                            user = user,
                            unreadCount = list,
                            onSearchClick = {
                                val intent = Intent(context, SearchActivity::class.java)
                                context.startActivity(intent)
                            },
                            onNotificationClick = {
                                val intent = Intent(context, NotificationActivity::class.java)
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        },
        containerColor = Color.White,
    ) { paddingValues ->
        LazyColumn(
            state = scrollState,
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
                    recommendedState = recommendedState,
                    onClick = { hotelId ->
                        val intent = Intent(context, HotelDetailActivity::class.java)
                            .putExtra("hotelId", hotelId)
                        context.startActivity(intent)
                    }
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