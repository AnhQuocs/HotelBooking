package com.example.hotelbooking.features.booking.presentation.ui.history.stay

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelState
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelViewModel
import com.example.hotelbooking.features.hotel.presentation.viewmodel.UpdateRatingState
import com.example.hotelbooking.features.hotel.presentation.viewmodel.UpdateRatingViewModel
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.RatingYellow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StayCheckOutActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val hotelId = intent.getStringExtra("hotelId") ?: ""

        setContent {
            val hotelViewModel: HotelViewModel = hiltViewModel()
            val hotelState by hotelViewModel.hotelDetailState.collectAsState()

            LaunchedEffect(hotelId) {
                hotelViewModel.loadHotelById(hotelId)
            }

            when(val currentState = hotelState) {
                is HotelState.Loading -> {

                }

                is HotelState.Success -> {
                    StayCheckOutScreen(
                        hotel = currentState.data,
                        onDismiss = { finish() },
                        onRatingSuccess = { finish() }
                    )
                }

                is HotelState.Error -> {

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StayCheckOutScreen(
    hotel: Hotel,
    updateRatingViewModel: UpdateRatingViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onRatingSuccess: () -> Unit
) {
    val updateState by updateRatingViewModel.updateRatingState.collectAsState()

    // State cho cả Rating và Comment
    var rating by remember { mutableDoubleStateOf(0.0) }
    var comment by remember { mutableStateOf("") }

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Xử lý sự kiện thành công
    LaunchedEffect(updateState) {
        if (updateState is UpdateRatingState.Success) {
            Toast.makeText(context, "Cảm ơn đánh giá của bạn!", Toast.LENGTH_SHORT).show()
            onRatingSuccess()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA), // Nền xám nhạt hiện đại
        topBar = {
            // Header đơn giản trong suốt
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, end = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                IconButton(
                    onClick = onDismiss,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()), // Cho phép cuộn nếu bàn phím che
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Đánh giá kỳ nghỉ",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = BlueNavy
            )

            Text(
                text = "Trải nghiệm của bạn tại đây thế nào?",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            // --- 1. HOTEL CARD INFO ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = hotel.thumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = hotel.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = hotel.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- 2. RATING SECTION ---
            InteractiveRatingBar(
                currentRating = rating,
                onRatingChanged = { rating = it }
            )

            // Text phản hồi cảm xúc
            AnimatedContent(
                targetState = rating,
                label = "RatingText",
                modifier = Modifier.padding(top = 16.dp)
            ) { r ->
                Text(
                    text = when (r.toInt()) {
                        1 -> "Rất tệ 😞"
                        2 -> "Tệ 😕"
                        3 -> "Bình thường 😐"
                        4 -> "Hài lòng 🙂"
                        5 -> "Tuyệt vời! 😍"
                        else -> "Chạm vào sao để đánh giá"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = if(r > 0) BlueNavy else Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- 3. COMMENT INPUT ---
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Chia sẻ thêm (Tùy chọn)") },
                placeholder = { Text("Phòng sạch sẽ, nhân viên thân thiện...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp), // TextArea cao
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = BlueNavy,
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                ),
                maxLines = 5,
                textStyle = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- 4. ACTION BUTTONS ---
            Button(
                onClick = {
                    keyboardController?.hide()
                    updateRatingViewModel.submitReview(
                        hotelId = hotel.id,
                        rating = rating,
                        comment = comment // Truyền comment vào ViewModel
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueNavy,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                ),
                enabled = rating > 0 && updateState !is UpdateRatingState.Loading
            ) {
                if (updateState is UpdateRatingState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Gửi đánh giá", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text("Bỏ qua", color = Color.Gray, fontWeight = FontWeight.Medium)
            }

            // Error Message
            if (updateState is UpdateRatingState.Error) {
                Text(
                    text = (updateState as UpdateRatingState.Error).message,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Spacer bottom để tránh bị sát đáy khi cuộn
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun InteractiveRatingBar(
    currentRating: Double,
    onRatingChanged: (Double) -> Unit,
    maxStars: Int = 5,
    starSize: Dp = 44.dp,
    starColor: Color = RatingYellow
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in 1..maxStars) {
            val isSelected = i <= currentRating
            // Animation: Nếu được chọn thì icon to hơn chút xíu
            val animatedSize by animateDpAsState(
                targetValue = if (isSelected) starSize else starSize * 0.9f,
                label = "StarSize"
            )

            Icon(
                imageVector = if (isSelected) Icons.Filled.Star else Icons.Rounded.StarBorder, // Dùng Rounded nhìn mềm hơn
                contentDescription = null,
                tint = if (isSelected) starColor else Color.Gray.copy(alpha = 0.3f),
                modifier = Modifier
                    .size(animatedSize)
                    .clip(CircleShape) // Để hiệu ứng ripple tròn đẹp
                    .clickable { onRatingChanged(i.toDouble()) }
                    .padding(2.dp)
            )
        }
    }
}