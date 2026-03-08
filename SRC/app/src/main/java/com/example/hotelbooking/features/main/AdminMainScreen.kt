package com.example.hotelbooking.features.main

import android.content.Intent
import android.util.Log
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
import com.example.hotelbooking.components.AdminBottomBar
import com.example.hotelbooking.features.chat.presentation.ui.admin.AdminChatActivity
import com.example.hotelbooking.features.chat.presentation.ui.admin.AdminMessageScreen
import com.example.hotelbooking.features.home.admin.ui.dashboard.AdminHomeScreen
import com.example.hotelbooking.features.hotel.presentation.ui.admin.MyHotelsScreen
import com.example.hotelbooking.features.hotel.presentation.ui.admin.add.AddHotelActivity
import com.example.hotelbooking.features.hotel.presentation.viewmodel.admin.AdminHotelViewModel
import com.example.hotelbooking.features.profile.ui.admin.AdminProfileScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AdminMainScreen(
    navController: NavController,
    adminHotelViewModel: AdminHotelViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by adminHotelViewModel.adminHotelState.collectAsState()

    LaunchedEffect(Unit) {
        FirebaseAuth.getInstance().currentUser?.uid?.let { adminId ->
            Log.d("LoadHotelsDebug", "Admin ID: $adminId")
            adminHotelViewModel.observeHotels(adminId)
        }
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var previousTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
            )
        },
        bottomBar = {
            AdminBottomBar(
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
            label = "AdminTabTransition",
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
                0 -> AdminHomeScreen(
                    onNavigateToCreateHotel = {
                        val intent = Intent(context, AddHotelActivity::class.java)
                        context.startActivity(intent)
                    }
                )

                1 -> {
                    MyHotelsScreen(state)
                }

                2 -> {
                    AdminMessageScreen(
                        onOpenChat = { chat, adminId ->
                            val intent = Intent(context, AdminChatActivity::class.java)
                                .putExtra("chatId", chat.chatId)
                                .putExtra("adminId", adminId)
                            context.startActivity(intent)
                        }
                    )
                }

                3 -> {
                    AdminProfileScreen(navController = navController)
                }
            }
        }
    }
}