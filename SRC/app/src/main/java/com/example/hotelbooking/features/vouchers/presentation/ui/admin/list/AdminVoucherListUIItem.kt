package com.example.hotelbooking.features.vouchers.presentation.ui.admin.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.vouchers.domain.model.AdminVoucher
import com.example.hotelbooking.features.vouchers.domain.model.DiscountType
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.DiscountPink
import com.example.hotelbooking.ui.theme.SurfaceLight

@Composable
fun HotelFilterSection(
    hotels: List<Hotel>, selectedHotelId: String?, onHotelSelected: (String?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Dimen.PaddingM, vertical = Dimen.PaddingS),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.S),
        modifier = Modifier.background(Color.White)
    ) {
        item {
            FilterChip(
                selected = selectedHotelId == null,
                onClick = { onHotelSelected(null) },
                label = { Text(stringResource(id = R.string.status_all)) })
        }
        items(hotels) { hotel ->
            FilterChip(
                selected = selectedHotelId == hotel.id,
                onClick = { onHotelSelected(hotel.id) },
                label = { Text(hotel.name) })
        }
    }
}

@Composable
fun EmptyVoucherContent(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ConfirmationNumber,
            contentDescription = null,
            modifier = Modifier.size(Dimen.SizeMega),
            tint = Color.LightGray
        )
        Spacer(Modifier.height(AppSpacing.MediumLarge))
        Text(
            stringResource(R.string.no_promotions_yet), color = Color.Gray
        )
        Spacer(Modifier.height(AppSpacing.L))
        Button(
            onClick = onAddClick, colors = ButtonDefaults.buttonColors(containerColor = BlueNavy)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(AppSpacing.S))
            Text(stringResource(R.string.create_first_promotion))
        }
    }
}

@Composable
fun AdminVoucherCard(
    voucher: AdminVoucher, onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(AppShape.ShapeM)
    ) {
        Column(modifier = Modifier.padding(Dimen.PaddingM)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = voucher.code, style = AfacadTypography.titleMedium.copy(
                            fontWeight = FontWeight.Bold, color = BlueNavy
                        )
                    )
                    Text(text = voucher.title, style = AfacadTypography.bodyMedium)
                }

                Switch(
                    checked = voucher.isActive,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White, checkedTrackColor = BlueNavy
                    )
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = Dimen.PaddingSM), thickness = 0.5.dp
            )

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        stringResource(R.string.voucher_used),
                        style = AfacadTypography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "${voucher.usedCount} / ${voucher.totalQuantity}",
                        style = AfacadTypography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        stringResource(R.string.voucher_discount_value),
                        style = AfacadTypography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = if (voucher.discountType == DiscountType.PERCENTAGE) "${voucher.discountValue.toInt()}%"
                        else "${voucher.discountValue.toInt()}$",
                        style = AfacadTypography.bodyLarge.copy(
                            color = DiscountPink, fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            if (voucher.isSoldOut) {
                Surface(
                    color = SurfaceLight,
                    shape = RoundedCornerShape(AppShape.ShapeXXS),
                    modifier = Modifier.padding(top = Dimen.PaddingS)
                ) {
                    Text(
                        text = stringResource(R.string.voucher_sold_out),
                        color = Color.Red,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(
                            horizontal = Dimen.PaddingS, vertical = Dimen.PaddingXXS
                        )
                    )
                }
            }
        }
    }
}