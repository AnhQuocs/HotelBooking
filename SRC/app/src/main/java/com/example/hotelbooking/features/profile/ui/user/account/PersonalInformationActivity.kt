package com.example.hotelbooking.features.profile.ui.user.account

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.components.LineGray
import com.example.hotelbooking.features.auth.domain.model.UserRole
import com.example.hotelbooking.features.auth.presentation.viewmodel.AuthViewModel
import com.example.hotelbooking.features.auth.presentation.viewmodel.UpdateActionState
import com.example.hotelbooking.features.main.MainActivity
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.CancelledRed
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.TextTertiary
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class PersonalInformationActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val authViewModel: AuthViewModel = hiltViewModel()

            PersonalInfoScreen(
                authViewModel = authViewModel,
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    val user by authViewModel.currentUser.collectAsState()
    val updateState by authViewModel.updateState.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var editingField by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showReAuthPasswordDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                text = stringResource(R.string.personal_information),
                onBackClick = onBackClick
            )
        },
        containerColor = Color.White
    ) { padding ->
        val isAdmin = user?.role == UserRole.ADMIN

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(AppSpacing.L))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                            .clickable { galleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (user?.avatar != null) {
                            AsyncImage(
                                model = user?.avatar,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                modifier = Modifier.size(Dimen.SizeXLPlus),
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(AppSpacing.M))

                    Text(
                        text = user?.email ?: "example@gmail.com",
                        style = AfacadTypography.titleMedium.copy(color = TextTertiary),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimen.PaddingSM)
                    )
                }

                if (selectedImageUri != null) {
                    Button(
                        onClick = {
                            selectedImageUri?.let { authViewModel.updateAvatar(it) }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(top = Dimen.PaddingS),
                        enabled = updateState !is UpdateActionState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        if (updateState is UpdateActionState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(Dimen.SizeSM),
                                color = PrimaryBlue
                            )
                        } else {
                            Text(stringResource(R.string.save_avatar_btn))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(AppSpacing.L))
            }

            item {
                Text(
                    text = stringResource(
                        id = if (isAdmin) R.string.admin_save_info_hint else R.string.save_info_hint
                    ),
                    style = AfacadTypography.titleMedium.copy(color = TextTertiary),
                    modifier = Modifier.padding(horizontal = Dimen.PaddingSM)
                )

                Spacer(modifier = Modifier.height(AppSpacing.XL))
            }

            item {
                ProfileInfoRow(
                    label = stringResource(R.string.username_label),
                    value = user?.username,
                    onClick = {
                        editingField = "username"
                        showEditDialog = true
                    }
                )
                ProfileInfoRow(
                    label = stringResource(R.string.full_name),
                    value = user?.fullName,
                    onClick = {
                        editingField = "fullName"
                        showEditDialog = true
                    }
                )
                ProfileInfoRow(
                    label = stringResource(R.string.date_of_birth),
                    value = user?.dob?.toDate()?.let {
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                    },
                    onClick = { showDatePicker = true },
                )

                Column {
                    ProfileInfoRow(
                        label = stringResource(R.string.phone),
                        value = user?.phoneNumber,
                        onClick = {
                            editingField = "phoneNumber"
                            showEditDialog = true
                        },
                        isLast = true
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.XS))

                    Text(
                        text = stringResource(
                            id = if (isAdmin)
                                R.string.guest_contact_phone_hint
                            else
                                R.string.hotel_contact_phone_hint
                        ),
                        style = AfacadTypography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = Dimen.PaddingM)
                    )
                }
            }

            item {
                Column {
                    LineGray(modifier = Modifier.padding(Dimen.PaddingM))

                    Text(
                        text = stringResource(R.string.security),
                        style = AfacadTypography.bodyLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = Dimen.PaddingM)
                    )

                    ProfileInfoRow(
                        label = stringResource(R.string.password_label),
                        value = stringResource(R.string.change_password),
                        onClick = {
                            context.startActivity(
                                Intent(
                                    context,
                                    ChangePasswordActivity::class.java
                                )
                            )
                        },
                        isLast = true
                    )
                }
            }

            if (!isAdmin) {
                item {
                    OutlinedButton(
                        onClick = { showConfirmDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CancelledRed),
                        border = BorderStroke(1.dp, CancelledRed),
                        shape = RoundedCornerShape(AppShape.ShapeS),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimen.PaddingM)
                            .padding(bottom = Dimen.PaddingL)
                            .height(Dimen.HeightLarge)
                    ) {
                        Text(
                            text = stringResource(id = R.string.delete_account),
                            style = AfacadTypography.bodyLarge
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(updateState) {
        if (updateState is UpdateActionState.Success) {
            selectedImageUri = null
            showEditDialog = false
            authViewModel.resetUpdateState()
        }

        if (updateState is UpdateActionState.Error) {
            val errorMessage = (updateState as UpdateActionState.Error).message.asString(context)
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            authViewModel.resetUpdateState()
        }

        if (updateState is UpdateActionState.DeleteAccountSuccess) {
            authViewModel.resetState()

            Toast.makeText(
                context,
                context.getString(R.string.delete_account_success),
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("GO_TO_AUTH", true)
            }

            context.startActivity(intent)
            (context as? Activity)?.finish()
        }
    }

    if (showEditDialog) {
        EditInfoDialog(
            field = editingField,
            initialValue = when (editingField) {
                "fullName" -> user?.fullName
                "username" -> user?.username
                else -> user?.phoneNumber
            },
            isLoading = updateState is UpdateActionState.Loading,
            onDismiss = { showEditDialog = false },
            onSave = { newValue ->
                authViewModel.updateProfileField(editingField, newValue)
            }
        )
    }

    if (showConfirmDialog) {
        DeleteAccountDialog(
            onConfirm = {
                showConfirmDialog = false
                showReAuthPasswordDialog = true
            },
            onDismiss = { showConfirmDialog = false }
        )
    }

    if (showReAuthPasswordDialog) {
        ReAuthPasswordDialog(
            isLoading = updateState is UpdateActionState.Loading,
            onDismiss = { authViewModel.resetUpdateState(); showReAuthPasswordDialog = false },
            onConfirm = { password ->
                authViewModel.deleteAccountWithReAuth(password)
            }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Date(millis)
                        val timestamp = Timestamp(selectedDate)

                        authViewModel.updateProfileField("dob", timestamp)
                    }
                    showDatePicker = false
                }) { Text(stringResource(id = R.string.confirm), color = PrimaryBlue) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(id = R.string.cancel), color = Color.Gray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun ProfileInfoRow(
    label: String,
    value: String?,
    onClick: () -> Unit,
    isLast: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Dimen.PaddingSM, horizontal = Dimen.PaddingM)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = AfacadTypography.bodyLarge,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(AppSpacing.S))
                Text(
                    text = value ?: stringResource(R.string.not_provided),
                    style = AfacadTypography.bodyMedium,
                    color = if (value == null) Color.Gray else Color.DarkGray
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(Dimen.PaddingL)
            )
        }
        if (!isLast) {
            HorizontalDivider(
                modifier = Modifier.padding(top = Dimen.PaddingSM),
                thickness = 0.5.dp,
                color = Color.LightGray
            )
        }
    }
}