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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.room.presentation.viewmodel.admin.AddRoomUiState
import com.example.hotelbooking.features.room.presentation.viewmodel.admin.AdminRoomViewModel
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
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
        Text(
            text = stringResource(R.string.room_basic_specs),
            style = AfacadTypography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.S)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                AdminTextField(
                    value = state.capacity,
                    onValueChange = { text ->
                        if (text.all { it.isDigit() }) {
                            viewModel.updateUiState { it.copy(capacity = text) }
                        }
                    },
                    label = stringResource(R.string.room_capacity_label),
                    placeholder = stringResource(R.string.room_capacity_placeholder),
                    leadingIcon = { Icon(Icons.Default.Groups, null, tint = RoyalBlue) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                AdminTextField(
                    value = state.roomSize,
                    onValueChange = { text ->
                        if (text.all { it.isDigit() }) {
                            viewModel.updateUiState { it.copy(roomSize = text) }
                        }
                    },
                    label = stringResource(R.string.room_size_label),
                    placeholder = stringResource(R.string.room_size_placeholder),
                    leadingIcon = { Icon(Icons.Default.SquareFoot, null, tint = RoyalBlue) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    suffix = {
                        Text(
                            stringResource(R.string.room_size_suffix),
                            style = AfacadTypography.bodySmall
                        )
                    }
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = AppSpacing.S),
            thickness = 0.5.dp
        )

        Text(
            text = stringResource(R.string.sleep_bath_amenities),
            style = AfacadTypography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        AdminTextField(
            value = state.bedTypeVi,
            onValueChange = { text ->
                viewModel.updateUiState { it.copy(bedTypeVi = text) }
            },
            label = stringResource(R.string.bed_type_vi_label),
            placeholder = stringResource(R.string.bed_type_vi_placeholder),
            leadingIcon = { Icon(Icons.Default.Bed, null, tint = Color.Gray) }
        )

        AdminTextField(
            value = state.bedTypeEn,
            onValueChange = { text ->
                viewModel.updateUiState { it.copy(bedTypeEn = text) }
            },
            label = stringResource(R.string.bed_type_en_label),
            placeholder = stringResource(R.string.bed_type_en_placeholder)
        )

        AdminTextField(
            value = state.bathroomTypeVi,
            onValueChange = { text ->
                viewModel.updateUiState { it.copy(bathroomTypeVi = text) }
            },
            label = stringResource(R.string.bathroom_type_vi_label),
            placeholder = stringResource(R.string.bathroom_type_vi_placeholder),
            leadingIcon = { Icon(Icons.Default.Bathtub, null, tint = Color.Gray) }
        )

        AdminTextField(
            value = state.bathroomTypeEn,
            onValueChange = { text ->
                viewModel.updateUiState { it.copy(bathroomTypeEn = text) }
            },
            label = stringResource(R.string.bathroom_type_en_label),
            placeholder = stringResource(R.string.bathroom_type_en_placeholder)
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = AppSpacing.S),
            thickness = 0.5.dp
        )

        Text(
            text = stringResource(R.string.room_policies),
            style = AfacadTypography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        PolicySwitchRow(
            label = stringResource(R.string.policy_smoking_label),
            description = stringResource(R.string.policy_smoking_desc),
            checked = state.smokingPolicy,
            onCheckedChange = { checked ->
                viewModel.updateUiState { it.copy(smokingPolicy = checked) }
            },
            icon = Icons.Default.SmokingRooms
        )

        PolicySwitchRow(
            label = stringResource(R.string.policy_pet_label),
            description = stringResource(R.string.policy_pet_desc),
            checked = state.petPolicy,
            onCheckedChange = { checked ->
                viewModel.updateUiState { it.copy(petPolicy = checked) }
            },
            icon = Icons.Default.Pets
        )
    }
}

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
            modifier = Modifier.size(Dimen.SizeM)
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