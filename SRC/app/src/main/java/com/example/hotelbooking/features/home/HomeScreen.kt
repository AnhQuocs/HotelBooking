package com.example.hotelbooking.features.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.features.auth.presentation.viewmodel.AuthViewModel
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.presentation.ui.HotelSection
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelState

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    hotelState: HotelState<List<Hotel>>,
    recommendedState: HotelState<List<Hotel>>,
//    list: Int
) {
    val currentUser = authViewModel.currentUser.collectAsState()
    val user = currentUser.value
    val context = LocalContext.current

    Scaffold(
        topBar = {

        },
        containerColor = Color.White,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            item {
                HotelSection(
                    state = hotelState,
                    onClick = { hotelId ->

                    }
                )
            }
        }
    }
}