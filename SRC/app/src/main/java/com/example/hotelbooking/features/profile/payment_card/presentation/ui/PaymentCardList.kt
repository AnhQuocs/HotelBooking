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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.profile.payment_card.domain.model.PaymentBrand
import com.example.hotelbooking.features.profile.payment_card.domain.model.PaymentCard

@Composable
fun PaymentCardList(list: List<PaymentCard>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "My Cards",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
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
                    .padding(8.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Add New Payment Method")
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
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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
                modifier = Modifier.size(50.dp).align(Alignment.TopEnd)
            )

            Canvas(
                modifier = Modifier
                    .size(45.dp, 32.dp)
                    .align(Alignment.TopStart)
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.dp, Color(0xFFB8860B), RoundedCornerShape(6.dp))
            ) {
                val metallicBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFFACD),
                        Color(0xFFFFD700),
                        Color(0xFFDAA520)
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
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "CARD HOLDER", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                        Text(text = card.holderName.uppercase(), style = MaterialTheme.typography.bodyLarge, color = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "EXPIRES", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                        Text(
                            text = "${card.expiryMonth.toString().padStart(2, '0')}/${card.expiryYear.toString().takeLast(2)}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
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
                        text = "Default",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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
            Brush.linearGradient(listOf(Color(0xFF1A237E), Color(0xFF283593))),
            R.drawable.ic_visa
        )
        PaymentBrand.MASTERCARD -> Pair(
            Brush.linearGradient(listOf(Color(0xFF37474F), Color(0xFF263238))),
            R.drawable.ic_mastercard
        )
        PaymentBrand.JCB -> Pair(
            Brush.linearGradient(listOf(Color(0xFF004D40), Color(0xFF00695C))),
            R.drawable.ic_jcb
        )
    }
}