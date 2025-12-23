package com.example.hotelbooking.features.auth.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.InputBackground
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.SlateGray

@Composable
fun AuthOutlinedTextField(
    icon: Int,
    value: String,
    onValueChange: (String) -> Unit,
    title: String,
    placeholder: String,
    isError: Boolean,
    errorMessage: String,
    isTrailingIcon: Boolean = false
) {
    var isShowPassword by remember { mutableStateOf(false) }

    val colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        focusedContainerColor = InputBackground,
        unfocusedContainerColor = InputBackground,
        errorContainerColor = InputBackground,
        errorBorderColor = Color.Red,
        cursorColor = Color.Black
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            color = Color.Black,
            style = JostTypography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
        )

        Spacer(modifier = Modifier.height(Dimen.PaddingXS))

        OutlinedTextField(
            value = value,
            onValueChange = { newValue -> onValueChange(newValue) },
            placeholder = { Text(placeholder, fontSize = 15.sp, color = SlateGray) },
            shape = RoundedCornerShape(AppShape.ShapeM),
            textStyle = TextStyle(color = Color.Black),
            colors = colors,
            leadingIcon = {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = SlateGray,
                    modifier = Modifier.size(24.dp)
                )
            },
            trailingIcon = {
                if (isTrailingIcon) {
                    IconButton(
                        onClick = { isShowPassword = !isShowPassword }
                    ) {
                        Icon(
                            if (isShowPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            tint = SlateGray,
                            contentDescription = null
                        )
                    }
                }
            },
            singleLine = true,
            isError = isError,
            visualTransformation = if (isTrailingIcon && !isShowPassword) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (isError && errorMessage.isNotEmpty()) {
            Text(
                errorMessage,
                color = Color.Red,
                fontSize = 12.sp
            )
        }
    }
}