package com.example.hotelbooking.features.hotel.presentation.ui.admin.detail

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.RoyalBlue

@Composable
fun ToggleStatusDialog(
    isCurrentlyActive: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isCurrentlyActive) "Tạm ngưng kinh doanh?" else "Mở bán khách sạn?") },
        text = {
            Text(
                if (isCurrentlyActive) "Khách sạn sẽ bị ẩn khỏi ứng dụng của khách hàng. Bạn có chắc chắn không?"
                else "Khách sạn sẽ hiển thị trở lại. Hãy đảm bảo thông tin và phòng ốc đã sẵn sàng."
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = if (isCurrentlyActive) Color.Red else AvailableGreen)
            ) { Text("Xác nhận") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy", color = Color.Gray) }
        }
    )
}

@Composable
fun NoRoomsWarningDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Không thể mở bán!") },
        text = { Text("Khách sạn này hiện chưa có loại phòng nào. Vui lòng thêm ít nhất một loại phòng trước khi kích hoạt trạng thái kinh doanh.") },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)) {
                Text("Đã hiểu")
            }
        }
    )
}