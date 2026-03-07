package com.example.hotelbooking.features.booking.presentation.ui.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.vouchers.domain.model.DiscountType
import com.example.hotelbooking.features.vouchers.domain.model.Voucher
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.LightBlueBackground
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.SurfaceSoftBlue

@Composable
fun PromoUI(
    appliedVoucher: Voucher?,
    onClick: () -> Unit
) {
    Text(
        text = stringResource(R.string.promo),
        style = AfacadTypography.bodyLarge.copy(fontWeight = FontWeight.Medium, color = Color.Black)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimen.HeightLarge)
            .padding(top = Dimen.PaddingXSPlus)
            .clip(RoundedCornerShape(AppShape.ShapeL))
            .background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(AppShape.ShapeL))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimen.PaddingSM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_promo),
                contentDescription = null,
                modifier = Modifier.size(Dimen.SizeM)
            )

            Spacer(modifier = Modifier.width(AppSpacing.M))

            if (appliedVoucher != null) {
                Column {
                    Text(
                        text = stringResource(
                            R.string.promo_applied_code,
                            appliedVoucher.code
                        ),
                        style = AfacadTypography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    )

                    Text(
                        text = if (appliedVoucher.discountType == DiscountType.PERCENTAGE)
                            stringResource(
                                R.string.promo_discount_percent,
                                appliedVoucher.discountValue.toInt()
                            )
                        else
                            stringResource(
                                R.string.promo_discount_amount,
                                appliedVoucher.discountValue.toInt()
                            ),
                        style = AfacadTypography.bodySmall.copy(color = Color.Gray)
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.select_promo),
                    style = AfacadTypography.bodyMedium.copy(fontSize = 15.sp, color = PrimaryBlue)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (appliedVoucher != null) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(Dimen.SizeSM)
                )
            } else {
                Icon(
                    Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    tint = PrimaryBlue.copy(alpha = 0.8f),
                    modifier = Modifier.size(Dimen.SizeSM)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherSelectionBottomSheet(
    vouchers: List<Voucher>,
    selectedVoucher: Voucher?,
    onDismiss: () -> Unit,
    onSelect: (Voucher?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimen.PaddingM, vertical = Dimen.PaddingS)
        ) {
            Text(
                text = stringResource(R.string.select_promotion),
                style = AfacadTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = Dimen.PaddingM)
            )

            if (vouchers.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_promotion_available),
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = Dimen.PaddingXL)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = Dimen.PaddingXL),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.M)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(null) }
                                .padding(vertical = Dimen.PaddingS),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedVoucher == null,
                                onClick = { onSelect(null) }
                            )
                            Text(stringResource(R.string.no_promotion))
                        }
                    }

                    items(vouchers) { voucher ->
                        val isSelected = selectedVoucher?.id == voucher.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(voucher) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) LightBlueBackground else SurfaceSoftBlue
                            ),
                            border = if (isSelected) BorderStroke(1.dp, PrimaryBlue) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(Dimen.PaddingM),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        voucher.code,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue
                                    )
                                    Text(voucher.title, style = AfacadTypography.bodyMedium)

                                    Spacer(modifier = Modifier.height(AppSpacing.XS))

                                    Text(
                                        text = stringResource(
                                            R.string.voucher_min_order,
                                            voucher.minOrderValue.toInt()
                                        ),
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )

                                    val isAlmostEmpty =
                                        voucher.usedCount >= (voucher.totalQuantity * 0.8)
                                    Text(
                                        text = stringResource(
                                            R.string.voucher_used_count,
                                            voucher.usedCount,
                                            voucher.totalQuantity
                                        ),
                                        fontSize = 12.sp,
                                        color = if (isAlmostEmpty) Color(0xFFE53935) else Color.Gray,
                                        fontWeight = if (isAlmostEmpty) FontWeight.Bold else FontWeight.Normal
                                    )
                                }

                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onSelect(voucher) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}