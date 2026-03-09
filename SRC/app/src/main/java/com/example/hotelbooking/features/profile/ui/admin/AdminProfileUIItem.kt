package com.example.hotelbooking.features.profile.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.components.InfoTitle
import com.example.hotelbooking.components.LineGray
import com.example.hotelbooking.features.profile.ui.component.ProfileItem
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen

@Composable
fun OperationsCenterSection(
    onRevenueClick: () -> Unit,
    onOpenGalleryClick: () -> Unit,
    onManageVoucherClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimen.PaddingM)
    ) {
        InfoTitle(text = stringResource(id = R.string.operations_center))

        Spacer(modifier = Modifier.height(AppSpacing.S))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, Color.LightGray, RoundedCornerShape(AppShape.ShapeS))
                .background(Color.White, RoundedCornerShape(AppShape.ShapeS))
                .wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            Column {
                ProfileItem(
                    Icons.Default.AttachMoney,
                    stringResource(id = R.string.revenue),
                    onClick = onRevenueClick
                )

                LineGray()

                ProfileItem(
                    Icons.Default.PermMedia,
                    stringResource(id = R.string.media_library),
                    onClick = onOpenGalleryClick
                )

                LineGray()

                ProfileItem(
                    Icons.Default.ConfirmationNumber,
                    stringResource(id = R.string.voucher),
                    onClick = onManageVoucherClick
                )
            }
        }
    }
}