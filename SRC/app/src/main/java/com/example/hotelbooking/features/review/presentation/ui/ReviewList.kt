package com.example.hotelbooking.features.review.presentation.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTitle
import com.example.hotelbooking.features.review.domain.model.HotelReviewSummary
import com.example.hotelbooking.features.review.domain.model.Review
import com.example.hotelbooking.features.review.domain.model.ReviewStatus
import com.example.hotelbooking.features.review.presentation.util.TimeUtils
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun ReviewList(list: HotelReviewSummary, onSeeAllClick: () -> Unit) {
    val reviewList = list.reviews

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AppTitle(
            text1 = stringResource(id = R.string.reviews),
            text2 = stringResource(id = R.string.see_all),
            onClick = { onSeeAllClick() }
        )

        if (list.reviews.isEmpty()) {
            Text(
                text = stringResource(R.string.dashboard_no_reviews),
                color = Color.Red,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = Dimen.PaddingSM)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimen.PaddingS),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.M)
            ) {
                reviewList.take(3).forEach { review ->
                    ReviewItem(review)
                }
            }
        }
    }
}

@Composable
fun ReviewItem(
    review: Review,
    isAdmin: Boolean = false,
    onToggleStatus: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current

    val displayTime = remember(review.timestamp) {
        TimeUtils.getRelativeTime(context, review.timestamp)
    }

    val targetAlpha = if (isAdmin && review.status == ReviewStatus.HIDE) 0.5f else 1f
    val animatedAlpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = 300),
        label = "ReviewAlphaAnimation"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(animatedAlpha),
        verticalAlignment = Alignment.Top
    ) {
        if(review.userProfilePicture.isEmpty()) {
            Image(
                painter = painterResource(id = R.drawable.user_avatar),
                contentDescription = null,
                modifier = Modifier
                    .size(Dimen.SizeXXL)
                    .clip(CircleShape)
            )
        } else {
            AsyncImage(
                model = review.userProfilePicture,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(Dimen.SizeXXL - 5.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(AppSpacing.S))

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = review.userName,
                    style = AfacadTypography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                    ),
                    modifier = Modifier.weight(1f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⭐${review.rating}",
                        style = AfacadTypography.bodyLarge.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                        )
                    )

                    if (isAdmin) {
                        Spacer(modifier = Modifier.width(AppSpacing.S))
                        Switch(
                            checked = review.status == ReviewStatus.ACTIVE,
                            onCheckedChange = onToggleStatus,
                            modifier = Modifier.scale(0.7f),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PrimaryBlue,
                                checkedTrackColor = PrimaryBlue.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }

            Text(
                text = review.comment,
                style = AfacadTypography.bodyLarge.copy(
                    fontSize = 15.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp,
                )
            )

            Text(
                text = displayTime,
                style = AfacadTypography.bodySmall.copy(fontSize = 12.sp),
                color = Color.LightGray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}