package com.example.hotelbooking.features.hotel.presentation.ui.user

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.presentation.ui.user.details.HotelDetailActivity
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelState
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelViewModel
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.ErrorRed
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun AllHotelsScreen(
    onBackClick: () -> Unit,
    hotelViewModel: HotelViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by hotelViewModel.hotelsState.collectAsState()

    LaunchedEffect(Unit) {
        hotelViewModel.loadHotels()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                text = stringResource(id = R.string.most_popular), onBackClick = onBackClick
            )
        }, containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            AllHotelsSection(
                state = uiState,
                context = context,
                onClick = { hotelId ->
                    val intent = Intent(context, HotelDetailActivity::class.java)
                        .putExtra("hotelId", hotelId)
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun AllHotelsSection(state: HotelState<List<Hotel>>, context: Context, onClick: (String) -> Unit) {
    when (state) {
        is HotelState.Loading -> {
            CircularProgressIndicator(color = PrimaryBlue)
        }

        is HotelState.Success -> {
            val hotels = state.data

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.PaddingM),
                contentPadding = PaddingValues(0.dp),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
            ) {
                items(hotels, key = { it.id }) { hotel ->
                    AllHotelItem(hotel, context, onClick)
                }
            }
        }

        is HotelState.Error -> {
            Text(
                text = stringResource(id = R.string.error, state.message),
                color = ErrorRed
            )
        }
    }
}