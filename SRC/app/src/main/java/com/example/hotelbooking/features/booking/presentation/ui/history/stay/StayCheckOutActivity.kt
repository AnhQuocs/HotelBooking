package com.example.hotelbooking.features.booking.presentation.ui.history.stay

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.presentation.viewmodel.user.HotelState
import com.example.hotelbooking.features.hotel.presentation.viewmodel.user.HotelViewModel
import com.example.hotelbooking.features.hotel.presentation.viewmodel.user.UpdateRatingState
import com.example.hotelbooking.features.hotel.presentation.viewmodel.user.UpdateRatingViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.RatingYellow
import com.example.hotelbooking.ui.theme.SurfaceSoftBlue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StayCheckOutActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val hotelId = intent.getStringExtra("hotelId") ?: ""

        setContent {
            val hotelViewModel: HotelViewModel = hiltViewModel()
            val hotelState by hotelViewModel.hotelDetailState.collectAsState()

            LaunchedEffect(hotelId) {
                hotelViewModel.loadHotelById(hotelId)
            }

            when (val currentState = hotelState) {
                is HotelState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }

                is HotelState.Success -> {
                    StayCheckOutScreen(
                        hotel = currentState.data,
                        onDismiss = { finish() },
                        onRatingSuccess = { finish() })
                }

                is HotelState.Error -> {
                    Text(
                        text = stringResource(id = R.string.error, currentState.message)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StayCheckOutScreen(
    hotel: Hotel,
    updateRatingViewModel: UpdateRatingViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onRatingSuccess: () -> Unit
) {
    val updateState by updateRatingViewModel.updateRatingState.collectAsState()

    var rating by remember { mutableDoubleStateOf(0.0) }
    var comment by remember { mutableStateOf("") }

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val thankYouToastText = stringResource(id = R.string.thank_you_for_review)

    LaunchedEffect(updateState) {
        if (updateState is UpdateRatingState.Success) {
            Toast.makeText(context, thankYouToastText, Toast.LENGTH_SHORT).show()
            onRatingSuccess()
        }
    }

    Scaffold(
        containerColor = SurfaceSoftBlue, topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimen.PaddingM, end = Dimen.PaddingM),
                contentAlignment = Alignment.CenterEnd
            ) {
                IconButton(
                    onClick = onDismiss,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "", tint = Color.Gray)
                }
            }
        }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = Dimen.SizeL)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(AppSpacing.SPlus))

            Text(
                text = stringResource(id = R.string.review_title),
                style = AfacadTypography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = BlueNavy
            )

            Text(
                text = stringResource(id = R.string.review_subtitle),
                style = AfacadTypography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = Dimen.PaddingS, bottom = Dimen.PaddingL)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(AppShape.ShapeL),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(Dimen.PaddingM),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = hotel.thumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(AppShape.ShapeM))
                            .background(Color.LightGray)
                    )

                    Spacer(modifier = Modifier.width(AppSpacing.MediumLarge))

                    Column {
                        Text(
                            text = hotel.name,
                            style = AfacadTypography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.XS))
                        Text(
                            text = hotel.address,
                            style = AfacadTypography.bodySmall,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.XL))

            InteractiveRatingBar(
                currentRating = rating, onRatingChanged = { rating = it })

            AnimatedContent(
                targetState = rating, label = "", modifier = Modifier.padding(top = 16.dp)
            ) { r ->
                Text(
                    text = when (r.toInt()) {
                        1 -> stringResource(R.string.rating_very_bad)
                        2 -> stringResource(R.string.rating_bad)
                        3 -> stringResource(R.string.rating_normal)
                        4 -> stringResource(R.string.rating_good2)
                        5 -> stringResource(R.string.rating_excellent2)
                        else -> stringResource(R.string.rating_hint)
                    },
                    style = AfacadTypography.titleMedium,
                    color = if (r > 0) BlueNavy else Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.XL))

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = {
                    Text(stringResource(R.string.review_comment_label))
                },
                placeholder = {
                    Text(stringResource(R.string.review_comment_placeholder))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(AppShape.ShapeL),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = BlueNavy,
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                ),
                maxLines = 5,
                textStyle = AfacadTypography.bodyMedium
            )

            Spacer(modifier = Modifier.height(AppSpacing.XL))

            Button(
                onClick = {
                    keyboardController?.hide()
                    updateRatingViewModel.submitReview(
                        hotelId = hotel.id, rating = rating, comment = comment
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(AppShape.ShapeM),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueNavy,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                ),
                enabled = rating > 0 && updateState !is UpdateRatingState.Loading
            ) {
                if (updateState is UpdateRatingState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(Dimen.SizeM), color = Color.White
                    )
                } else {
                    Text(
                        stringResource(id = R.string.submit_review),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            TextButton(
                onClick = onDismiss, modifier = Modifier.padding(top = Dimen.PaddingSM)
            ) {
                Text(
                    stringResource(id = R.string.skip_review),
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            if (updateState is UpdateRatingState.Error) {
                Text(
                    text = (updateState as UpdateRatingState.Error).message,
                    color = Color.Red,
                    style = AfacadTypography.bodySmall,
                    modifier = Modifier.padding(top = Dimen.PaddingS)
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.L))
        }
    }
}

@Composable
fun InteractiveRatingBar(
    currentRating: Double,
    onRatingChanged: (Double) -> Unit,
    maxStars: Int = 5,
    starSize: Dp = 44.dp,
    starColor: Color = RatingYellow
) {
    Row(
        horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()
    ) {
        for (i in 1..maxStars) {
            val isSelected = i <= currentRating
            val animatedSize by animateDpAsState(
                targetValue = if (isSelected) starSize else starSize * 0.9f, label = ""
            )

            Icon(
                imageVector = if (isSelected) Icons.Filled.Star else Icons.Rounded.StarBorder,
                contentDescription = null,
                tint = if (isSelected) starColor else Color.Gray.copy(alpha = 0.3f),
                modifier = Modifier
                    .size(animatedSize)
                    .clip(CircleShape)
                    .clickable { onRatingChanged(i.toDouble()) }
                    .padding(Dimen.PaddingXXS))
        }
    }
}