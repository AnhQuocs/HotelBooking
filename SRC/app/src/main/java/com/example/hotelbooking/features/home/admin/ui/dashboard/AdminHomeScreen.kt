package com.example.hotelbooking.features.home.admin.ui.dashboard

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.presentation.viewmodel.admin.AdminHomeViewModel
import com.example.hotelbooking.features.booking.presentation.viewmodel.admin.DashboardUiState
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.ArrivalBlue
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.BrightBlue
import com.example.hotelbooking.ui.theme.CancelledRed
import com.example.hotelbooking.ui.theme.NearBlack
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class BookingFilterType { ARRIVALS, DEPARTURES, NEW_BOOKINGS, OCCUPANCY, REVENUE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    viewModel: AdminHomeViewModel = hiltViewModel(), onNavigateToCreateHotel: () -> Unit
) {
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var isHotelMenuExpanded by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val currentState = uiState
        if (currentState is DashboardUiState.Success) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = currentState.selectedDate.atStartOfDay(ZoneId.systemDefault())
                    .toInstant().toEpochMilli()
            )
            DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        viewModel.updateSelectedDate(date)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }, dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }) {
                DatePicker(state = datePickerState)
            }
        }
    }

    when (val state = uiState) {
        is DashboardUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrightBlue)
            }
        }

        is DashboardUiState.NoHotels -> {
            EmptyDashboardState(onNavigateToCreateHotel)
        }

        is DashboardUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.message, color = CancelledRed)
            }
        }

        is DashboardUiState.Success -> {
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val dateLabel = if (state.selectedDate == LocalDate.now()) {
                stringResource(id = R.string.today)
            } else {
                state.selectedDate.format(dateFormatter)
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Column(
                                modifier = Modifier
                                    .clickable {
                                        if (state.allHotels.size > 1) isHotelMenuExpanded = true
                                    }
                                    .padding(end = Dimen.PaddingS)) {
                                Text(
                                    stringResource(id = R.string.dashboard),
                                    fontWeight = FontWeight.Bold,
                                    color = NearBlack
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = state.currentHotel.name,
                                        style = AfacadTypography.bodySmall,
                                        color = Color.Gray
                                    )
                                    if (state.allHotels.size > 1) {
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            null,
                                            modifier = Modifier.size(Dimen.SizeS),
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            }

                            DropdownMenu(
                                expanded = isHotelMenuExpanded,
                                onDismissRequest = { isHotelMenuExpanded = false }) {
                                state.allHotels.forEach { hotel ->
                                    DropdownMenuItem(text = {
                                        Text(
                                            text = hotel.name,
                                            fontWeight = if (hotel.id == state.currentHotel.id) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }, onClick = {
                                        viewModel.switchHotel(hotel)
                                        isHotelMenuExpanded = false
                                    }, leadingIcon = {
                                        if (hotel.id == state.currentHotel.id) {
                                            Icon(
                                                Icons.Default.Check, null, tint = AvailableGreen
                                            )
                                        }
                                    })
                                }
                            }
                        }, actions = {
                            Text(
                                text = dateLabel,
                                style = AfacadTypography.labelLarge,
                                color = BrightBlue,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, null, tint = NearBlack)
                            }
                        }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                    )
                }, containerColor = Color(0xFFF5F7FA)
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = Dimen.PaddingM),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.MediumLarge)
                ) {
                    item { Spacer(modifier = Modifier.height(AppSpacing.XS)) }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.M)) {
                            DashboardCard(
                                title = stringResource(id = R.string.today_revenue),
                                value = "$${
                                    String.format(
                                        Locale.US, "%,.0f", state.stats.todayRevenue
                                    )
                                }",
                                icon = Icons.Default.AttachMoney,
                                iconColor = AvailableGreen,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        navigateToBookingList(
                                            context, BookingFilterType.REVENUE, state
                                        )
                                    }
                            )
                            DashboardCard(
                                title = stringResource(id = R.string.new_booking),
                                value = "${state.stats.newBookingsCount}",
                                icon = Icons.Default.BookOnline,
                                iconColor = BrightBlue,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        navigateToBookingList(
                                            context, BookingFilterType.NEW_BOOKINGS, state
                                        )
                                    }
                            )
                        }
                    }

                    item {
                        DashboardCard(
                            title = stringResource(R.string.dashboard_occupancy_title),
                            value = stringResource(
                                R.string.dashboard_occupancy_value,
                                state.stats.occupiedCount,
                                state.stats.totalRooms
                            ),
                            icon = Icons.Default.MeetingRoom,
                            iconColor = BrightBlue,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navigateToBookingList(
                                        context, BookingFilterType.OCCUPANCY, state
                                    )
                                })
                    }

                    item {
                        Text(
                            stringResource(R.string.dashboard_today_activity),
                            style = AfacadTypography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NearBlack
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.M)) {
                            OperationStatCard(
                                label = stringResource(R.string.dashboard_arrivals),
                                count = state.stats.arrivalsCount,
                                color = ArrivalBlue,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        navigateToBookingList(
                                            context, BookingFilterType.ARRIVALS, state
                                        )
                                    })
                            OperationStatCard(
                                label = stringResource(R.string.dashboard_departures),
                                count = state.stats.departuresCount,
                                color = CancelledRed,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        navigateToBookingList(
                                            context, BookingFilterType.DEPARTURES, state
                                        )
                                    })
                        }
                    }

                    item {
                        Text(
                            stringResource(R.string.dashboard_revenue_chart_title),
                            style = AfacadTypography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NearBlack
                        )
                    }
                    item { RevenueBarChart(data = state.chartData) }

                    item {
                        Text(
                            stringResource(R.string.dashboard_recent_reviews),
                            style = AfacadTypography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NearBlack
                        )
                    }
                    if (state.recentReviews.isEmpty()) {
                        item {
                            Text(
                                stringResource(R.string.dashboard_no_reviews),
                                fontStyle = FontStyle.Italic,
                                color = Color.Gray
                            )
                        }
                    } else {
                        items(state.recentReviews) { review -> AdminReviewItem(review) }
                    }

                    item { Spacer(modifier = Modifier.height(AppSpacing.L)) }
                }
            }
        }
    }
}

private fun navigateToBookingList(
    context: Context, filterType: BookingFilterType, state: DashboardUiState.Success
) {
    val intent = Intent(context, AdminBookingListActivity::class.java).apply {
        putExtra("FILTER_TYPE", filterType.name)
        putExtra("HOTEL_ID", state.currentHotel.id)
        putExtra("TARGET_DATE", state.selectedDate.toEpochDay())
    }
    context.startActivity(intent)
}