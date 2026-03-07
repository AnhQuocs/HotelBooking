package com.example.hotelbooking.features.profile.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.components.InfoTitle
import com.example.hotelbooking.components.LineGray
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography

@Composable
fun LegalSupportSection(
    onHelpCenterClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onTermsClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dimen.PaddingM)
    ) {

        InfoTitle(text = stringResource(id = R.string.legal_support))

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
                    Icons.Default.HelpOutline,
                    stringResource(id = R.string.help_center),
                    onClick = onHelpCenterClick
                )

                LineGray()

                ProfileItem(
                    Icons.Default.PrivacyTip,
                    stringResource(id = R.string.privacy_policy),
                    onClick = onPrivacyPolicyClick
                )

                LineGray()

                ProfileItem(
                    Icons.Default.Gavel,
                    stringResource(id = R.string.terms_of_service),
                    onClick = onTermsClick
                )
            }
        }
    }
}

@Composable
fun Setting(
    onPersonalInfoClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onAppearanceClick: () -> Unit,
    onCurrencyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dimen.PaddingM)
    ) {
        InfoTitle(text = stringResource(id = R.string.setting_title))

        Spacer(modifier = Modifier.height(AppSpacing.S))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(AppShape.ShapeS))
                .wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            Column {
                ProfileItem(
                    Icons.Default.Person,
                    stringResource(id = R.string.personal_information),
                    onClick = { onPersonalInfoClick() })
                LineGray()
                ProfileItem(
                    Icons.Default.Language,
                    stringResource(id = R.string.language),
                    onClick = { onLanguageClick() })
                LineGray()
                ProfileItem(
                    Icons.Default.Contrast,
                    stringResource(id = R.string.appearance),
                    onClick = { onAppearanceClick() })
                LineGray()
                ProfileItem(
                    Icons.Default.AttachMoney,
                    stringResource(id = R.string.currency),
                    onClick = { onCurrencyClick() })
            }
        }
    }
}

@Composable
fun PaymentInformation(
    onPromotionsClick: () -> Unit,
    onPaymentMethodClick: () -> Unit,
    onTransactionsClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimen.PaddingM)
    ) {
        InfoTitle(text = stringResource(id = R.string.payment_info))

        Spacer(modifier = Modifier.height(AppSpacing.S))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(AppShape.ShapeS))
                .wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            Column {
                ProfileItem(
                    Icons.Default.Discount,
                    text = stringResource(id = R.string.promotions),
                    onClick = { onPromotionsClick() })
                LineGray()
                ProfileItem(
                    Icons.Default.Payment,
                    text = stringResource(id = R.string.payment_method),
                    onClick = { onPaymentMethodClick() })
                LineGray()
                ProfileItem(
                    Icons.Default.Receipt,
                    text = stringResource(id = R.string.transactions),
                    onClick = { onTransactionsClick() })
            }
        }
    }
}

@Composable
fun ProfileItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimen.PaddingSM)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.Black.copy(alpha = 0.7f),
            modifier = Modifier.size(Dimen.SizeM)
        )

        Spacer(Modifier.width(AppSpacing.MediumLarge))

        Text(
            text = text, style = AfacadTypography.titleMedium.copy(
                fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Normal
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            Icons.Default.ArrowForwardIos,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(Dimen.SizeS)
        )
    }
}