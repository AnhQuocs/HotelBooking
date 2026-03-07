package com.example.hotelbooking.features.profile.ui.user.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.hotelbooking.R
import com.example.hotelbooking.features.profile.util.ProfileValidationUtil
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun EditInfoDialog(
    field: String,
    initialValue: String?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    isLoading: Boolean
) {
    val context = LocalContext.current

    var firstName by remember {
        mutableStateOf(if (field == "fullName") initialValue?.split(" ")?.lastOrNull() ?: "" else "")
    }
    var lastName by remember {
        mutableStateOf(if (field == "fullName") initialValue?.split(" ")?.dropLast(1)?.joinToString(" ") ?: "" else "")
    }
    var singleValue by remember {
        mutableStateOf(if (field != "fullName") initialValue ?: "" else "")
    }

    var isFirstNameTouched by remember { mutableStateOf(false) }
    var isLastNameTouched by remember { mutableStateOf(false) }
    var isSingleValueTouched by remember { mutableStateOf(false) }

    val firstNameError = ProfileValidationUtil.validateName(context, firstName, stringResource(id = R.string.first_name_label))
    val lastNameError = ProfileValidationUtil.validateName(context, lastName, stringResource(id = R.string.last_name_label))
    val usernameError = ProfileValidationUtil.validateUsername(context, singleValue)
    val phoneError = ProfileValidationUtil.validatePhone(context, singleValue)

    val isDataValid = when (field) {
        "fullName" -> firstName.isNotBlank() && lastName.isNotBlank() && firstNameError == null && lastNameError == null
        "username" -> singleValue.isNotBlank() && usernameError == null
        "phoneNumber" -> singleValue.isNotBlank() && phoneError == null
        else -> false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val finalValue = if (field == "fullName") "$lastName $firstName".trim() else singleValue
                    onSave(finalValue)
                },
                enabled = !isLoading && isDataValid
            ) {
                Text(stringResource(R.string.save), color = if (isDataValid) PrimaryBlue else Color.Gray)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = Color.Gray)
            }
        },
        title = {
            Text(
                text = when (field) {
                    "fullName" -> stringResource(R.string.edit_full_name)
                    "username" -> stringResource(R.string.edit_username)
                    "phoneNumber" -> stringResource(R.string.edit_phone)
                    else -> ""
                }, style = AfacadTypography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.S)) {
                when (field) {
                    "fullName" -> {
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = {
                                lastName = it
                                isLastNameTouched = true
                            },
                            label = { Text(stringResource(R.string.last_name_label)) },
                            shape = RoundedCornerShape(AppShape.ShapeM),
                            isError = isLastNameTouched && lastNameError != null,
                            supportingText = {
                                if (isLastNameTouched && lastNameError != null) {
                                    Text(text = lastNameError, color = Color.Red)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = {
                                firstName = it
                                isFirstNameTouched = true
                            },
                            label = { Text(stringResource(R.string.first_name_label)) },
                            shape = RoundedCornerShape(AppShape.ShapeM),
                            isError = isFirstNameTouched && firstNameError != null,
                            supportingText = {
                                if (isFirstNameTouched && firstNameError != null) {
                                    Text(text = firstNameError, color = Color.Red)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    "username" -> {
                        OutlinedTextField(
                            value = singleValue,
                            onValueChange = {
                                singleValue = it
                                isSingleValueTouched = true
                            },
                            label = { Text(stringResource(R.string.username_label)) },
                            shape = RoundedCornerShape(AppShape.ShapeM),
                            isError = isSingleValueTouched && usernameError != null,
                            supportingText = {
                                if (isSingleValueTouched && usernameError != null) {
                                    Text(text = usernameError, color = Color.Red)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    "phoneNumber" -> {
                        OutlinedTextField(
                            value = singleValue,
                            onValueChange = {
                                singleValue = it
                                isSingleValueTouched = true
                            },
                            label = { Text(stringResource(R.string.phone)) },
                            shape = RoundedCornerShape(AppShape.ShapeM),
                            isError = isSingleValueTouched && phoneError != null,
                            supportingText = {
                                if (isSingleValueTouched && phoneError != null) {
                                    Text(text = phoneError, color = Color.Red)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                    }
                }

                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().padding(top = Dimen.PaddingS)
                    )
                }
            }
        }
    )
}