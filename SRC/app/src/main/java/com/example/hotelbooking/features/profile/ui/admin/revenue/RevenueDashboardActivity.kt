package com.example.hotelbooking.features.profile.ui.admin.revenue

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.presentation.viewmodel.admin.AdminHotelState
import com.example.hotelbooking.features.hotel.presentation.viewmodel.admin.AdminHotelViewModel
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.ErrorRed
import com.example.hotelbooking.ui.theme.PrimaryBlue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RevenueDashboardActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val adminId = intent.getStringExtra("adminId") ?: ""
        setContent {
            val adminHotelViewModel: AdminHotelViewModel = hiltViewModel()
            val hotelState by adminHotelViewModel.adminHotelState.collectAsState()

            LaunchedEffect(adminId) {
                adminHotelViewModel.observeHotels(adminId)
            }

            RevenueDashboardSection(
                onBackClick = { finish() },
                hotelState = hotelState
            )
        }
    }
}

@Composable
fun RevenueDashboardSection(
    onBackClick: () -> Unit,
    hotelState: AdminHotelState<List<Hotel>>
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        when (hotelState) {
            is AdminHotelState.Loading -> {
                CircularProgressIndicator(color = PrimaryBlue)
            }

            is AdminHotelState.Success -> {
                val list = hotelState.data

                RevenueDashboardScreen(
                    managedHotels = list,
                    onBackClick = onBackClick
                )
            }

            is AdminHotelState.Error -> {
                Text(
                    text = hotelState.message,
                    style = AfacadTypography.bodyMedium,
                    color = ErrorRed
                )
            }
        }
    }
}