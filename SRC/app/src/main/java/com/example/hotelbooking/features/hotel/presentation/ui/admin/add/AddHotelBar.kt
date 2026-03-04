package com.example.hotelbooking.features.hotel.presentation.ui.admin.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.NearBlack
import com.example.hotelbooking.ui.theme.RoyalBlue

@Composable
fun AddHotelTopBar(
    isEdit: Boolean,
    hasUnsavedChanges: Boolean,
    onBackClick: () -> Unit,
    onSaveDraftClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingM)
            .height(70.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                tint = NearBlack,
                modifier = Modifier
                    .size(Dimen.SizeSM)
                    .clickable { onBackClick() }
            )

            Text(
                text = if (isEdit) stringResource(id = R.string.edit_hotel) else stringResource(id = R.string.add_new_hotel),
                style = AfacadTypography.titleMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NearBlack
                )
            )

            if (hasUnsavedChanges) {
                Icon(
                    imageVector = Icons.Outlined.Save,
                    contentDescription = null,
                    tint = RoyalBlue,
                    modifier = Modifier
                        .size(Dimen.SizeM)
                        .clickable { onSaveDraftClick() }
                )
            } else {
                Spacer(modifier = Modifier.size(Dimen.SizeM))
            }
        }
    }
}

@Composable
fun AddHotelBottomBar(
    currentStep: Int,
    isNextEnabled: Boolean,
    isLoading: Boolean,
    onBackStep: () -> Unit,
    onNextStep: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 10.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingM)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.S),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentStep > 0) {
                OutlinedButton(
                    onClick = onBackStep,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(Dimen.HeightLarge),
                    shape = RoundedCornerShape(AppShape.ShapeM),
                    border = BorderStroke(1.dp, Color.LightGray),
                    enabled = !isLoading
                ) {
                    Text(
                        text = stringResource(id = R.string.previous),
                        style = AfacadTypography.bodyMedium,
                        color = Color.Black
                    )
                }
            }

            Button(
                onClick = onNextStep,
                modifier = Modifier
                    .weight(2f)
                    .height(Dimen.HeightLarge),
                shape = RoundedCornerShape(AppShape.ShapeM),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoyalBlue,
                    disabledContainerColor = RoyalBlue.copy(alpha = 0.5f)
                ),
                enabled = isNextEnabled && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(Dimen.SizeM),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (currentStep == 3) stringResource(id = R.string.submit)
                        else stringResource(id = R.string.next),
                        style = AfacadTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }
    }
}