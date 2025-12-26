package com.example.hotelbooking.features.booking.presentation.ui.book

import android.net.Uri
import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.model.Guest
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingUiState
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.BlueNavy
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    navController: NavController,
    roomId: String,
    startDate: LocalDate,
    endDate: LocalDate,
    hotelId: String,
    availableStock: Int,
    roomName: String,
    price: String,
    capacity: Int,
    bookingViewModel: BookingViewModel = hiltViewModel()
) {
    val uiState by bookingViewModel.uiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var ageStr by remember { mutableStateOf("") }
    var numberOfGuest by remember { mutableStateOf(1) }

    var isNameDirty by remember { mutableStateOf(false) }
    var isEmailDirty by remember { mutableStateOf(false) }
    var isPhoneDirty by remember { mutableStateOf(false) }

    val isNameValid = name.isNotBlank()
    val isPhoneValid = phone.isNotBlank()
    val isEmailValid = email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    val isFormValid = isNameValid && isPhoneValid && isEmailValid

    val pricePerNight = price.toDoubleOrNull() ?: 0.0
    val totalDays = ChronoUnit.DAYS.between(startDate, endDate)
    val totalPrice = totalDays * pricePerNight

    val timeoutSeconds: Long = 10 * 60L
//    val timeoutSeconds: Long = 30L

    LaunchedEffect(uiState) {
        if (uiState is BookingUiState.BookingSuccess) {
            val dateStr = "${startDate.dayOfMonth}-${endDate.dayOfMonth} ${
                startDate.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
            } ${startDate.year}"
            val encodedRoomName = Uri.encode(roomName)
            val encodedGuestName = Uri.encode(name)
            val bookingId = (uiState as BookingUiState.BookingSuccess).bookingId
            val timeoutSecondsInt: Int = timeoutSeconds.toInt()

            navController.navigate(
                "checkout?date=${Uri.encode(dateStr)}&hotelId=$hotelId&bookingId=$bookingId&roomName=$encodedRoomName&guestName=$encodedGuestName&numberOfGuest=$numberOfGuest&phone=$phone&totalPrice=$totalPrice&timeoutSecond=$timeoutSecondsInt"
            ) {
                popUpTo("booking_screen/{roomId}?start={start}&end={end}&hotelId={hotelId}&stock={stock}&roomName={roomName}&price={price}") {
                    inclusive = true
                }
            }
            bookingViewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.complete_your_booking)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
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
            Surface(shadowElevation = 16.dp, color = Color.White) {
                Button(
                    onClick = {
                        val guest = Guest(
                            name = name,
                            email = email,
                            phone = phone,
                            age = ageStr.toIntOrNull() ?: 18
                        )
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                        bookingViewModel.submitBooking(
                            hotelId = hotelId,
                            roomTypeId = roomId,
                            userId = userId,
                            startDate = startDate,
                            endDate = endDate,
                            guest = guest,
                            numberOfGuests = numberOfGuest,
                            pricePerNight = pricePerNight,
                            availableRooms = availableStock,
                            timeoutSeconds = timeoutSeconds
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimen.PaddingM)
                        .height(Dimen.HeightDefault + 2.dp),
                    shape = RoundedCornerShape(AppShape.ShapeL),
                    enabled = (uiState !is BookingUiState.Loading) && isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlueNavy,
                        contentColor = Color.White,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.White
                    )
                ) {
                    if (uiState is BookingUiState.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(Dimen.SizeM)
                        )
                    } else {
                        Text(
                            text = stringResource(
                                R.string.pay_now,
                                totalPrice
                            )
                        )
                    }
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
                .padding(16.dp)
        ) {
            BookingSummaryCard(
                roomName = roomName,
                startDate = startDate,
                endDate = endDate,
                totalDays = totalDays,
                totalPrice = totalPrice
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            GuestInformationSection(
                name = name,
                onNameChange = {
                    name = it
                    isNameDirty = true
                },
                isNameDirty = isNameDirty,
                isNameValid = isNameValid,
                email = email,
                onEmailChange = {
                    email = it
                    isEmailDirty = true
                },
                isEmailDirty = isEmailDirty,
                isEmailValid = isEmailValid,
                phone = phone,
                onPhoneChange = {
                    phone = it
                    isPhoneDirty = true
                },
                isPhoneDirty = isPhoneDirty,
                isPhoneValid = isPhoneValid,
                ageStr = ageStr,
                onAgeChange = { ageStr = it }
            )

            Spacer(modifier = Modifier.height(AppSpacing.XS))

            GuestCountSection(
                numberOfGuest = numberOfGuest,
                capacity = capacity,
                onValueChange = { numberOfGuest = it }
            )

            if (uiState is BookingUiState.Error) {
                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = (uiState as BookingUiState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(Dimen.PaddingM)
                    )
                }
            }
        }
    }
}