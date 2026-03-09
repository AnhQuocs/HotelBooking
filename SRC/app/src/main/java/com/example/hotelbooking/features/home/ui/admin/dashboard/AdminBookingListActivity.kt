package com.example.hotelbooking.features.home.ui.admin.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.presentation.viewmodel.admin.AdminBookingDetailViewModel
import com.example.hotelbooking.features.booking.presentation.viewmodel.admin.AdminBookingListViewModel
import com.example.hotelbooking.features.home.ui.admin.detail.AdminBookingDetailActivity
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.BrightBlue
import com.example.hotelbooking.ui.theme.NearBlack
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.util.Locale

@AndroidEntryPoint
class AdminBookingListActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val filterString = intent.getStringExtra("FILTER_TYPE") ?: BookingFilterType.ARRIVALS.name
        val filterType = BookingFilterType.valueOf(filterString)
        val hotelId = intent.getStringExtra("HOTEL_ID") ?: return
        val targetDateEpoch = intent.getLongExtra("TARGET_DATE", LocalDate.now().toEpochDay())

        setContent {
            val viewModel: AdminBookingListViewModel = hiltViewModel()
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                viewModel.loadFilteredBookings(hotelId, filterType, targetDateEpoch)
            }

            AdminBookingListScreen(
                filterType = filterType,
                viewModel = viewModel,
                onNavigateBack = { finish() },
                onNavigateToDetail = { bookingId ->
                    val intent = Intent(context, AdminBookingDetailActivity::class.java)
                        .putExtra("BOOKING_ID", bookingId)
                    context.startActivity(intent)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingListScreen(
    filterType: BookingFilterType,
    viewModel: AdminBookingListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    adminBookingDetailViewModel: AdminBookingDetailViewModel = hiltViewModel()
) {
    val bookings by viewModel.bookings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val screenTitle = when (filterType) {
        BookingFilterType.ARRIVALS -> stringResource(R.string.dashboard_arrivals)
        BookingFilterType.DEPARTURES -> stringResource(R.string.dashboard_departures)
        BookingFilterType.NEW_BOOKINGS -> stringResource(R.string.new_booking)
        BookingFilterType.OCCUPANCY -> stringResource(R.string.dashboard_occupancy_title)
        BookingFilterType.REVENUE -> stringResource(R.string.today_revenue)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = screenTitle,
                        style = AfacadTypography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F7FA)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = BrightBlue
                )
            } else if (bookings.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.S))
                    Text(
                        text = stringResource(id = R.string.no_bookings_in_category),
                        style = AfacadTypography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Dimen.PaddingM),
                    verticalArrangement = Arrangement.spacedBy(Dimen.PaddingS)
                ) {
                    if (filterType == BookingFilterType.REVENUE) {
                        item {
                            val totalRevenue = bookings.sumOf { it.totalPrice }
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = AvailableGreen.copy(
                                        alpha = 0.1f
                                    )
                                ),
                                shape = RoundedCornerShape(AppShape.ShapeM)
                            ) {
                                Column(modifier = Modifier.padding(Dimen.PaddingM)) {
                                    Text(
                                        text = stringResource(id = R.string.total_revenue_today),
                                        style = AfacadTypography.bodyMedium,
                                        color = NearBlack
                                    )
                                    Text(
                                        text = "$${
                                            String.format(
                                                Locale.US,
                                                "%,.0f",
                                                totalRevenue
                                            )
                                        }",
                                        style = AfacadTypography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AvailableGreen
                                    )
                                }
                            }
                        }
                    }

                    items(bookings) { booking ->
                        AdminBookingItemCard(
                            booking = booking,
                            filterType = filterType,
                            onClick = { onNavigateToDetail(booking.bookingId) },
                            adminBookingDetailViewModel = adminBookingDetailViewModel
                        )
                    }
                    item { Spacer(modifier = Modifier.height(Dimen.PaddingL)) }
                }
            }
        }
    }
}