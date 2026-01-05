package com.example.hotelbooking.features.profile

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        val context = LocalContext.current

        Text("Profile Screen", fontSize = 20.sp, color = Color.Black)

        OptionItem(
            iconRes = R.drawable.ic_language,
            text = stringResource(id = R.string.language),
            onClick = {
                context.startActivity(Intent(context, ChangeLanguageActivity::class.java))
            }
        )
    }
}

@Composable
fun OptionItem(
    @DrawableRes iconRes: Int,
    text: String,
    onClick: () -> Unit
) {
    val color = Color(0xFF767E8C)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimen.PaddingXXS),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color),
            modifier = Modifier.size(Dimen.SizeM)
        )

        Spacer(modifier = Modifier.width(AppSpacing.SPlus))

        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.titleSmall.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = onClick
        ) {
            Icon(
                Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = Color.Black.copy(0.4f),
                modifier = Modifier.size(Dimen.SizeSM)
            )
        }
    }
}