package com.example.hotelbooking.features.hotel.presentation.ui.admin.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.features.hotel.domain.model.CustomAmenity
import com.example.hotelbooking.features.hotel.presentation.ui.user.details.AmenityProvider
import com.example.hotelbooking.features.hotel.presentation.viewmodel.admin.AddHotelUiState
import com.example.hotelbooking.ui.theme.NearBlack
import com.example.hotelbooking.ui.theme.RoyalBlue
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UpdateDetailsScreen(
    uiState: AddHotelUiState,
    customAmenities: List<CustomAmenity> = emptyList(),
    onValueChange: (amenities: List<String>, checkIn: String, checkOut: String) -> Unit,
    onAddCustomAmenity: (CustomAmenity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showTimePicker by remember { mutableStateOf(false) }
    var isPickingCheckIn by remember { mutableStateOf(true) }
    var showAddAmenityDialog by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour = 14,
        initialMinute = 0,
        is24Hour = false
    )

    val timeFormatter = remember { DateTimeFormatter.ofPattern("h:mm a", Locale.US) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "Tiện ích khách sạn",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = NearBlack
            )
            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                AmenityProvider.catalog.forEach { amenityUi ->
                    // Quy ước: Lưu title tiếng Anh (titles[0]) vào Database làm key chuẩn
                    val amenityKey = amenityUi.titles[0]
                    val isSelected = uiState.amenities.contains(amenityKey)

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            val newList = if (isSelected) {
                                uiState.amenities - amenityKey
                            } else {
                                uiState.amenities + amenityKey
                            }
                            onValueChange(
                                newList,
                                uiState.checkInTime,
                                uiState.checkOutTime
                            )
                        },
                        label = {
                            Text(
                                // Hiển thị tiếng Việt trên UI nếu có (titles[1])
                                text = stringResource(id = amenityUi.titleRes),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = amenityUi.iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RoyalBlue.copy(alpha = 0.15f),
                            selectedLabelColor = RoyalBlue,
                            selectedLeadingIconColor = RoyalBlue
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) RoyalBlue else Color.LightGray
                        )
                    )
                }

                customAmenities.forEach { custom ->
                    val isSelected = uiState.amenities.contains(custom.nameEn)
                    val iconVector = CustomIconCatalog[custom.iconName] ?: Icons.Default.Star

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            val newList = if (isSelected) uiState.amenities - custom.nameEn else uiState.amenities + custom.nameEn
                            onValueChange(newList, uiState.checkInTime, uiState.checkOutTime)
                        },
                        label = { Text(text = custom.nameVi.ifBlank { custom.nameEn }, style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = { Icon(imageVector = iconVector, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RoyalBlue.copy(alpha = 0.15f), selectedLabelColor = RoyalBlue, selectedLeadingIconColor = RoyalBlue),
                        border = FilterChipDefaults.filterChipBorder(enabled = true, selected = isSelected, borderColor = if (isSelected) RoyalBlue else Color.LightGray)
                    )
                }

                AssistChip(
                    onClick = { showAddAmenityDialog = true },
                    label = { Text("Thêm tiện ích", color = NearBlack) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add",
                            tint = NearBlack,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color.Gray)
                )
            }
        }

        // --- SECTION 2: THỜI GIAN NHẬN/TRẢ PHÒNG ---
        item {
            Text(
                text = "Thời gian Nhận / Trả phòng",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = NearBlack
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Ô chọn Check-In
                OutlinedTextField(
                    value = uiState.checkInTime,
                    onValueChange = { },
                    readOnly = true,
                    enabled = false, // Vô hiệu hóa gõ tay
                    label = { Text("Giờ nhận phòng") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = NearBlack
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = NearBlack,
                        disabledBorderColor = NearBlack,
                        disabledLabelColor = NearBlack
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            isPickingCheckIn = true
                            showTimePicker = true
                        }
                )

                // Ô chọn Check-Out
                OutlinedTextField(
                    value = uiState.checkOutTime,
                    onValueChange = { },
                    readOnly = true,
                    enabled = false,
                    label = { Text("Giờ trả phòng") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = NearBlack
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = NearBlack,
                        disabledBorderColor = NearBlack,
                        disabledLabelColor = NearBlack
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            isPickingCheckIn = false
                            showTimePicker = true
                        }
                )
            }
        }
    }

    // --- HIỂN THỊ DIALOG CHỌN GIỜ ---
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    // Lấy giờ từ TimePicker chuyển sang dạng hh:mm a
                    val localTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    val formattedTime = localTime.format(timeFormatter)

                    if (isPickingCheckIn) {
                        onValueChange(
                            uiState.amenities,
                            formattedTime,
                            uiState.checkOutTime
                        )
                    } else {
                        onValueChange(
                            uiState.amenities,
                            uiState.checkInTime,
                            formattedTime
                        )
                    }
                    showTimePicker = false
                }) {
                    Text("Xác nhận", color = RoyalBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Hủy", color = Color.Gray)
                }
            },
            title = {
                Text(if (isPickingCheckIn) "Chọn giờ nhận phòng" else "Chọn giờ trả phòng")
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = timePickerState)
                }
            }
        )
    }

    if (showAddAmenityDialog) {
        AddCustomAmenityDialog(
            onDismiss = { showAddAmenityDialog = false },
            onSave = { nameEn, nameVi, iconName ->
                val newCustomAmenity = CustomAmenity(nameEn, nameVi, iconName)

                onAddCustomAmenity(newCustomAmenity)

                onValueChange(uiState.amenities + nameEn, uiState.checkInTime, uiState.checkOutTime)
                showAddAmenityDialog = false
            }
        )
    }
}