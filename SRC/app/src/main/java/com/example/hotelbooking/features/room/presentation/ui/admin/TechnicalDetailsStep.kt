package com.example.hotelbooking.features.room.presentation.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.SmokingRooms
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.features.room.presentation.viewmodel.admin.AddRoomUiState
import com.example.hotelbooking.features.room.presentation.viewmodel.admin.AdminRoomViewModel
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.RoyalBlue

@Composable
fun TechnicalDetailsStep(
    state: AddRoomUiState,
    viewModel: AdminRoomViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.M)
    ) {
        // --- PHẦN 1: THÔNG SỐ CƠ BẢN (Dùng Row để tiết kiệm không gian) ---
        Text(
            text = "Thông số cơ bản",
            style = AfacadTypography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.S)
        ) {
            // Sức chứa
            Box(modifier = Modifier.weight(1f)) {
                AdminTextField(
                    value = state.capacity,
                    onValueChange = { text ->
                        if (text.all { it.isDigit() }) {
                            viewModel.updateUiState { it.copy(capacity = text) }
                        }
                    },
                    label = "Sức chứa",
                    placeholder = "Người",
                    leadingIcon = { Icon(Icons.Default.Groups, null, tint = RoyalBlue) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // Diện tích
            Box(modifier = Modifier.weight(1f)) {
                AdminTextField(
                    value = state.roomSize,
                    onValueChange = { text ->
                        if (text.all { it.isDigit() }) {
                            viewModel.updateUiState { it.copy(roomSize = text) }
                        }
                    },
                    label = "Diện tích",
                    placeholder = "m²",
                    leadingIcon = { Icon(Icons.Default.SquareFoot, null, tint = RoyalBlue) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    suffix = { Text("m²", style = AfacadTypography.bodySmall) }
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = AppSpacing.S), thickness = 0.5.dp)

        // --- PHẦN 2: LOẠI GIƯỜNG & PHÒNG TẮM (Đa ngôn ngữ) ---
        Text(
            text = "Tiện nghi ngủ & tắm",
            style = AfacadTypography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // Bed Type (VI/EN)
        AdminTextField(
            value = state.bedTypeVi,
            onValueChange = { text -> viewModel.updateUiState { it.copy(bedTypeVi = text) } },
            label = "Loại giường (Tiếng Việt)",
            placeholder = "Ví dụ: 1 Giường đôi lớn",
            leadingIcon = { Icon(Icons.Default.Bed, null, tint = Color.Gray) }
        )
        AdminTextField(
            value = state.bedTypeEn,
            onValueChange = { text -> viewModel.updateUiState { it.copy(bedTypeEn = text) } },
            label = "Bed Type (English)",
            placeholder = "Ex: 1 Large Double Bed"
        )

        // Bathroom Type (VI/EN)
        AdminTextField(
            value = state.bathroomTypeVi,
            onValueChange = { text -> viewModel.updateUiState { it.copy(bathroomTypeVi = text) } },
            label = "Phòng tắm (Tiếng Việt)",
            placeholder = "Ví dụ: Bồn tắm & Vòi sen",
            leadingIcon = { Icon(Icons.Default.Bathtub, null, tint = Color.Gray) }
        )
        AdminTextField(
            value = state.bathroomTypeEn,
            onValueChange = { text -> viewModel.updateUiState { it.copy(bathroomTypeEn = text) } },
            label = "Bathroom Type (English)",
            placeholder = "Ex: Bathtub & Shower"
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = AppSpacing.S), thickness = 0.5.dp)

        // --- PHẦN 3: CHÍNH SÁCH PHÒNG (Dùng Switch cho chuyên nghiệp) ---
        Text(
            text = "Chính sách phòng",
            style = AfacadTypography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        PolicySwitchRow(
            label = "Cho phép hút thuốc",
            description = "Cho phép khách sử dụng thuốc lá trong phòng",
            checked = state.smokingPolicy,
            onCheckedChange = { checked ->
                viewModel.updateUiState { it.copy(smokingPolicy = checked) }
            },
            icon = Icons.Default.SmokingRooms
        )

        PolicySwitchRow(
            label = "Cho phép thú cưng",
            description = "Khách có thể mang theo vật nuôi",
            checked = state.petPolicy,
            onCheckedChange = { checked ->
                viewModel.updateUiState { it.copy(petPolicy = checked) }
            },
            icon = Icons.Default.Pets
        )
    }
}

// Composable phụ cho dòng Switch chính sách
@Composable
fun PolicySwitchRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.S),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (checked) RoyalBlue else Color.Gray,
            modifier = Modifier.size(24.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = AppSpacing.M)
        ) {
            Text(text = label, style = AfacadTypography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(text = description, style = AfacadTypography.labelSmall, color = Color.Gray)
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = RoyalBlue,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}