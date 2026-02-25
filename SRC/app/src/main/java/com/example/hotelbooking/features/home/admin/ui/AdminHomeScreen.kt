package com.example.hotelbooking.features.home.admin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.R
import com.example.hotelbooking.features.home.admin.viewmodel.AdminHomeViewModel
import com.example.hotelbooking.features.review.domain.model.Review
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.ArrivalBlue
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.BrightBlue
import com.example.hotelbooking.ui.theme.CancelledRed
import com.example.hotelbooking.ui.theme.HeaderBlue
import com.example.hotelbooking.ui.theme.RatingYellow
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    viewModel: AdminHomeViewModel = hiltViewModel(),
    onNavigateToCreateHotel: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val allHotels by viewModel.allManagedHotels.collectAsState()
    val currentHotel by viewModel.currentHotel.collectAsState()

    val revenue by viewModel.todayRevenue.collectAsState()
    val newBookings by viewModel.newBookingsCount.collectAsState()
    val occupied by viewModel.occupiedRoomsCount.collectAsState()
    val totalRooms by viewModel.totalRooms.collectAsState()
    val arrivals by viewModel.todayArrivals.collectAsState()
    val departures by viewModel.todayDepartures.collectAsState()
    val chartData by viewModel.revenueChartData.collectAsState()
    val reviews by viewModel.recentReviews.collectAsState()

    var isHotelMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier
                            .clickable { if (allHotels.size > 1) isHotelMenuExpanded = true }
                            .padding(end = Dimen.PaddingS)
                    ) {
                        Text(stringResource(id = R.string.dashboard), fontWeight = FontWeight.Bold)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentHotel?.name
                                    ?: stringResource(id = R.string.hotel_not_selected),
                                style = AfacadTypography.bodySmall,
                                color = Color.Gray
                            )
                            if (allHotels.size > 1) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(Dimen.SizeS),
                                    tint = Color.Gray
                                )
                            }
                        }
                    }

                    DropdownMenu(
                        expanded = isHotelMenuExpanded,
                        onDismissRequest = { isHotelMenuExpanded = false }
                    ) {
                        allHotels.forEach { hotel ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = hotel.name,
                                        fontWeight = if (hotel.id == currentHotel?.id) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    viewModel.switchHotel(hotel)
                                    isHotelMenuExpanded = false
                                },
                                leadingIcon = {
                                    if (hotel.id == currentHotel?.id) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = AvailableGreen
                                        )
                                    }
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (currentHotel == null) {
            EmptyDashboardState(onNavigateToCreateHotel)
        } else {
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
                            value = "$${String.format(Locale.US, "%,.0f", revenue)}",
                            icon = Icons.Default.AttachMoney,
                            iconColor = AvailableGreen,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardCard(
                            title = stringResource(id = R.string.new_booking),
                            value = "$newBookings",
                            icon = Icons.Default.BookOnline,
                            iconColor = BrightBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    DashboardCard(
                        title = stringResource(R.string.dashboard_occupancy_title),
                        value = stringResource(
                            R.string.dashboard_occupancy_value,
                            occupied,
                            totalRooms
                        ),
                        icon = Icons.Default.MeetingRoom,
                        iconColor = BrightBlue,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text(
                        text = stringResource(R.string.dashboard_today_activity),
                        style = AfacadTypography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OperationStatCard(
                            label = stringResource(R.string.dashboard_arrivals),
                            count = arrivals.size,
                            color = ArrivalBlue,
                            modifier = Modifier.weight(1f)
                        )
                        OperationStatCard(
                            label = stringResource(R.string.dashboard_departures),
                            count = departures.size,
                            color = CancelledRed,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.dashboard_revenue_chart_title),
                        style = AfacadTypography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    RevenueBarChart(data = chartData)
                }

                item {
                    Text(
                        text = stringResource(R.string.dashboard_recent_reviews),
                        style = AfacadTypography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (reviews.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.dashboard_no_reviews),
                            fontStyle = FontStyle.Italic,
                            color = Color.Gray
                        )
                    }
                } else {
                    items(reviews) { review ->
                        AdminReviewItem(review)
                    }
                }

                item { Spacer(modifier = Modifier.height(AppSpacing.L)) }
            }
        }
    }
}

@Composable
fun EmptyDashboardState(onCreateClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Apartment,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))
        Text(
            text = stringResource(R.string.empty_dashboard_title),
            style = AfacadTypography.titleMedium
        )
        Spacer(modifier = Modifier.height(AppSpacing.L))
        Button(onClick = onCreateClick) {
            Text(text = stringResource(R.string.empty_dashboard_action))
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = iconColor)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = AfacadTypography.bodySmall, color = Color.Gray)
            Text(value, style = AfacadTypography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OperationStatCard(label: String, count: Int, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(Dimen.PaddingM)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                count.toString(),
                style = AfacadTypography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(label, style = AfacadTypography.bodyMedium, color = color)
        }
    }
}

@Composable
fun RevenueBarChart(data: List<Pair<String, Double>>) {
    val maxVal = data.maxOfOrNull { it.second }?.takeIf { it > 0 } ?: 1.0

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.height(200.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimen.PaddingM),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            data.forEach { (date, amount) ->
                val heightRatio = (amount / maxVal).toFloat().coerceAtLeast(0.02f)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .width(20.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(heightRatio)
                                .background(
                                    HeaderBlue,
                                    RoundedCornerShape(
                                        topStart = AppShape.ShapeXXS,
                                        topEnd = AppShape.ShapeXXS
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(AppSpacing.XS))
                    Text(
                        text = date,
                        style = AfacadTypography.labelSmall,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AdminReviewItem(review: Review) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dimen.PaddingS)
    ) {
        Column(modifier = Modifier.padding(Dimen.PaddingSM)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(review.userName, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text("${review.rating}/5", color = RatingYellow, fontWeight = FontWeight.Bold)
            }
            Text(
                text = review.comment.ifBlank { stringResource(id = R.string.no_content) },
                style = AfacadTypography.bodySmall,
                color = Color.DarkGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}