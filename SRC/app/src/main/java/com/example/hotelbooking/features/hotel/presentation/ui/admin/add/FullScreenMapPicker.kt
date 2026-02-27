package com.example.hotelbooking.features.hotel.presentation.ui.admin.add

import android.location.Geocoder
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun FullScreenMapPicker(
    initialLocation: LatLng,
    isEnglish: Boolean,
    onClose: () -> Unit,
    onLocationSelected: (LatLng, String, String) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // Khởi tạo 2 Geocoder để lấy cả Vi và En
    val geocoderVi = remember { Geocoder(context, Locale("vi", "VN")) }
    val geocoderEn = remember { Geocoder(context, Locale.US) }

    val labelLocating = if (isEnglish) "Locating position..." else "Đang xác định vị trí..."
    val labelNotFound = if (isEnglish) "Street name not found" else "Không tìm thấy tên đường"

    var localDisplayAddress by remember { mutableStateOf(labelLocating) }
    var tempShortVi by remember { mutableStateOf("") }
    var tempShortEn by remember { mutableStateOf("") }
    var isGeocoding by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 16f)
    }

    // Xử lý nút Back vật lý
    BackHandler { onClose() }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving) {
            isGeocoding = true
            localDisplayAddress = labelLocating
        } else {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            val center = cameraPositionState.position.target

            withContext(Dispatchers.IO) {
                try {
                    val viResults = geocoderVi.getFromLocation(center.latitude, center.longitude, 1)
                    val enResults = geocoderEn.getFromLocation(center.latitude, center.longitude, 1)

                    withContext(Dispatchers.Main) {
                        val viAddr = viResults?.firstOrNull()
                        val enAddr = enResults?.firstOrNull()

                        // 1. Vẫn giữ lại shortAddress (thoroughfare) để trả về khi bấm Confirm (Logic cũ)
                        tempShortVi = viAddr?.thoroughfare ?: ""
                        tempShortEn = enAddr?.thoroughfare ?: ""

                        // 2. Lấy Full Address để HIỂN THỊ LÊN UI cho giống ảnh 2
                        val fullVi = viAddr?.getAddressLine(0) ?: tempShortVi
                        val fullEn = enAddr?.getAddressLine(0) ?: tempShortEn

                        val displayRaw = if (isEnglish) fullEn else fullVi
                        localDisplayAddress = displayRaw.ifEmpty { labelNotFound }
                        isGeocoding = false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        localDisplayAddress = labelNotFound
                        isGeocoding = false
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        )

        // 1. Overlay Tọa độ (Chính giữa bên trên)
        val currentTarget = cameraPositionState.position.target
        Surface(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp),
            color = Color.Black.copy(alpha = 0.7f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = String.format(Locale.US, "%.6f , %.6f", currentTarget.latitude, currentTarget.longitude),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelSmall
            )
        }

        // 2. Nút Back (Góc trên bên trái)
        SmallFloatingActionButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
            containerColor = Color.White,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }

        // 3. Pin chính giữa (Đứng im)
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(24.dp)) // Bù khoảng cách để mũi nhọn của icon chạm đúng tâm
        }

        // 4. Card thông tin và nút Chốt vị trí (Bên dưới cùng)
        // 4. Card thông tin và nút Chốt vị trí (Bên dưới cùng)
        Card(
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    // Đổi label cho hợp với việc hiển thị full
                    text = if (isEnglish) "Selected Address" else "Địa chỉ đang chọn",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = localDisplayAddress, // Lúc này nó sẽ chứa Full Address
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    maxLines = 3, // Tăng lên 3 dòng để không bị cắt chữ
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Nút Chốt Vị Trí (Giữ nguyên logic, vẫn gửi tempShortVi đi)
                Button(
                    onClick = {
                        onLocationSelected(cameraPositionState.position.target, tempShortVi, tempShortEn)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !cameraPositionState.isMoving && !isGeocoding
                ) {
                    Text(if (isEnglish) "Confirm Location" else "Chốt vị trí này")
                }
            }
        }
    }
}