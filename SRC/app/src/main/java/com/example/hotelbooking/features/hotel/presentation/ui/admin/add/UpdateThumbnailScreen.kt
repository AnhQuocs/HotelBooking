package com.example.hotelbooking.features.hotel.presentation.ui.admin.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.presentation.viewmodel.admin.AddHotelViewModel
import com.example.hotelbooking.features.upload_image.presentation.viewmodel.GalleryViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.NearBlack
import com.example.hotelbooking.ui.theme.OrangeVibrant
import com.example.hotelbooking.ui.theme.RoyalBlue

@Composable
fun UpdateThumbnailScreen(
    addHotelViewModel: AddHotelViewModel,
    galleryViewModel: GalleryViewModel = hiltViewModel(),
    onImageChange: (String) -> Unit
) {
    val hotelUiState by addHotelViewModel.uiState.collectAsState()
    val images by galleryViewModel.images.collectAsState()
    val isGalleryLoading by galleryViewModel.isLoading.collectAsState()

    val currentSelectedUrl = hotelUiState.thumbnailUrl

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimen.PaddingM)
    ) {
        Text(
            text = stringResource(R.string.current_cover_image),
            style = AfacadTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = NearBlack
        )
        Spacer(modifier = Modifier.height(AppSpacing.L))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(AppShape.ShapeL))
                .background(Color.LightGray.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            if (currentSelectedUrl.isEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Image,
                        null,
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(stringResource(R.string.no_cover_selected), color = Color.Gray)
                }
            } else {
                AsyncImage(
                    model = currentSelectedUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { addHotelViewModel.updateThumbnail("") },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Dimen.PaddingS)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .size(Dimen.SizeL)
                ) {
                    Icon(
                        Icons.Default.Close,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.L))
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(AppSpacing.L))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.select_from_your_gallery),
                style = AfacadTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = NearBlack
            )
            if (isGalleryLoading) {
                CircularProgressIndicator(strokeWidth = 2.dp, color = RoyalBlue)
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.M))

        if (!isGalleryLoading && images.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(id = R.string.gallery_empty), color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.S),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.S),
                modifier = Modifier.fillMaxSize()
            ) {
                items(images) { image ->
                    val isSelected = image.imageUrl == currentSelectedUrl

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(AppShape.ShapeM))
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) RoyalBlue else Color.Transparent,
                                shape = RoundedCornerShape(AppShape.ShapeM)
                            )
                            .clickable {
                                addHotelViewModel.updateThumbnail(image.imageUrl)
                                onImageChange(image.id)
                            }
                    ) {
                        AsyncImage(
                            model = image.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Box(modifier = Modifier.padding(Dimen.PaddingXS)) {
                            when {
                                image.hotelId != null -> ImageBadge(
                                    stringResource(R.string.badge_hotel),
                                    OrangeVibrant
                                )

                                image.roomId != null -> ImageBadge(
                                    stringResource(R.string.badge_room),
                                    AvailableGreen
                                )

                                image.isUsed -> ImageBadge(
                                    stringResource(R.string.badge_used),
                                    Color.Gray
                                )
                            }
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(RoyalBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    null,
                                    tint = RoyalBlue,
                                    modifier = Modifier.size(Dimen.SizeML)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImageBadge(text: String, color: Color) {
    Surface(
        color = color,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.wrapContentSize()
    ) {
        Text(
            text = text,
            color = Color.White,
            style = AfacadTypography.labelSmall.copy(
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = Dimen.PaddingXS, vertical = Dimen.PaddingXXS)
        )
    }
}