package com.example.hotelbooking.features.room.presentation.ui.admin.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.room.domain.model.AdminAmenity
import com.example.hotelbooking.features.room.presentation.viewmodel.admin.AddRoomUiState
import com.example.hotelbooking.features.room.presentation.viewmodel.admin.AdminRoomViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.RoyalBlue
import com.example.hotelbooking.utils.LangUtils

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InventoryStep(
    state: AddRoomUiState,
    viewModel: AdminRoomViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.M)
    ) {
        Text(
            text = stringResource(id = R.string.room_amenities),
            style = AfacadTypography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = R.string.room_amenities_desc),
            style = AfacadTypography.labelSmall,
            color = Color.Gray
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.S),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
        ) {
            RoomAmenityProvider.getAll().forEach { amenity ->
                val isSelected = state.selectedAmenities.any { it.name["vi"] == amenity.name["vi"] }

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val newList = if (isSelected) {
                            state.selectedAmenities.filterNot { it.name["vi"] == amenity.name["vi"] }
                        } else {
                            state.selectedAmenities + amenity
                        }
                        viewModel.updateUiState { it.copy(selectedAmenities = newList) }
                    },
                    label = {
                        Text(
                            text = LangUtils.getLocalizedText(amenity.name),
                            style = AfacadTypography.labelMedium
                        )
                    },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, null, modifier = Modifier.size(Dimen.SizeS)) }
                    } else null,
                    shape = RoundedCornerShape(AppShape.ShapeS),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalBlue.copy(alpha = 0.1f),
                        selectedLabelColor = RoyalBlue,
                        selectedLeadingIconColor = RoyalBlue
                    )
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = AppSpacing.S), thickness = 0.5.dp)

        Text(
            text = stringResource(id = R.string.room_number_list),
            style = AfacadTypography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = R.string.room_number_desc),
            style = AfacadTypography.labelSmall,
            color = Color.Gray
        )

        AdminTextField(
            value = state.roomNumbersString,
            onValueChange = { text ->
                viewModel.updateUiState { it.copy(roomNumbersString = text) }
            },
            label = stringResource(id = R.string.room_number_label),
            placeholder = stringResource(id = R.string.room_number_placeholder),
            leadingIcon = { Icon(Icons.Default.Numbers, null, tint = RoyalBlue) }
        )

        val roomList = state.roomNumbersString.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (roomList.isNotEmpty()) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppSpacing.S),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.S),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
            ) {
                roomList.forEach { number ->
                    InputChip(
                        selected = true,
                        onClick = { },
                        label = { Text(number, style = AfacadTypography.labelSmall) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable {
                                        val updatedList = roomList.filter { it != number }
                                        viewModel.updateUiState {
                                            it.copy(roomNumbersString = updatedList.joinToString(", "))
                                        }
                                    }
                            )
                        },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = Color(0xFFF0F4FF),
                            selectedLabelColor = RoyalBlue
                        ),
                        border = BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.2f))
                    )
                }
            }

            Text(
                text = stringResource(id = R.string.admin_total_rooms, roomList.size),
                style = AfacadTypography.labelMedium,
                color = RoyalBlue,
                modifier = Modifier.padding(top = AppSpacing.XS)
            )
        }
    }
}

object RoomAmenityProvider {
    fun getAll(): List<AdminAmenity> {
        return listOf(
            AdminAmenity(
                name = mapOf("vi" to "Wifi miễn phí", "en" to "Free Wi-Fi"),
                iconUrl = "ic_wifi"
            ),
            AdminAmenity(
                name = mapOf("vi" to "Điều hòa", "en" to "Air Conditioning"),
                iconUrl = "ic_ac"
            ),
            AdminAmenity(name = mapOf("vi" to "Tivi", "en" to "Television"), iconUrl = "ic_tv"),
            AdminAmenity(
                name = mapOf("vi" to "Mini Bar", "en" to "Mini Bar"),
                iconUrl = "ic_minibar"
            ),
            AdminAmenity(
                name = mapOf("vi" to "Máy sấy tóc", "en" to "Hair Dryer"),
                iconUrl = "ic_hairdryer"
            ),
            AdminAmenity(
                name = mapOf("vi" to "Két sắt", "en" to "Safety Box"),
                iconUrl = "ic_safe"
            ),
            AdminAmenity(
                name = mapOf("vi" to "Bàn làm việc", "en" to "Work Desk"),
                iconUrl = "ic_desk"
            ),
            AdminAmenity(
                name = mapOf("vi" to "Ban công", "en" to "Balcony"),
                iconUrl = "ic_balcony"
            )
        )
    }
}