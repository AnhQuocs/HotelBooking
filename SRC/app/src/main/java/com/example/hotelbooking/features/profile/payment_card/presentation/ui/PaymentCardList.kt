package com.example.hotelbooking.features.profile.payment_card.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.profile.payment_card.domain.model.PaymentBrand
import com.example.hotelbooking.features.profile.payment_card.domain.model.PaymentCard
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.GoldBorder
import com.example.hotelbooking.ui.theme.GoldHighlight
import com.example.hotelbooking.ui.theme.GoldPrimary
import com.example.hotelbooking.ui.theme.GoldShadow
import com.example.hotelbooking.ui.theme.JcbGreenDark
import com.example.hotelbooking.ui.theme.JcbGreenLight
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.MasterCardGrayDark
import com.example.hotelbooking.ui.theme.MasterCardGrayLight
import com.example.hotelbooking.ui.theme.VisaBlueDark
import com.example.hotelbooking.ui.theme.VisaBlueLight

@Composable
fun PaymentCardList(list: List<PaymentCard>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimen.PaddingSM),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
    ) {
        item {
            Text(
                text = stringResource(id = R.string.my_cards),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = Dimen.PaddingM, start = Dimen.PaddingS)
            )
        }

        items(list, key = { it.id }) { card ->
            PaymentCardItem(card = card)
        }

        item {
            OutlinedButton(
                onClick = {

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.PaddingS)
                    .height(60.dp),
                shape = RoundedCornerShape(AppShape.ShapeM)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text(stringResource(id = R.string.add_new_card))
            }
        }
    }
}

@Composable
fun PaymentCardItem(
    card: PaymentCard,
    onClick: () -> Unit = {}
) {
    val (backgroundBrush, logoRes) = getCardDesign(card.brand)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.586f)
            .clickable { onClick() },
        shape = RoundedCornerShape(AppShape.ShapeL),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(20.dp)
        ) {
            Image(
                painter = painterResource(id = logoRes),
                contentDescription = null,
                modifier = Modifier.size(Dimen.SizeXXL).align(Alignment.TopEnd)
            )

            Canvas(
                modifier = Modifier
                    .size(45.dp, 32.dp)
                    .align(Alignment.TopStart)
                    .clip(RoundedCornerShape(AppShape.ShapeXS))
                    .border(
                        1.dp,
                        GoldBorder,
                        RoundedCornerShape(AppShape.ShapeXS)
                    )
            ) {
                val metallicBrush = Brush.linearGradient(
                    colors = listOf(
                        GoldHighlight,
                        GoldPrimary,
                        GoldShadow
                    ),
                    start = Offset.Zero,
                    end = Offset(size.width, size.height)
                )

                drawRect(brush = metallicBrush)

                val lineColor = Color(0xFF8B4513).copy(alpha = 0.6f)
                val lineStroke = 1.5.dp.toPx()

                drawLine(
                    color = lineColor,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = lineStroke
                )

                drawLine(
                    color = lineColor,
                    start = Offset(size.width / 3, 0f),
                    end = Offset(size.width / 3, size.height),
                    strokeWidth = lineStroke
                )

                drawLine(
                    color = lineColor,
                    start = Offset(size.width * 2 / 3, 0f),
                    end = Offset(size.width * 2 / 3, size.height),
                    strokeWidth = lineStroke
                )

                drawRect(
                    color = lineColor,
                    topLeft = Offset(size.width / 2 - 4.dp.toPx(), size.height / 2 - 3.dp.toPx()),
                    size = Size(8.dp.toPx(), 6.dp.toPx()),
                    style = Stroke(width = lineStroke)
                )
            }

            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text(
                    text = "**** **** **** ${card.cardNumber.takeLast(4)}",
                    style = JostTypography.headlineSmall.copy(
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(id = R.string.card_holder),
                            style = JostTypography.labelMedium.copy(
                                color = Color.LightGray
                            )
                        )
                        Text(
                            text = card.holderName.uppercase(),
                            style = JostTypography.bodyLarge.copy(
                                color = Color.White
                            )
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = stringResource(id = R.string.expires),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.LightGray)
                        Text(
                            text = "${card.expiryMonth.toString().padStart(2, '0')}/${card.expiryYear.toString().takeLast(2)}",
                            style = JostTypography.bodyLarge.copy(
                                color = Color.White
                            )
                        )
                    }
                }
            }

            if (card.isDefault) {
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text(
                        text = stringResource(id = R.string.card_default),
                        modifier = Modifier.padding(horizontal = Dimen.PaddingS, vertical = Dimen.PaddingXS),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun getCardDesign(brand: PaymentBrand): Pair<Brush, Int> {
    return when (brand) {
        PaymentBrand.VISA -> Pair(
            Brush.linearGradient(
                listOf(
                    VisaBlueDark,
                    VisaBlueLight
                )
            ),
            R.drawable.ic_visa
        )

        PaymentBrand.MASTERCARD -> Pair(
            Brush.linearGradient(
                listOf(
                    MasterCardGrayLight,
                    MasterCardGrayDark
                )
            ),
            R.drawable.ic_mastercard
        )

        PaymentBrand.JCB -> Pair(
            Brush.linearGradient(
                listOf(
                    JcbGreenDark,
                    JcbGreenLight
                )
            ),
            R.drawable.ic_jcb
        )
    }
}