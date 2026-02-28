package com.example.hotelbooking.features.hotel.presentation.ui.admin.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.NearBlack
import com.example.hotelbooking.ui.theme.RoyalBlue

val CustomIconCatalog = mapOf(
    "Star" to Icons.Default.Star,
    "Cafe" to Icons.Default.LocalCafe,
    "Fitness" to Icons.Default.FitnessCenter,
    "Dining" to Icons.Default.LocalDining,
    "Spa" to Icons.Default.Spa,
    "Snow" to Icons.Default.AcUnit,
    "Pets" to Icons.Default.Pets,
    "Wifi" to Icons.Default.Wifi,
    "Pool" to Icons.Default.Pool,
    "Check" to Icons.Default.CheckCircle
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomAmenityDialog(
    onDismiss: () -> Unit,
    onSave: (nameEn: String, nameVi: String, iconName: String) -> Unit
) {
    var nameEn by remember { mutableStateOf("") }
    var nameVi by remember { mutableStateOf("") }
    var selectedIconName by remember { mutableStateOf("Star") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.add_amenity), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.M)) {
                OutlinedTextField(
                    value = nameEn,
                    onValueChange = { nameEn = it },
                    label = { Text(stringResource(id = R.string.name_english)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nameVi,
                    onValueChange = { nameVi = it },
                    label = { Text(stringResource(id = R.string.name_vietnamese)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    stringResource(id = R.string.select_icon),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = Dimen.PaddingS)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.M),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(CustomIconCatalog.keys.toList()) { iconName ->
                        val iconVector = CustomIconCatalog[iconName]!!
                        val isSelected = iconName == selectedIconName

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = if (isSelected) RoyalBlue.copy(alpha = 0.2f) else Color.Transparent,
                                    shape = RoundedCornerShape(AppShape.ShapeS)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) RoyalBlue else Color.LightGray,
                                    shape = RoundedCornerShape(AppShape.ShapeS)
                                )
                                .clickable { selectedIconName = iconName },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = iconName,
                                tint = if (isSelected) RoyalBlue else NearBlack
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(nameEn.trim(), nameVi.trim(), selectedIconName) },
                enabled = nameEn.isNotBlank()
            ) {
                Text(
                    stringResource(id = R.string.save),
                    color = if (nameEn.isNotBlank()) RoyalBlue else Color.Gray
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(id = R.string.cancel),
                    color = Color.Gray
                )
            }
        }
    )
}