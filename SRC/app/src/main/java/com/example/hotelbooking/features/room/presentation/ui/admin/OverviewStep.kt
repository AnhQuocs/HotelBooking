package com.example.hotelbooking.features.room.presentation.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.features.room.presentation.viewmodel.admin.AddRoomUiState
import com.example.hotelbooking.features.room.presentation.viewmodel.admin.AdminRoomViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.RoyalBlue

@Composable
fun OverviewStep(
    state: AddRoomUiState,
    viewModel: AdminRoomViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.M)
    ) {
        // --- PHẦN 1: TÊN LOẠI PHÒNG ---
        Text(
            text = "Tên loại phòng",
            style = AfacadTypography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // Tên tiếng Việt
        AdminTextField(
            value = state.nameVi,
            onValueChange = { newName -> viewModel.updateUiState { it.copy(nameVi = newName) } },
            label = "Tên (Tiếng Việt)",
            placeholder = "Ví dụ: Phòng Deluxe Giường Đôi",
            leadingIcon = { Icon(Icons.Default.Title, null, tint = RoyalBlue) }
        )

        // Tên tiếng Anh
        AdminTextField(
            value = state.nameEn,
            onValueChange = { newName -> viewModel.updateUiState { it.copy(nameEn = newName) } },
            label = "Name (English)",
            placeholder = "Ex: Deluxe Double Room",
            leadingIcon = { Icon(Icons.Default.Translate, null, tint = Color.Gray) }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = AppSpacing.S), thickness = 0.5.dp)

        // --- PHẦN 2: MÔ TẢ ---
        Text(
            text = "Mô tả chi tiết",
            style = AfacadTypography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // Mô tả tiếng Việt
        AdminTextField(
            value = state.descriptionVi,
            onValueChange = { newDes -> viewModel.updateUiState { it.copy(descriptionVi = newDes) } },
            label = "Mô tả (Tiếng Việt)",
            placeholder = "Nhập mô tả về tiện nghi, view phòng...",
            singleLine = false,
            minLines = 3
        )

        // Mô tả tiếng Anh
        AdminTextField(
            value = state.descriptionEn,
            onValueChange = { newDes -> viewModel.updateUiState { it.copy(descriptionEn = newDes) } },
            label = "Description (English)",
            placeholder = "Enter room description, view...",
            singleLine = false,
            minLines = 3
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = AppSpacing.S), thickness = 0.5.dp)

        // --- PHẦN 3: GIÁ CẢ ---
        Text(
            text = "Giá phòng mỗi đêm",
            style = AfacadTypography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        AdminTextField(
            value = state.price,
            onValueChange = {
                // Chỉ cho phép nhập số
                if (it.all { char -> char.isDigit() }) {
                    viewModel.updateUiState { old -> old.copy(price = it) }
                }
            },
            label = "Giá (USD)",
            placeholder = "0.00",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = { Icon(Icons.Default.AttachMoney, null, tint = Color(0xFF4CAF50)) },
            suffix = { Text("/ đêm", style = AfacadTypography.bodySmall) }
        )
    }
}

// Hàm bổ trợ để tạo TextField đồng bộ cho Admin
@Composable
fun AdminTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label, style = AfacadTypography.bodySmall) },
        placeholder = {
            Text(
                placeholder,
                style = AfacadTypography.bodySmall,
                color = Color.LightGray
            )
        },
        leadingIcon = leadingIcon,
        suffix = suffix,
        singleLine = singleLine,
        minLines = minLines,
        shape = RoundedCornerShape(AppShape.ShapeM),
        keyboardOptions = keyboardOptions,
        textStyle = AfacadTypography.bodyMedium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = RoyalBlue,
            unfocusedBorderColor = Color.LightGray
        )
    )
}