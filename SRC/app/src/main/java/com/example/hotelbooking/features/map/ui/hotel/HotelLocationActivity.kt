package com.example.hotelbooking.features.map.ui.hotel

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.features.chat.presentation.ui.user.ChatActivity
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.presentation.viewmodel.user.HotelState
import com.example.hotelbooking.features.hotel.presentation.viewmodel.user.HotelViewModel
import com.example.hotelbooking.features.map.ui.HotelInfo
import com.example.hotelbooking.features.map.ui.MapTopBar
import com.example.hotelbooking.features.map.util.bitmapFromVector
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HotelLocationActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val lat = intent.getDoubleExtra("lat", 0.0)
        val lng = intent.getDoubleExtra("lng", 0.0)
        val hotelId = intent.getStringExtra("hotelId") ?: ""

        setContent {
            val context = LocalContext.current
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            MapScreen(
                latLng = LatLng(lat, lng),
                hotelId = hotelId,
                context = context,
                onContactClick = { hotelId, hotelName, shortAddress ->
                    val intent = Intent(context, ChatActivity::class.java)
                        .putExtra("hotelId", hotelId)
                        .putExtra("hotelName", hotelName)
                        .putExtra("shortAddress", shortAddress)
                        .putExtra("userId", userId)
                    context.startActivity(intent)
                },
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
fun MapScreen(
    latLng: LatLng,
    hotelId: String,
    context: Context,
    onContactClick: (String, String, String) -> Unit,
    onBackClick: () -> Unit,
    hotelViewModel: HotelViewModel = hiltViewModel(),
) {
    val hotelDetailState by hotelViewModel.hotelDetailState.collectAsState()
    val hotelBitmap = remember(context) {
        bitmapFromVector(context, R.drawable.ic_hotel_marker_detail, 80)
    }

    LaunchedEffect(hotelId) {
        hotelViewModel.loadHotelById(hotelId)
    }

    MapSection(
        uiState = hotelDetailState,
        onContactClick = onContactClick,
        latLng = latLng,
        hotelBitmap = hotelBitmap,
        onBackClick = onBackClick
    )
}

@Composable
fun MapSection(
    uiState: HotelState<Hotel>,
    onContactClick: (String, String, String) -> Unit,
    latLng: LatLng,
    hotelBitmap: Bitmap,
    onBackClick: () -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 15f)
    }

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
            val hotel = uiState.data

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                GoogleMap(
                    modifier = Modifier
                        .fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = MarkerState(position = latLng),
                        icon = BitmapDescriptorFactory.fromBitmap(hotelBitmap),
                        title = stringResource(id = R.string.hotel_location)
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = Dimen.PaddingXXL)
                ) {
                    MapTopBar(
                        onBackClick = { onBackClick() },
                        text = stringResource(id = R.string.hotel_location),
                        modifier = Modifier
                            .fillMaxWidth()
                            .zIndex(1f)
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.M))

                    HotelLocationCard(
                        hotel = hotel,
                        onContactClick = onContactClick,
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
                    style = AfacadTypography.titleMedium
                )
            }
        }
    }
}

@Composable
fun HotelLocationCard(
    hotel: Hotel,
    onContactClick: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimen.HeightXL3 - 10.dp)
            .padding(Dimen.PaddingM),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimen.PaddingM)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
                ) {
                    AsyncImage(
                        model = hotel.thumbnailUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(0.25f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(AppShape.ShapeS)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(AppSpacing.S))

                    HotelInfo(hotel = hotel)
                }

                IconButton(
                    onClick = { onContactClick(hotel.id, hotel.name, hotel.shortAddress) },
                    modifier = Modifier
                        .border(1.dp, Color.LightGray, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_contact),
                        contentDescription = null,
                        modifier = Modifier.size(Dimen.SizeM)
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.L))
        }
    }
}