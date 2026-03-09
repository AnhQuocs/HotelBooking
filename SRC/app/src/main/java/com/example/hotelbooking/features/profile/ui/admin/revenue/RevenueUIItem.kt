package com.example.hotelbooking.features.profile.ui.admin.revenue

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.profile.viewmodel.admin.RevenuePeriod
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.PrimaryBlue
import java.time.format.DateTimeFormatter
import kotlin.collections.find

@Composable
fun RevenuePeriod.getLabel(): String {
    return when (this) {
        RevenuePeriod.DAY -> stringResource(R.string.period_day)
        RevenuePeriod.WEEK -> stringResource(R.string.period_week)
        RevenuePeriod.MONTH -> stringResource(R.string.period_month)
        RevenuePeriod.ALL -> stringResource(R.string.period_all)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodFilterRow(selectedPeriod: RevenuePeriod, onPeriodSelected: (RevenuePeriod) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.S)
    ) {
        RevenuePeriod.entries.forEach { period ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                label = { Text(period.getLabel()) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryBlue.copy(alpha = 0.1f),
                    selectedLabelColor = PrimaryBlue
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedPeriod == period,
                    borderColor = Color.Gray.copy(alpha = 0.5f),
                    selectedBorderColor = PrimaryBlue
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelFilterDropdown(
    hotels: List<Hotel>,
    selectedHotelId: String?,
    onHotelSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedHotelName =
        hotels.find { it.id == selectedHotelId }?.name ?: stringResource(id = R.string.all_hotels)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedHotelName,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(AppShape.ShapeM)
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.all_hotels)) },
                onClick = { onHotelSelected(null); expanded = false }
            )
            hotels.forEach { hotel ->
                DropdownMenuItem(
                    text = { Text(hotel.name) },
                    onClick = { onHotelSelected(hotel.id); expanded = false }
                )
            }
        }
    }
}

@Composable
fun DateSelector(
    selectedDate: java.time.LocalDate,
    period: RevenuePeriod,
    onDateSelected: (java.time.LocalDate) -> Unit
) {
    val context = LocalContext.current

    val label = when (period) {
        RevenuePeriod.DAY -> stringResource(
            R.string.revenue_day,
            selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        )

        RevenuePeriod.WEEK -> stringResource(
            R.string.revenue_week,
            selectedDate.format(DateTimeFormatter.ofPattern("dd/MM"))
        )

        RevenuePeriod.MONTH -> stringResource(
            R.string.revenue_month,
            selectedDate.format(DateTimeFormatter.ofPattern("MM/yyyy"))
        )

        RevenuePeriod.ALL -> stringResource(R.string.revenue_all_time)
    }

    if (period != RevenuePeriod.ALL) {
        OutlinedCard(
            onClick = {
                android.app.DatePickerDialog(context, { _, y, m, d ->
                    onDateSelected(java.time.LocalDate.of(y, m + 1, d))
                }, selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth).show()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AppShape.ShapeM),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(Dimen.PaddingM),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(Dimen.SizeSM)
                )
                Spacer(modifier = Modifier.width(AppSpacing.M))
                Text(text = label, style = AfacadTypography.bodyLarge)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(Dimen.SizeS),
                    tint = Color.Gray
                )
            }
        }
    }
}