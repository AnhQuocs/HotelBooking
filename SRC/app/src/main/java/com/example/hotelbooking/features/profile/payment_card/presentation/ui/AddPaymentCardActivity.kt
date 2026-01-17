package com.example.hotelbooking.features.profile.payment_card.presentation.ui

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.features.profile.payment_card.domain.model.PaymentBrand
import com.example.hotelbooking.features.profile.payment_card.domain.model.PaymentCard
import com.example.hotelbooking.features.profile.payment_card.presentation.viewmodel.PaymentCardState
import com.example.hotelbooking.features.profile.payment_card.presentation.viewmodel.PaymentCardViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.ErrorRed
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.SlateGray
import com.example.hotelbooking.utils.removeAccents
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class AddPaymentCardActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val activity = context as? Activity
            val toastText = stringResource(id = R.string.add_card_success)

            AddPaymentCardScreen(
                onBackClick = {
                    activity?.finish()
                },
                onSuccess = {
                    Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()

                    activity?.setResult(RESULT_OK)

                    activity?.finish()
                }
            )
        }
    }
}

@Composable
fun AddPaymentCardScreen(
    viewModel: PaymentCardViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    val currentUserId = remember { FirebaseAuth.getInstance().currentUser?.uid ?: "" }

    var cardNumber by remember { mutableStateOf("") }
    var holderName by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    val brand = remember(cardNumber) {
        when {
            cardNumber.startsWith("4") -> PaymentBrand.VISA
            cardNumber.startsWith("5") -> PaymentBrand.MASTERCARD
            cardNumber.startsWith("3") -> PaymentBrand.JCB
            else -> PaymentBrand.VISA
        }
    }

    val textFieldColor = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = SlateGray,
        errorBorderColor = ErrorRed,
        cursorColor = PrimaryBlue
    )

    val cardState by viewModel.cardState.collectAsState()
    LaunchedEffect(cardState) {
        when (cardState) {
            is PaymentCardState.Success -> {
                if ((cardState as PaymentCardState.Success<PaymentCard?>).data == null) {
                    onSuccess()
                    viewModel.resetCardState()
                }
            }
            is PaymentCardState.Error -> {

            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                text = stringResource(id = R.string.add_new_card),
                onBackClick = { onBackClick() }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(Dimen.PaddingM),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PaymentCardItem(
                card = PaymentCard(
                    id = "",
                    userId = "",
                    brand = brand,
                    cardNumber = cardNumber.ifEmpty { "0000000000000000" },
                    holderName = holderName.removeAccents().uppercase().ifEmpty { stringResource(id = R.string.card_holder) },
                    expiryMonth = if (expiryDate.length >= 2) expiryDate.take(2).toIntOrNull()
                        ?: 12 else 12,
                    expiryYear = if (expiryDate.length == 4) "20${expiryDate.takeLast(2)}".toIntOrNull()
                        ?: 2024 else 2024,
                    cvv = cvv,
                    isDefault = false
                )
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            OutlinedTextField(
                value = cardNumber,
                shape = RoundedCornerShape(AppShape.ShapeL),
                onValueChange = {
                    if (it.length <= 16) cardNumber = it.filter { char -> char.isDigit() }
                },
                colors = textFieldColor,
                label = { Text(stringResource(id = R.string.card_number)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = CardNumberTransformation()
            )

            Spacer(modifier = Modifier.height(AppSpacing.M))

            OutlinedTextField(
                value = holderName,
                shape = RoundedCornerShape(AppShape.ShapeL),
                onValueChange = { holderName = it },
                colors = textFieldColor,
                label = { Text(stringResource(id = R.string.card_holder_name)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("NGUYEN VAN A") }
            )

            Spacer(modifier = Modifier.height(AppSpacing.M))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = {
                        if (it.length <= 4) expiryDate = it.filter { char -> char.isDigit() }
                    },
                    colors = textFieldColor,
                    label = { Text(stringResource(id = R.string.card_expiry_date)) },
                    shape = RoundedCornerShape(AppShape.ShapeL),
                    modifier = Modifier.weight(1.5f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.width(AppSpacing.M))
                OutlinedTextField(
                    value = cvv,
                    onValueChange = { if (it.length <= 4) cvv = it.filter { char -> char.isDigit() } },
                    label = { Text("CVV") },
                    colors = textFieldColor,
                    shape = RoundedCornerShape(AppShape.ShapeL),
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.XL))

            Button(
                onClick = {
                    val cardToSave = PaymentCard(
                        id = UUID.randomUUID().toString(),
                        userId = currentUserId,
                        brand = brand,
                        cardNumber = cardNumber,
                        holderName = holderName.removeAccents().uppercase().trim(),
                        expiryMonth = expiryDate.take(2).toInt(),
                        expiryYear = "20${expiryDate.takeLast(2)}".toInt(),
                        cvv = cvv,
                        isDefault = false
                    )
                    viewModel.createPaymentCard(cardToSave)
                },
                enabled = cardState !is PaymentCardState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimen.HeightLarge),
                shape = RoundedCornerShape(AppShape.ShapeL),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (cardState is PaymentCardState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(id = R.string.save_card))
                }
            }
        }
    }
}

class CardNumberTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 16) text.text.substring(0..15) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i % 4 == 3 && i != 15) out += " "
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 7) return offset + 1
                if (offset <= 11) return offset + 2
                if (offset <= 16) return offset + 3
                return 19
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 4) return offset
                if (offset <= 9) return offset - 1
                if (offset <= 14) return offset - 2
                if (offset <= 19) return offset - 3
                return 16
            }
        }
        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}