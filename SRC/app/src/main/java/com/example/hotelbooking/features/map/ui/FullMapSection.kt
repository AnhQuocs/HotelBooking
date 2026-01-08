package com.example.hotelbooking.features.map.ui

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelState
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.launch

private const val HOTEL_FOCUS_ZOOM = 16f
private const val HOTEL_OVERVIEW_ZOOM = 6f

@Composable
fun FullMapSection(
    uiState: HotelState<List<Hotel>>,
    onBookingClick: (String) -> Unit,
    onContactClick: (String, String, String) -> Unit,
    latLng: LatLng,
    hotelBitmap: Bitmap,
    cameraPositionState: CameraPositionState
) {
    var selectedHotel by remember { mutableStateOf<Hotel?>(null) }
    val coroutineScope = rememberCoroutineScope()

    when (uiState) {
        is HotelState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }

        is HotelState.Success -> {
            val hotels = uiState.data

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
            ) {
                GoogleMap(
                    cameraPositionState = cameraPositionState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Marker(
                        state = MarkerState(position = latLng),
                        title = stringResource(id = R.string.your_location),
                        snippet = null,
                        onClick = {
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(
                                        latLng,
                                        16f
                                    )
                                )
                            }

                            false
                        }
                    )

                    hotels.forEach { hotel ->
                        val pos = LatLng(hotel.latitude, hotel.longitude)
                        Marker(
                            state = MarkerState(position = pos),
                            title = hotel.name,
                            icon = BitmapDescriptorFactory.fromBitmap(hotelBitmap),
                            onClick = {
                                selectedHotel = hotel

                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(hotel.latitude, hotel.longitude),
                                            HOTEL_FOCUS_ZOOM
                                        )
                                    )
                                }

                                false
                            }
                        )
                    }
                }

                selectedHotel?.let { hotel ->
                    HotelInfoCard(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = Dimen.SizeXLPlus + 4.dp),
                        hotel = hotel,
                        onCloseClick = {
                            selectedHotel = null

                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(
                                            hotel.latitude,
                                            hotel.longitude
                                        ), HOTEL_OVERVIEW_ZOOM
                                    )
                                )
                            }
                        },
                        onBookingClick = { hotelId ->
                            onBookingClick(hotelId)
                        },
                        onContactClick = { hotelId, hotelName, shortAddress ->
                            onContactClick(hotelId, hotelName, shortAddress)
                        }
                    )
                }
            }
        }

        is HotelState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.cannot_load_hotels),
                    style = JostTypography.titleMedium
                )
            }
        }
    }
}