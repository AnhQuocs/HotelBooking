package com.example.hotelbooking.features.auth.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppButton
import com.example.hotelbooking.features.auth.domain.model.UserRole
import com.example.hotelbooking.features.auth.presentation.components.AuthOptions
import com.example.hotelbooking.features.auth.presentation.components.AuthOutlinedTextField
import com.example.hotelbooking.features.auth.presentation.components.AuthTitle
import com.example.hotelbooking.features.auth.presentation.viewmodel.AuthState
import com.example.hotelbooking.features.auth.presentation.viewmodel.AuthViewModel
import com.example.hotelbooking.features.auth.presentation.viewmodel.UpdateActionState
import com.example.hotelbooking.features.auth.util.AuthValidation
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.ActionDanger
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.TextTertiary
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState = authViewModel.uiState.collectAsState()
    val updateState by authViewModel.updateState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val webClientId = stringResource(R.string.default_web_client_id)
    val googleAuthUiClient = remember {
        GoogleAuthUiClient(context, webClientId)
    }
    val scope = rememberCoroutineScope()

    val failedMessage = stringResource(id = R.string.sign_in_failed)
    LaunchedEffect(key1 = uiState.value) {
        when (val state = uiState.value) {
            is AuthState.Success -> {
                val destination = when (state.user.role) {
                    UserRole.ADMIN -> "admin_root"
                    UserRole.USER -> "user_root"
                }

                navController.navigate(destination) {
                    popUpTo(0) { inclusive = true }
                }

                authViewModel.resetState()
            }

            is AuthState.Error -> {
                Toast.makeText(context, failedMessage, Toast.LENGTH_SHORT)
                    .show()
                authViewModel.resetState()
            }

            else -> {}
        }
    }

    LaunchedEffect(updateState) {
        when (updateState) {
            is UpdateActionState.Success -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.reset_password_email_sent),
                    Toast.LENGTH_SHORT
                ).show()

                showDialog = false
                authViewModel.resetUpdateState()
            }
            is UpdateActionState.Error -> {
                val errorMsg = (updateState as UpdateActionState.Error).message.asString(context)
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                authViewModel.resetUpdateState()
            }
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimen.PaddingM)
                .padding(top = Dimen.PaddingXXL),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthTitle(
                title = stringResource(id = R.string.signIn_title),
                desc = stringResource(id = R.string.signIn_description)
            )

            Spacer(modifier = Modifier.height(Dimen.PaddingXL))

            SignInSection(
                onSignIn = { email, password ->
                    authViewModel.signIn(email, password)
                },
                onShowDialog = { showDialog = true }
            )

            Spacer(modifier = Modifier.height(Dimen.PaddingL))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.dont_have_account) + " ",
                    color = TextTertiary,
                    style = AfacadTypography.bodyLarge
                )
                Text(
                    text = stringResource(id = R.string.sign_up) + " ",
                    color = PrimaryBlue,
                    style = AfacadTypography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable {
                        navController.navigate("sign_up") {
                            popUpTo("sign_in") { inclusive = true }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(Dimen.PaddingL))

            val googleCanceled = stringResource(id = R.string.google_sign_in_canceled)

            AuthOptions(
                onClick = {
                    scope.launch {
                        authViewModel.showLoading()

                        val idToken = googleAuthUiClient.signIn()

                        if (idToken != null) {
                            authViewModel.signInWithGoogle(idToken)
                        } else {
                            authViewModel.resetState()
                            Toast.makeText(context, googleCanceled, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        if (uiState.value == AuthState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.2f))
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitPointerEvent()
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }
    }

    if (showDialog) {
        ForgotPasswordDialog(
            onDismiss = { showDialog = false },
            onConfirm = { email ->
                authViewModel.resetPassword(email)
                showDialog = false
            },
            isLoading = updateState is UpdateActionState.Loading
        )
    }
}

@Composable
fun SignInSection(
    onSignIn: (String, String) -> Unit,
    onShowDialog: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }

    val showEmailError = emailTouched && !AuthValidation.validateEmail(email)
    val showPasswordError = passwordTouched && !AuthValidation.validatePassword(password)

    AuthOutlinedTextField(
        icon = R.drawable.ic_email,
        value = email,
        onValueChange = {
            email = it
            emailTouched = true
        },
        title = stringResource(id = R.string.email_label),
        placeholder = stringResource(id = R.string.email_hint),
        isError = showEmailError,
        errorMessage = if (showEmailError) stringResource(id = R.string.email_not_valid) else ""
    )

    Spacer(modifier = Modifier.height(Dimen.PaddingM))

    AuthOutlinedTextField(
        icon = R.drawable.ic_password,
        value = password,
        onValueChange = {
            password = it
            passwordTouched = true
        },
        title = stringResource(id = R.string.password_label),
        placeholder = stringResource(id = R.string.password_hint),
        isError = showPasswordError,
        errorMessage = if (showPasswordError) stringResource(id = R.string.password_too_short) else "",
        isTrailingIcon = true,
    )

    Spacer(modifier = Modifier.height(Dimen.PaddingSM))

    Text(
        text = stringResource(id = R.string.forgot_password),
        color = ActionDanger,
        style = AfacadTypography.labelLarge,
        textAlign = TextAlign.End,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onShowDialog() }
    )

    Spacer(modifier = Modifier.height(Dimen.PaddingL))

    val isButtonEnable = emailTouched && passwordTouched && !showEmailError && !showPasswordError

    AppButton(
        onClick = { onSignIn(email, password) },
        text = stringResource(id = R.string.sign_in),
        enabled = isButtonEnable,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    isLoading: Boolean
) {
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(AppShape.ShapeL),
        containerColor = Color.White,
        title = {
            Text(
                text = stringResource(id = R.string.forgot_password),
                style = AfacadTypography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.reset_password_hint),
                    style = AfacadTypography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(id = R.string.email_label)) },
                    textStyle = TextStyle(color = Color.Black),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(AppShape.ShapeM),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        cursorColor = Color.Black
                    ),
                    singleLine = true,
                    enabled = !isLoading
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(email) },
                enabled = email.isNotBlank() && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = BlueNavy)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.send_request), color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text(stringResource(id = R.string.cancel), color = Color.Gray)
            }
        }
    )
}