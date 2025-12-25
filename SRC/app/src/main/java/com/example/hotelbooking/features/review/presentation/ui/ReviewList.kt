package com.example.hotelbooking.features.review.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTitle
import com.example.hotelbooking.features.review.domain.model.Review
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.JostTypography

@Composable
fun ReviewList(list: List<Review>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AppTitle(
            text1 = stringResource(id = R.string.reviews),
            text2 = stringResource(id = R.string.see_all),
            onClick = {

            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.PaddingS),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.M)
        ) {
            list.take(4).forEach { review ->
                ReviewItem(review)
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.user_avatar),
            contentDescription = null,
            modifier = Modifier.size(Dimen.SizeXXL)
        )

        Spacer(modifier = Modifier.width(AppSpacing.S))

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = review.userName,
                    style = JostTypography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                    )
                )

                Text(
                    text = "⭐${review.rating}",
                    style = JostTypography.bodyLarge.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                    )
                )
            }

            Text(
                text = review.comment,
                style = JostTypography.bodyLarge.copy(
                    fontSize = 15.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp,
                )
            )
        }
    }
}