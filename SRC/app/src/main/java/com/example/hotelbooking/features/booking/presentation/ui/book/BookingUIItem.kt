package com.example.hotelbooking.features.booking.presentation.ui.book

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.BlueNavy
import java.time.LocalDate

@Composable
fun BookingSummaryCard(
    roomName: String,
    startDate: LocalDate,
    endDate: LocalDate,
    totalDays: Long,
    totalPrice: Double
) {
    val nightText = stringResource(
        if (totalDays.toInt() == 1)
            R.string.night
        else
            R.string.nights
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(AppShape.ShapeM),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(Dimen.PaddingM)) {
            Text(
                text = stringResource(R.string.booking_summary),
                style = MaterialTheme.typography.titleSmall,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(AppSpacing.S))

            Text(
                roomName,
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = Dimen.PaddingSM))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        stringResource(id = R.string.check_in),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                    Text(
                        startDate.toString(),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                }
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Black)
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        stringResource(id = R.string.check_out),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                    Text(endDate.toString(), fontWeight = FontWeight.SemiBold, color = Color.Black)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = Dimen.PaddingSM))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "$totalDays $nightText",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Text(
                    "$$totalPrice",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BlueNavy
                )
            }
        }
    }
}

@Composable
fun AppOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorText: String? = null,
    singleLine: Boolean = true,
) {
    val colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.Black,
        unfocusedBorderColor = Color.Gray,
        errorBorderColor = Color.Red,
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        cursorColor = Color.Black
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        shape = RoundedCornerShape(12.dp),
        colors = colors,
        modifier = modifier,
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        leadingIcon = {
            leadingIcon?.let {
                Icon(it, contentDescription = null, tint = Color.Black)
            }
        },
        isError = isError,
        supportingText = {
            if (isError && errorText != null) {
                Text(text = errorText, color = Color.Red)
            }
        }
    )
}

@Composable
fun GuestInformationSection(
    name: String,
    onNameChange: (String) -> Unit,
    isNameDirty: Boolean,
    isNameValid: Boolean,
    email: String,
    onEmailChange: (String) -> Unit,
    isEmailDirty: Boolean,
    isEmailValid: Boolean,
    phone: String,
    onPhoneChange: (String) -> Unit,
    isPhoneDirty: Boolean,
    isPhoneValid: Boolean,
    ageStr: String,
    onAgeChange: (String) -> Unit,
) {
    Text(
        text = stringResource(R.string.guest_information),
        color = Color.Black,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(AppSpacing.S))

    AppOutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = stringResource(R.string.full_name),
        leadingIcon = Icons.Default.Person,
        isError = isNameDirty && !isNameValid,
        errorText = stringResource(R.string.name_required),
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(AppSpacing.XS))

    AppOutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = stringResource(R.string.email_label),
        leadingIcon = Icons.Default.Email,
        keyboardType = KeyboardType.Email,
        isError = isEmailDirty && !isEmailValid,
        errorText = if (email.isBlank())
            stringResource(R.string.email_required)
        else
            stringResource(R.string.email_not_valid),
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(AppSpacing.XS))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        AppOutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = stringResource(R.string.phone),
            leadingIcon = Icons.Default.Phone,
            keyboardType = KeyboardType.Phone,
            isError = isPhoneDirty && !isPhoneValid,
            errorText = stringResource(R.string.phone_required),
            modifier = Modifier.weight(2f)
        )

        Spacer(modifier = Modifier.width(AppSpacing.M))

        AppOutlinedTextField(
            value = ageStr,
            onValueChange = { if (it.all(Char::isDigit)) onAgeChange(it) },
            label = stringResource(R.string.age),
            keyboardType = KeyboardType.Number,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun GuestCountSection(
    numberOfGuest: Int,
    capacity: Int,
    onValueChange: (Int) -> Unit
) {
    val isEnable = numberOfGuest < capacity

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.guests) + ": ",
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.width(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = {
                    if (numberOfGuest > 1) onValueChange(numberOfGuest - 1)
                }
            ) {
                Icon(
                    Icons.Default.Remove,
                    contentDescription = stringResource(R.string.decrease_guests),
                    tint = if (numberOfGuest == 1) Color.LightGray else Color.Black
                )
            }

            Text(
                text = numberOfGuest.toString(),
                color = Color.Black,
                modifier = Modifier.width(32.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )

            IconButton(
                onClick = {
                    if (numberOfGuest < capacity) {
                        onValueChange(numberOfGuest + 1)
                    }
                },
                enabled = isEnable
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.increase_guests),
                    tint = if (isEnable) Color.Black else Color.LightGray
                )
            }
        }
    }
}