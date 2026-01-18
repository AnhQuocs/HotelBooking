package com.example.hotelbooking.features.booking.presentation.ui.rebook

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppButton
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.features.booking.presentation.ui.book.AppOutlinedTextField
import com.example.hotelbooking.features.booking.presentation.ui.book.GuestCountSection
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun UpdateGuestInfoScreen(
    name: String,
    email: String,
    phone: String,
    age: String,
    numberOfGuest: Int,
    capacity: Int,
    isUpdating: Boolean,
    onUpdate: (String, String, String, String, Int) -> Unit,
    onBackClick: () -> Unit,
) {
    var newName by remember { mutableStateOf(name) }
    var newEmail by remember { mutableStateOf(email) }
    var newPhone by remember { mutableStateOf(phone) }
    var newAge by remember { mutableStateOf(age) }
    var newNumberOfGuest by remember { mutableStateOf(numberOfGuest) }

    val isNameValid = newName.isNotBlank()
    val isPhoneValid = newPhone.isNotBlank() && newPhone.length >= 10
    val isEmailValid = newEmail.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()

    val hasChanges = remember(newName, newEmail, newPhone, newAge, newNumberOfGuest) {
        newName != name ||
                newEmail != email ||
                newPhone != phone ||
                newAge != age ||
                newNumberOfGuest != numberOfGuest
    }
    val isFormValid = isNameValid && isPhoneValid && isEmailValid
    val canUpdate = isFormValid && hasChanges

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                AppTopBar(
                    text = stringResource(id = R.string.guest_info),
                    onBackClick = onBackClick
                )
            },
            bottomBar = {
                AppButton(
                    text = stringResource(id = R.string.update),
                    modifier = Modifier.fillMaxWidth().padding(Dimen.PaddingM),
                    enabled = canUpdate && !isUpdating,
                    onClick = {
                        onUpdate(newName, newEmail, newPhone, newAge, newNumberOfGuest)
                    }
                )
            },
            containerColor = Color.White
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(Dimen.PaddingM)
            ) {
                Text(
                    text = stringResource(R.string.guest_information),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(AppSpacing.S))

                AppOutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = stringResource(R.string.full_name),
                    leadingIcon = Icons.Default.Person,
                    isError = !isNameValid,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(AppSpacing.XS))

                AppOutlinedTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = stringResource(R.string.email_label),
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    isError = !isEmailValid,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(AppSpacing.XS))

                Row(modifier = Modifier.fillMaxWidth()) {
                    AppOutlinedTextField(
                        value = newPhone,
                        onValueChange = { if (it.length <= 11) newPhone = it },
                        label = stringResource(R.string.phone),
                        leadingIcon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone,
                        isError = !isPhoneValid,
                        modifier = Modifier.weight(2f)
                    )

                    Spacer(modifier = Modifier.width(AppSpacing.M))

                    AppOutlinedTextField(
                        value = newAge,
                        onValueChange = { if (it.all(Char::isDigit)) newAge = it },
                        label = stringResource(R.string.age),
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.SPlus))

                GuestCountSection(
                    numberOfGuest = newNumberOfGuest,
                    capacity = capacity,
                    onValueChange = { newNumberOfGuest = it }
                )
            }
        }

        if (isUpdating) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }
    }
}