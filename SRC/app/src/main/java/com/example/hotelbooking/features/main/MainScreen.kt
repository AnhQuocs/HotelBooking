package com.example.hotelbooking.features.main

import android.content.Intent
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
import com.example.hotelbooking.features.booking.presentation.ui.history.BookingDetailActivity
import com.example.hotelbooking.features.booking.presentation.ui.history.BookingHistoryScreen
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryViewModel
import com.example.hotelbooking.features.chat.presentation.ui.ChatActivity
import com.example.hotelbooking.features.chat.presentation.ui.MessageScreen
import com.example.hotelbooking.features.home.HomeScreen
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelViewModel
import com.example.hotelbooking.features.map.MapPreviewViewModel
import com.example.hotelbooking.features.profile.ProfileScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainScreen(
    navController: NavController,
    hotelViewModel: HotelViewModel = hiltViewModel(),
    mapPreviewViewModel: MapPreviewViewModel = hiltViewModel(),
    bookingHistoryViewModel: BookingHistoryViewModel = hiltViewModel(),
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var previousTabIndex by remember { mutableIntStateOf(0) }

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val hotelState by hotelViewModel.hotelsState.collectAsState()
    val recommendedState by hotelViewModel.recommendedState.collectAsState()
    val bookingHistoryState by bookingHistoryViewModel.state.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        hotelViewModel.loadHotels()
        hotelViewModel.loadRecommendedHotels(4.7)
        bookingHistoryViewModel.loadMyBookings(userId)
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

                1 -> BookingHistoryScreen(
                    bookingHistoryState,
                    onDetailClick = { bookingId, roomId ->
                        val intent = Intent(context, BookingDetailActivity::class.java)
                            .putExtra("bookingId", bookingId)
                            .putExtra("roomId", roomId)
                        context.startActivity(intent)
                    }
                )

                2 -> MessageScreen(
                    userId = userId,
                    onOpenChat = { chat ->
                        val intent = Intent(context, ChatActivity::class.java)
                            .putExtra("hotelId", chat.hotelId)
                            .putExtra("hotelName", chat.hotelName)
                            .putExtra("shortAddress", chat.shortAddress)
                            .putExtra("userId", chat.userId)
                        context.startActivity(intent)
                    }
                )

                3 -> ProfileScreen()
            }
        }
    }
}