package com.example.hotelbooking.features.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.hotelbooking.components.UserBottomAppBar
import com.example.hotelbooking.features.booking.presentation.ui.history.BookingHistoryScreen
import com.example.hotelbooking.features.chat.MessageScreen
import com.example.hotelbooking.features.home.HomeScreen
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelViewModel
import com.example.hotelbooking.features.map.MapPreviewViewModel
import com.example.hotelbooking.features.profile.ProfileScreen

@Composable
fun MainScreen(
    navController: NavController,
    hotelViewModel: HotelViewModel = hiltViewModel(),
    mapPreviewViewModel: MapPreviewViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var previousTabIndex by remember { mutableIntStateOf(0) }

    val hotelState by hotelViewModel.hotelsState.collectAsState()
    val recommendedState by hotelViewModel.recommendedState.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        hotelViewModel.loadHotels()
        hotelViewModel.loadRecommendedHotels(4.7)
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
            )
        },
        bottomBar = {
            UserBottomAppBar(
                currentIndex = selectedTabIndex,
                onTabSelected = { newIndex ->
                    previousTabIndex = selectedTabIndex
                    selectedTabIndex = newIndex
                }
            )
        }
    ) { paddingValues ->
        val isForward = selectedTabIndex > previousTabIndex

        AnimatedContent(
            targetState = selectedTabIndex,
            label = "TabTransition",
            transitionSpec = {
                if (isForward) {
                    (slideInHorizontally(
                        initialOffsetX = { width -> width },
                        animationSpec = tween(durationMillis = 200)
                    ) + fadeIn(
                        animationSpec = tween(durationMillis = 200)
                    )).togetherWith(
                        slideOutHorizontally(
                            targetOffsetX = { width -> -width },
                            animationSpec = tween(durationMillis = 200)
                        )
                    )
                } else {
                    (slideInHorizontally(
                        initialOffsetX = { width -> -width },
                        animationSpec = tween(durationMillis = 200)
                    ) + fadeIn(
                        animationSpec = tween(durationMillis = 200)
                    )).togetherWith(
                        slideOutHorizontally(
                            targetOffsetX = { width -> width },
                            animationSpec = tween(durationMillis = 200)
                        )
                    )
                }.using(
                    SizeTransform(clip = false)
                )
            },
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) { tab ->
            when (tab) {
                0 -> HomeScreen(
                    cameraPositionState = mapPreviewViewModel.cameraPositionState,
                    hotelState = hotelState,
                    recommendedState = recommendedState,
//                    list = unreadCount
                )

                1 -> BookingHistoryScreen()

                2 -> MessageScreen()

                3 -> ProfileScreen()
            }
        }
    }
}