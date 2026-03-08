package com.example.hotelbooking.features.profile.ui.user.account

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.features.auth.presentation.viewmodel.AuthViewModel
import com.example.hotelbooking.features.auth.presentation.viewmodel.UpdateActionState
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.ErrorRed
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.RoyalBlue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ChangePasswordScreen(onBackClick = { finish() })
        }
    }
}

@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val updateState by viewModel.updateState.collectAsState()

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isOldPasswordDirty by remember { mutableStateOf(false) }
    var isNewPasswordDirty by remember { mutableStateOf(false) }
    var isConfirmPasswordDirty by remember { mutableStateOf(false) }

    val oldPasswordError = if (isOldPasswordDirty && oldPassword.isBlank()) {
        stringResource(R.string.error_empty_password)
    } else null

    val newPasswordError = if (isNewPasswordDirty) {
        when {
            newPassword.isBlank() -> stringResource(R.string.error_empty_password)
            newPassword.length < 8 -> stringResource(R.string.password_too_short)
            newPassword == oldPassword -> stringResource(R.string.error_password_same_as_old)
            else -> null
        }
    } else null

    val confirmPasswordError = if (isConfirmPasswordDirty) {
        when {
            confirmPassword != newPassword -> stringResource(R.string.error_password_mismatch)
            else -> null
        }
    } else null

    var currentToast by remember { mutableStateOf<Toast?>(null) }

    LaunchedEffect(updateState) {
        when (updateState) {
            is UpdateActionState.Success -> {
                currentToast?.cancel()

                val newToast = Toast.makeText(context, context.getString(R.string.update_success), Toast.LENGTH_SHORT)
                newToast.show()
                currentToast = newToast

                viewModel.resetUpdateState()
                onBackClick()
            }
            is UpdateActionState.Error -> {
                currentToast?.cancel()

                val message = (updateState as UpdateActionState.Error).message.asString(context)
                val newToast = Toast.makeText(context, message, Toast.LENGTH_LONG)
                newToast.show()
                currentToast = newToast

                viewModel.resetUpdateState()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                text = stringResource(id = R.string.change_password),
                onBackClick = onBackClick
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(Dimen.PaddingM),
            verticalArrangement = Arrangement.spacedBy(Dimen.PaddingS)
        ) {
            PasswordInputField(
                value = oldPassword,
                onValueChange = { oldPassword = it; isOldPasswordDirty = true },
                label = stringResource(R.string.old_password),
                errorText = oldPasswordError
            )

            PasswordInputField(
                value = newPassword,
                onValueChange = { newPassword = it; isNewPasswordDirty = true },
                label = stringResource(R.string.new_password),
                errorText = newPasswordError
            )

            PasswordInputField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; isConfirmPasswordDirty = true },
                label = stringResource(R.string.confirm_new_password),
                errorText = confirmPasswordError
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.changePassword(oldPassword, newPassword) },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                enabled = oldPassword.isNotBlank() &&
                        newPassword.length >= 8 &&
                        newPassword != oldPassword &&
                        confirmPassword == newPassword &&
                        updateState !is UpdateActionState.Loading,
                shape = RoundedCornerShape(AppShape.ShapeM),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (updateState is UpdateActionState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text(stringResource(R.string.confirm))
                }
            }
        }
    }
}

@Composable
fun PasswordInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorText: String?
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    label,
                    style = AfacadTypography.bodyMedium
                )
            },
            modifier = Modifier.fillMaxWidth(),
            isError = errorText != null,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff

                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (errorText != null) ErrorRed else RoyalBlue
                    )
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(AppShape.ShapeM),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                errorBorderColor = ErrorRed
            )
        )

        if (errorText != null) {
            Text(
                text = errorText,
                color = ErrorRed,
                style = AfacadTypography.bodySmall,
                modifier = Modifier.padding(start = Dimen.PaddingXS, top = Dimen.PaddingXXS)
            )
        }
    }
}