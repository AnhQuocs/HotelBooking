package com.example.hotelbooking.features.booking.presentation.ui.history.stay

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.model.Guest
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.StayViewModel
import com.example.hotelbooking.features.main.BookingRefreshEvent
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.InputBackground
import com.example.hotelbooking.ui.theme.LightBlueBackground
import com.example.hotelbooking.ui.theme.Silver
import com.example.hotelbooking.ui.theme.SurfaceGray
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CheckInActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val bookingId = intent.getStringExtra("bookingId") ?: ""

        setContent {
            GuestCheckInScreen(
                bookingId = bookingId,
                onBackClick = { finish() },
                onSuccess = {
                    lifecycleScope.launch {
                        BookingRefreshEvent.triggerRefresh()
                    }
                    setResult(RESULT_OK)
                    finish()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestCheckInScreen(
    bookingId: String,
    stayViewModel: StayViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(bookingId) {
        stayViewModel.loadBookingData(bookingId)
    }

    val guestList by stayViewModel.guestListState.collectAsState()
    val isSubmitting by stayViewModel.isSubmitting.collectAsState()
    val currentBooking by stayViewModel.bookingState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    currentBooking?.let { booking ->
                        Column {
                            Text(
                                text = stringResource(id = R.string.check_in_guest_information),
                                style = AfacadTypography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = stringResource(
                                    id = R.string.people,
                                    booking.numberOfGuests,
                                    booking.roomNumber
                                ),
                                style = AfacadTypography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    } ?: Text(
                        stringResource(id = R.string.loading),
                        style = AfacadTypography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = null,
                            modifier = Modifier.size(Dimen.SizeSM)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        stayViewModel.submitCheckIn(
                            onSuccess = onSuccess,
                            onError = { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimen.PaddingM)
                        .height(50.dp),
                    shape = RoundedCornerShape(AppShape.ShapeM),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueNavy),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(Dimen.SizeM)
                        )
                    } else {
                        Text(
                            stringResource(id = R.string.confirm_check_in),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = Dimen.PaddingM),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.MediumLarge)
        ) {
            item {
                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))
                Card(
                    colors = CardDefaults.cardColors(containerColor = LightBlueBackground),
                    shape = RoundedCornerShape(AppShape.ShapeS),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(Dimen.PaddingSM), verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = BlueNavy,
                            modifier = Modifier.size(Dimen.SizeSM)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.stay_regulation_notice),
                            style = AfacadTypography.bodySmall,
                            color = BlueNavy
                        )
                    }
                }
            }

            itemsIndexed(guestList) { index, guest ->
                GuestInfoCard(
                    index = index + 1,
                    guest = guest,
                    onUpdate = { updatedGuest ->
                        stayViewModel.updateGuestInfo(index, updatedGuest)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestInfoCard(
    index: Int,
    guest: Guest,
    onUpdate: (Guest) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    fun showDatePicker() {
        val currentTimestamp = guest.dateOfBirth ?: Timestamp.now()
        calendar.time = currentTimestamp.toDate()

        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val newCalendar = Calendar.getInstance()
                newCalendar.set(year, month, dayOfMonth)
                onUpdate(guest.copy(dateOfBirth = Timestamp(newCalendar.time)))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    val dobString = remember(guest.dateOfBirth) {
        guest.dateOfBirth?.toDate()?.let {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
        } ?: ""
    }

    Card(
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(AppShape.ShapeM),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceGray)
    ) {
        Column(modifier = Modifier.padding(Dimen.PaddingM)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(Dimen.SizeL)
                        .background(
                            if (guest.isRepresentative) BlueNavy else Silver,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$index",
                        color = if (guest.isRepresentative) Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(AppSpacing.M))

                Column {
                    Text(
                        text = stringResource(
                            if (guest.isRepresentative)
                                R.string.guest_representative
                            else
                                R.string.guest_companion
                        ),
                        style = AfacadTypography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (guest.isRepresentative) BlueNavy else Color.Black
                    )

                    if (guest.isRepresentative) {
                        Text(
                            text = stringResource(R.string.guest_info_from_booking),
                            style = AfacadTypography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = Dimen.PaddingM),
                color = Color(0xFFF0F0F0)
            )


            OutlinedTextField(
                value = guest.fullName,
                onValueChange = { onUpdate(guest.copy(fullName = it)) },
                label = { Text(stringResource(id = R.string.full_name) + "*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !guest.isRepresentative,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = InputBackground,
                    disabledTextColor = Color.Black,
                    disabledBorderColor = Color.LightGray,
                    disabledLabelColor = BlueNavy
                ),
                shape = RoundedCornerShape(AppShape.ShapeM)
            )

            Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = dobString,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.date_of_birth_required)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    shape = RoundedCornerShape(AppShape.ShapeM),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker() }
                )
            }

            if (!guest.isRepresentative) {
                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))
                OutlinedTextField(
                    value = guest.email ?: "",
                    onValueChange = { onUpdate(guest.copy(email = it)) },
                    label = { Text(stringResource(R.string.email_optional)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(AppShape.ShapeM),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }
        }
    }
}