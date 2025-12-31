package com.example.hotelbooking.features.booking.presentation.ui.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryState
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingWithHotel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.SurfaceGray
import com.example.hotelbooking.ui.theme.TextPrimaryDark
import com.example.hotelbooking.ui.theme.TextTertiary

@Composable
fun BookingHistoryScreen(
    bookingHistoryState: BookingHistoryState<List<BookingWithHotel>>,
    onDetailClick: (String, String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth().height(70.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = stringResource(id = R.string.my_booking),
                    style = JostTypography.titleLarge.copy(
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        containerColor = Color.White,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxSize()
                .padding(horizontal = Dimen.PaddingM)
                .padding(top = Dimen.PaddingM)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text(
                    stringResource(id = R.string.search), fontSize = 15.sp) },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(TextTertiary),
                        modifier = Modifier.size(Dimen.SizeSM)
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .height(Dimen.HeightXXS)
                                .width(1.dp)
                                .background(color = Color.LightGray)
                        )

                        Spacer(modifier = Modifier.width(AppSpacing.XS))

                        Icon(
                            Icons.Default.FilterAlt,
                            contentDescription = null,
                            tint = TextPrimaryDark,
                            modifier = Modifier.size(Dimen.SizeSM)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SurfaceGray,
                    unfocusedBorderColor = SurfaceGray
                ),
                shape = RoundedCornerShape(AppShape.ShapeXL2),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            BookingHistorySection(bookingHistoryState, onDetailClick)
        }
    }
}