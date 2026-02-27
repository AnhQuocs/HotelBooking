package com.example.hotelbooking.features.hotel.presentation.ui.admin.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.admin.hotel.presentation.viewmodel.AddHotelUiState
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.theme.AfacadTypography

@Composable
fun BasicInfoScreen(
    uiState: AddHotelUiState, onValueChange: (String, String, String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {

        Text(
            text = stringResource(id = R.string.basic_information),
            style = AfacadTypography.titleMedium.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(AppSpacing.L))

        SectionTitle(stringResource(id = R.string.hotel_name))
        Spacer(modifier = Modifier.height(12.dp))

        BilingualTextField(
            viValue = uiState.nameVi,
            enValue = uiState.nameEn,
            viTitle = stringResource(R.string.name_vietnamese),
            enTitle = stringResource(R.string.name_english),
            placeholder = stringResource(R.string.enter_hotel_name),
            maxLines = 1,
            onViChange = { vi ->
                onValueChange(vi, uiState.nameEn, uiState.descriptionVi, uiState.descriptionEn)
            },
            onEnChange = { en ->
                onValueChange(uiState.nameVi, en, uiState.descriptionVi, uiState.descriptionEn)
            })

        Spacer(modifier = Modifier.height(AppSpacing.L))

        SectionTitle(stringResource(R.string.hotel_description))
        Spacer(modifier = Modifier.height(AppSpacing.M))

        BilingualTextField(
            viValue = uiState.descriptionVi,
            enValue = uiState.descriptionEn,
            viTitle = stringResource(R.string.description_vietnamese),
            enTitle = stringResource(R.string.description_english),
            placeholder = stringResource(R.string.enter_description),
            maxLines = 3,
            onViChange = { vi ->
                onValueChange(uiState.nameVi, uiState.nameEn, vi, uiState.descriptionEn)
            },
            onEnChange = { en ->
                onValueChange(uiState.nameVi, uiState.nameEn, uiState.descriptionVi, en)
            })
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = AfacadTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = Color.Black
    )
}

@Composable
private fun BilingualTextField(
    viValue: String,
    enValue: String,
    viTitle: String,
    enTitle: String,
    placeholder: String,
    maxLines: Int,
    onViChange: (String) -> Unit,
    onEnChange: (String) -> Unit
) {
    BasicInfoOutlinedTextField(
        value = viValue,
        title = viTitle,
        placeholder = placeholder,
        maxLine = maxLines,
        onValueChange = onViChange
    )

    Spacer(modifier = Modifier.height(AppSpacing.S))

    BasicInfoOutlinedTextField(
        value = enValue,
        title = enTitle,
        placeholder = placeholder,
        maxLine = maxLines,
        onValueChange = onEnChange
    )
}

@Composable
fun BasicInfoOutlinedTextField(
    value: String, title: String, placeholder: String, maxLine: Int, onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = AfacadTypography.labelLarge,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(AppSpacing.S))

        OutlinedTextField(
            value = value,
            onValueChange = { newValue -> onValueChange(newValue) },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.Black
            ),
            placeholder = {
                Text(
                    text = placeholder, style = AfacadTypography.labelLarge, color = Color.Gray
                )
            },
            maxLines = maxLine,
            modifier = Modifier.fillMaxWidth()
        )
    }
}