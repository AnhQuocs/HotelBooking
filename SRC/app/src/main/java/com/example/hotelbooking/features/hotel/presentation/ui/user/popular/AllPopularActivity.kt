package com.example.hotelbooking.features.hotel.presentation.ui.user.popular

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.presentation.ui.user.AllHotelsScreen
import com.example.hotelbooking.features.hotel.presentation.viewmodel.user.HotelViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllPopularActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val hotelViewModel: HotelViewModel = hiltViewModel()
            val uiState by hotelViewModel.hotelsState.collectAsState()

            LaunchedEffect(Unit) {
                hotelViewModel.loadHotels()
            }

            AllHotelsScreen(
                title = stringResource(id = R.string.most_popular),
                state = uiState,
                onBackClick = { finish() })
        }
    }
}