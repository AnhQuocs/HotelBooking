package com.example.hotelbooking.features.room.presentation.ui.admin.detail

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.NearBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDetailTopBar(
    title: String,
    onBack: () -> Unit,
    showSwitch: Boolean = false,
    isActive: Boolean = false,
    onStatusChange: (Boolean) -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = AfacadTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            if (showSwitch) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = AppSpacing.S)
                ) {
                    Text(
                        text = if (isActive) stringResource(id = R.string.status_active) else stringResource(
                            id = R.string.status_hidden
                        ),
                        style = AfacadTypography.labelSmall,
                        color = if (isActive) AvailableGreen else Color.Gray
                    )

                    Spacer(modifier = Modifier.width(AppSpacing.XS))

                    Switch(
                        checked = isActive,
                        onCheckedChange = onStatusChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AvailableGreen,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.LightGray.copy(alpha = 0.5f),
                            uncheckedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.scale(0.8f)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            navigationIconContentColor = NearBlack,
            titleContentColor = NearBlack
        )
    )
}

@Composable
fun RuleItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    isAllowed: Boolean
) {
    val statusColor = if (isAllowed) AvailableGreen else Color.Red.copy(alpha = 0.7f)

    Row(
        modifier = modifier
            .border(0.5.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(AppSpacing.S))
            .padding(AppSpacing.S),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = statusColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(AppSpacing.S))
        Column {
            Text(text = label, style = AfacadTypography.labelSmall, color = Color.Gray)
            Text(
                text = value,
                style = AfacadTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = statusColor
            )
        }
    }
}