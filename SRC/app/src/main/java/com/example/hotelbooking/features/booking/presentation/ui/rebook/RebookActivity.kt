package com.example.hotelbooking.features.booking.presentation.ui.rebook

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.features.booking.presentation.ui.checkout.PaymentCompleteScreen
import com.example.hotelbooking.features.booking.presentation.ui.history.toLocalDate
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.UpdateBookingState
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.UpdateBookingViewModel
import com.example.hotelbooking.features.main.BookingRefreshEvent
import com.example.hotelbooking.features.main.MainActivity
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@AndroidEntryPoint
class RebookActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val bookingId = intent.getStringExtra("bookingId") ?: ""

        setContent {
            val context = LocalContext.current

            val updateViewModel: UpdateBookingViewModel = hiltViewModel()
            val updateState by updateViewModel.updateState.collectAsState()

            val navController = rememberNavController()
            val start = "rebook_screen"

            NavHost(
                navController = navController, startDestination = start
            ) {
                composable("rebook_screen") {
                    LaunchedEffect(updateState) {
                        if (updateState is UpdateBookingState.Success) {
                            Toast.makeText(
                                context,
                                (updateState as UpdateBookingState.Success).message,
                                Toast.LENGTH_SHORT
                            ).show()
                            updateViewModel.resetState()
                            navController.navigate("payment_complete") {
                                popUpTo("rebook_screen") {
                                    inclusive = true
                                }
                            }
                        }
                    }

                    RebookScreen(
                        bookingId = bookingId,
                        onBackClick = { finish() },
                        onEditGuestClick = { booking, capacity ->
                            updateViewModel.selectedBooking = booking
                            navController.navigate("update_guest_info/$capacity")
                        },
                        updateBookingViewModel = updateViewModel,
                        onUpdateBooking = { booking, newStart, newEnd, newTotalPrice, roomSelected, brand ->
                            updateViewModel.confirmRebook(
                                currentBooking = booking,
                                newCheckIn = newStart,
                                newCheckOut = newEnd,
                                newTotalPrice = newTotalPrice,
                                roomSelected = roomSelected,
                                brand = brand
                            )
                        })
                }

                composable(
                    "update_guest_info/{capacity}",
                    arguments = listOf(navArgument("capacity") { type = NavType.IntType })
                ) { backStackEntry ->
                    val capacity = backStackEntry.arguments?.getInt("capacity") ?: 0
                    val booking = updateViewModel.selectedBooking

                    LaunchedEffect(updateState) {
                        if (updateState is UpdateBookingState.Success) {
                            Toast.makeText(
                                context,
                                (updateState as UpdateBookingState.Success).message,
                                Toast.LENGTH_SHORT
                            ).show()
                            updateViewModel.resetState()
                            navController.popBackStack()
                        }
                    }

                    if (booking != null) {
                        val mainGuest = booking.guests.firstOrNull { it.isRepresentative }
                        mainGuest?.let {
                            val dobLocalDate: LocalDate =
                                mainGuest.dateOfBirth?.toLocalDate() ?: LocalDate.now()

                            UpdateGuestInfoScreen(
                                name = it.fullName,
                                email = it.email ?: "",
                                phone = it.phone ?: "",
                                dob = dobLocalDate,
                                numberOfGuest = booking.numberOfGuests,
                                capacity = capacity,
                                isUpdating = updateState is UpdateBookingState.Loading,
                                onBackClick = { navController.popBackStack() },
                                onUpdate = { newName, newEmail, newPhone, newDob, newCount ->
                                    val dobTimestamp = Timestamp(
                                        Date(
                                            newDob.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                                .toEpochMilli()
                                        )
                                    )

                                    updateViewModel.updateGuestInfo(
                                        newName = newName,
                                        newEmail = newEmail,
                                        newPhone = newPhone,
                                        newDob = dobTimestamp,
                                        newNumberOfGuest = newCount
                                    )
                                })
                        }
                    }
                }

                composable("payment_complete") {
                    val scope = rememberCoroutineScope()

                    PaymentCompleteScreen(onBackClick = {
                        scope.launch {
                            BookingRefreshEvent.triggerRefresh()

                            val intent = Intent(this@RebookActivity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                    }, onHomeClick = {
                        scope.launch {
                            BookingRefreshEvent.triggerRefresh()

                            val intent = Intent(this@RebookActivity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                    })
                }
            }
        }
    }
}