package com.example.hotelbooking.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun ReadMoreText(
    description: String,
    maxLine: Int = 2,
) {
    var expanded by remember { mutableStateOf(false) }
    var canOverflow by remember { mutableStateOf(false) }

    Column {
        Text(
            text = description,
            style = JostTypography.titleMedium.copy(
                fontSize = 18.sp,
                color = Color.Gray,
                lineHeight = 18.sp,
            ),
            maxLines = if (expanded) Int.MAX_VALUE else maxLine,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
            onTextLayout = { textLayoutResult ->
                if (!expanded) {
                    canOverflow = textLayoutResult.hasVisualOverflow
                }
            })

        if (canOverflow) {
            Text(
                text = if (expanded)
                    stringResource(id = R.string.show_less)
                else
                    stringResource(id = R.string.read_more),
                style = JostTypography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = PrimaryBlue,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    expanded = !expanded
                }
            )
        }
    }
}