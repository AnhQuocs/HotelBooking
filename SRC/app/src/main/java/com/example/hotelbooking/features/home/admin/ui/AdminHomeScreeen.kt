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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.features.home.admin.viewmodel.AdminHomeViewModel
import com.example.hotelbooking.features.review.domain.model.Review

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
                            .padding(end = 8.dp)
                    ) {
                        Text("Dashboard", fontWeight = FontWeight.Bold)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentHotel?.name ?: "Chưa chọn khách sạn",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            if (allHotels.size > 1) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
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
                                            tint = Color(0xFF4CAF50)
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
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DashboardCard(
                            title = "Doanh thu hôm nay",
                            value = "$${String.format("%,.0f", revenue)}",
                            icon = Icons.Default.AttachMoney,
                            iconColor = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                        DashboardCard(
                            title = "Booking mới",
                            value = "$newBookings",
                            icon = Icons.Default.BookOnline,
                            iconColor = Color(0xFF2196F3),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    DashboardCard(
                        title = "Công suất phòng (Occupancy)",
                        value = "$occupied / $totalRooms phòng đang dùng",
                        icon = Icons.Default.MeetingRoom,
                        iconColor = Color(0xFFFF9800),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 2. OPERATIONS SECTION
                item {
                    Text(
                        "Hoạt động hôm nay",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OperationStatCard(
                            label = "Khách đến",
                            count = arrivals.size,
                            color = Color(0xFF1976D2), // Blue
                            modifier = Modifier.weight(1f)
                        )
                        OperationStatCard(
                            label = "Khách đi",
                            count = departures.size,
                            color = Color(0xFFD32F2F), // Red
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 3. CHART SECTION
                item {
                    Text(
                        "Biểu đồ doanh thu (7 ngày)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    RevenueBarChart(data = chartData)
                }

                // 4. REVIEWS SECTION
                item {
                    Text(
                        "Đánh giá gần đây",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (reviews.isEmpty()) {
                    item {
                        Text(
                            "Chưa có đánh giá nào.",
                            fontStyle = FontStyle.Italic,
                            color = Color.Gray
                        )
                    }
                } else {
                    items(reviews) { review ->
                        AdminReviewItem(review)
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
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
        Spacer(modifier = Modifier.height(16.dp))
        Text("Bạn chưa quản lý khách sạn nào", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onCreateClick) {
            Text("Thêm khách sạn đầu tiên")
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
            Text(title, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
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
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(label, style = MaterialTheme.typography.bodyMedium, color = color)
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
                .padding(16.dp),
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
                                    Color(0xFF0D47A1),
                                    RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = date,
                        style = MaterialTheme.typography.labelSmall,
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
            .padding(bottom = 8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(review.userName, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text("${review.rating}/5", color = Color(0xFFFFC107), fontWeight = FontWeight.Bold)
            }
            Text(
                text = review.comment.ifBlank { "Không có nội dung" },
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}