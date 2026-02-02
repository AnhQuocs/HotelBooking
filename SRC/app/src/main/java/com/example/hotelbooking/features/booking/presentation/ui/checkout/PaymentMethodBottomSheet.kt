package com.example.hotelbooking.features.booking.presentation.ui.checkout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.profile.payment_card.domain.model.PaymentBrand
import com.example.hotelbooking.features.profile.payment_card.domain.model.PaymentCard
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.InputBackground
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.SurfaceSoftBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodBottomSheet(
    cards: List<PaymentCard>,
    onDismissRequest: () -> Unit,
    onNextClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    val sortedCards = cards.sortedByDescending { it.isDefault }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        containerColor = SurfaceSoftBlue
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimen.PaddingM)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.payment_method),
                    style = AfacadTypography.bodyLarge.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                )

                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier
                        .size(Dimen.SizeM)
                        .clickable { onDismissRequest() }
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.L))

            PaymentMethodRadioButton(cards = sortedCards)

            Spacer(modifier = Modifier.height(AppSpacing.L))

            Box(
                modifier = Modifier
                    .height(Dimen.HeightLarge)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppShape.ShapeM))
                    .background(Color.White, RoundedCornerShape(AppShape.ShapeM)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimen.PaddingSM, vertical = Dimen.PaddingS),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(Dimen.SizeXLPlus)
                            .clip(RoundedCornerShape(AppShape.ShapeS))
                            .background(InputBackground, RoundedCornerShape(AppShape.ShapeS)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(Dimen.SizeM)
                        )
                    }

                    Spacer(modifier = Modifier.width(AppSpacing.S))

                    Text(
                        text = stringResource(R.string.add_debit_card),
                        style = AfacadTypography.bodyMedium.copy(
                            fontSize = 15.sp,
                            color = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimen.HeightML))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimen.PaddingM),
                contentAlignment = Alignment.BottomEnd
            ) {
                Button(
                    onClick = { onNextClick() },
                    modifier = Modifier
                        .widthIn(min = Dimen.WidthL)
                        .height(50.dp),
                    shape = RoundedCornerShape(AppShape.ShapeL),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlueNavy,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = stringResource(R.string.confirm_and_pay))
                }
            }
        }
    }
}

@Composable
fun PaymentMethodRadioButton(
    cards: List<PaymentCard>
) {
    val defaultCard = cards.firstOrNull { it.isDefault }

    var selected by remember {
        mutableStateOf(defaultCard ?: cards.first())
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.MediumLarge)
    ) {
        cards.forEach { card ->
            PaymentCardItem(
                icon = painterResource(card.brand.icon()),
                text = card.brand.displayName(),
                radioButton = {
                    RadioButton(
                        selected = selected == card,
                        onClick = { selected = card },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFF0A3A7A)
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun PaymentCardItem(
    icon: Painter,
    text: String,
    radioButton: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .height(Dimen.HeightLarge)
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppShape.ShapeM))
            .background(Color.White, RoundedCornerShape(AppShape.ShapeM)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(Dimen.SizeXL)
            )

            Spacer(modifier = Modifier.width(AppSpacing.S))

            Text(
                text = text,
                style = AfacadTypography.bodyMedium.copy(
                    fontSize = 15.sp,
                    color = Color.Black,
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            radioButton()
        }
    }
}

@Composable
fun PaymentBrand.displayName(): String =
    when (this) {
        PaymentBrand.MASTERCARD -> stringResource(R.string.master_card)
        PaymentBrand.VISA -> stringResource(R.string.visa)
        PaymentBrand.JCB -> stringResource(R.string.jcb)
    }

fun PaymentBrand.icon(): Int =
    when (this) {
        PaymentBrand.MASTERCARD -> R.drawable.ic_mastercard
        PaymentBrand.VISA -> R.drawable.ic_visa
        PaymentBrand.JCB -> R.drawable.ic_jcb
    }