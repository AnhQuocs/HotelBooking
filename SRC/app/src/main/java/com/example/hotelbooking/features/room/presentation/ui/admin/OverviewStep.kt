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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
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
        Text(
            text = stringResource(R.string.room_type_name),
            style = AfacadTypography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        AdminTextField(
            value = state.nameVi,
            onValueChange = { newName ->
                viewModel.updateUiState { it.copy(nameVi = newName) }
            },
            label = stringResource(R.string.room_name_vi_label),
            placeholder = stringResource(R.string.room_name_vi_placeholder),
            leadingIcon = { Icon(Icons.Default.Title, null, tint = RoyalBlue) }
        )

        AdminTextField(
            value = state.nameEn,
            onValueChange = { newName ->
                viewModel.updateUiState { it.copy(nameEn = newName) }
            },
            label = stringResource(R.string.room_name_en_label),
            placeholder = stringResource(R.string.room_name_en_placeholder),
            leadingIcon = { Icon(Icons.Default.Translate, null, tint = Color.Gray) }
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = AppSpacing.S),
            thickness = 0.5.dp
        )

        Text(
            text = stringResource(R.string.room_detail_description),
            style = AfacadTypography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        AdminTextField(
            value = state.descriptionVi,
            onValueChange = { newDes ->
                viewModel.updateUiState { it.copy(descriptionVi = newDes) }
            },
            label = stringResource(R.string.room_description_vi_label),
            placeholder = stringResource(R.string.room_description_vi_placeholder),
            singleLine = false,
            minLines = 3
        )

        AdminTextField(
            value = state.descriptionEn,
            onValueChange = { newDes ->
                viewModel.updateUiState { it.copy(descriptionEn = newDes) }
            },
            label = stringResource(R.string.room_description_en_label),
            placeholder = stringResource(R.string.room_description_en_placeholder),
            singleLine = false,
            minLines = 3
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = AppSpacing.S),
            thickness = 0.5.dp
        )

        Text(
            text = stringResource(R.string.room_price_per_night),
            style = AfacadTypography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        AdminTextField(
            value = state.price,
            onValueChange = {
                if (it.all { char -> char.isDigit() }) {
                    viewModel.updateUiState { old -> old.copy(price = it) }
                }
            },
            label = stringResource(R.string.room_price_label),
            placeholder = "0.00",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = {
                Icon(Icons.Default.AttachMoney, null, tint = Color(0xFF4CAF50))
            },
            suffix = {
                Text(
                    stringResource(R.string.per_night_suffix),
                    style = AfacadTypography.bodySmall
                )
            }
        )
    }
}

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