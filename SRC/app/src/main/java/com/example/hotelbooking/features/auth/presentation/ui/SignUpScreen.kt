package com.example.hotelbooking.features.auth.presentation.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.hotelbooking.features.auth.util.AuthValidation.validateEmail
import com.example.hotelbooking.features.auth.util.AuthValidation.validatePassword
import com.example.hotelbooking.features.auth.util.AuthValidation.validateUsername
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.SlateGray
import com.example.hotelbooking.ui.theme.TextTertiary

@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState = authViewModel.uiState.collectAsState()

    val context = LocalContext.current

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
                Toast.makeText(context, "Sign up failed. Please try again!", Toast.LENGTH_SHORT)
                    .show()
                authViewModel.resetState()
            }

            else -> {}
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
                title = stringResource(id = R.string.signup_title),
                desc = stringResource(id = R.string.signup_description)
            )

            Spacer(modifier = Modifier.height(Dimen.PaddingXL))

            SignUpSection(
                onSignUp = { username, email, password ->
                    authViewModel.signUp(username, email, password)
                },
                onSignUpWithAdmin = { username, email, password, code ->
                    authViewModel.signUpAdmin(username, email, password, code)
                }
            )

            Spacer(modifier = Modifier.height(Dimen.PaddingL))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.already_account) + " ",
                    color = TextTertiary,
                    style = JostTypography.bodyLarge
                )
                Text(
                    text = stringResource(id = R.string.sign_in) + " ",
                    color = PrimaryBlue,
                    style = JostTypography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable {
                        navController.navigate("sign_in") {
                            popUpTo("sign_up") { inclusive = true }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(Dimen.PaddingL))

            AuthOptions()
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
}

@Composable
fun SignUpSection(
    onSignUp: (String, String, String) -> Unit,
    onSignUpWithAdmin: (String, String, String, String) -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var adminCode by remember { mutableStateOf("") }

    var usernameTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var adminCodeTouched by remember { mutableStateOf(false) }

    val showUsernameError = usernameTouched && !validateUsername(username)
    val showEmailError = emailTouched && !validateEmail(email)
    val showPasswordError = passwordTouched && !validatePassword(password)

    var isAdmin by remember { mutableStateOf(false) }

    AuthOutlinedTextField(
        icon = R.drawable.ic_user,
        value = username,
        onValueChange = {
            username = it
            usernameTouched = true
        },
        title = stringResource(id = R.string.username_label),
        placeholder = stringResource(id = R.string.username_hint),
        isError = showUsernameError,
        errorMessage = if (showUsernameError) stringResource(id = R.string.username_too_short) else ""
    )

    Spacer(modifier = Modifier.height(Dimen.PaddingM))

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

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = isAdmin,
            onCheckedChange = { isChecked ->
                isAdmin = isChecked
                if (!isChecked) adminCode = ""
            },
            colors = CheckboxDefaults.colors(
                checkedColor = PrimaryBlue,
                uncheckedColor = SlateGray,
                checkmarkColor = Color.White
            ),
            modifier = Modifier.padding(0.dp)
        )
        Text(
            text = stringResource(id = R.string.register_as_admin),
            style = JostTypography.labelLarge,
            color = Color.Black,
            modifier = Modifier.clickable { isAdmin = !isAdmin }
        )
    }

    AnimatedVisibility(visible = isAdmin) {
        Column {
            Spacer(modifier = Modifier.height(4.dp))
            AuthOutlinedTextField(
                value = adminCode,
                onValueChange = {
                    adminCode = it
                    adminCodeTouched = true
                },
                title = stringResource(id = R.string.admin_code_label),
                placeholder = stringResource(id = R.string.admin_code_hint),
                icon = R.drawable.ic_key,
                isError = adminCodeTouched && adminCode.isBlank(),
                errorMessage = if (adminCodeTouched && adminCode.isBlank()) stringResource(id = R.string.code_is_required) else "",
            )
        }
    }

    Spacer(modifier = Modifier.height(Dimen.PaddingM))

    val isFormTouched =
        usernameTouched && emailTouched && passwordTouched

    val hasError =
        showUsernameError || showEmailError || showPasswordError

    val isSignUpEnable = isFormTouched && !hasError

    val isSignUpAdminEnable = adminCode.isNotBlank() && !hasError

    AppButton(
        text = if (isAdmin) stringResource(id = R.string.sign_up_admin) else stringResource(id = R.string.sign_up),
        onClick = {
            if (isAdmin) {
                onSignUpWithAdmin(username, email, password, adminCode)
            } else {
                onSignUp(username, email, password)
            }
        },
        enabled = if (isAdmin) isSignUpAdminEnable else isSignUpEnable,
        modifier = Modifier.fillMaxWidth()
    )
}