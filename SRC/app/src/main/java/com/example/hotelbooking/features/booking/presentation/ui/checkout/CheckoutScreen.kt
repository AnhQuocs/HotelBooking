package com.example.hotelbooking.features.booking.presentation.ui.checkout

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingUiState
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingViewModel
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelState
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.BlueNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    date: String,
    hotelId: String,
    bookingId: String,
    roomName: String,
    guestName: String,
    numberOfGuest: Int,
    phone: String,
    totalPrice: String,
    navController: NavController,
    hotelViewModel: HotelViewModel = hiltViewModel(),
    bookingViewModel: BookingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by hotelViewModel.hotelDetailState.collectAsState()
    val bookingState by bookingViewModel.uiState.collectAsState()

    var isShowBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(hotelId) {
        hotelViewModel.loadHotelById(hotelId)
    }

    LaunchedEffect(bookingState) {
        if (bookingState is BookingUiState.BookingSuccess) {
            navController.navigate("payment_complete") {
                popUpTo("checkout?date={date}&hotelId={hotelId}&bookingId={bookingId}&roomName={roomName}&guestName={guestName}&numberOfGuest={numberOfGuest}&phone={phone}&totalPrice={totalPrice}") {
                    inclusive = true
                }
            }

            bookingViewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.checkout)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black,
                        navigationIconContentColor = Color.Black
                    )
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimen.PaddingM),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Button(
                        onClick = { isShowBottomSheet = true },
                        modifier = Modifier
                            .width(150.dp)
                            .padding(Dimen.PaddingM)
                            .height(Dimen.HeightDefault + 2.dp),
                        shape = RoundedCornerShape(AppShape.ShapeL),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BlueNavy,
                            contentColor = Color.White
                        )
                    ) {
                        Text(stringResource(R.string.next))
                    }
                }
            },
            containerColor = Color.White
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimen.PaddingM)
            ) {
                CountdownTimer(
                    onTimeout = {
                        bookingViewModel.updateStatus(bookingId, BookingStatus.CANCELLED)
                        Toast.makeText(
                            context,
                            context.getString(R.string.payment_time_expired),
                            Toast.LENGTH_LONG
                        ).show()
                        navController.popBackStack()
                    }
                )

                Spacer(modifier = Modifier.height(AppSpacing.L))

                when (uiState) {
                    is HotelState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is HotelState.Success -> {
                        HotelInfo(
                            hotel = (uiState as HotelState.Success<Hotel>).data,
                            context = context
                        )
                    }

                    else -> {
                        Text(text = stringResource(R.string.error_loading_hotel_data))
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.L))

                CheckoutSummaryCard(
                    date = date,
                    numberOfGuest = numberOfGuest,
                    guestName = guestName,
                    roomName = roomName,
                    phone = phone,
                    totalPrice = totalPrice
                )
            }
        }
    }
}