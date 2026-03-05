package com.example.hotelbooking.features.room.presentation.ui.admin.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.presentation.ui.admin.add.ImageBadge
import com.example.hotelbooking.features.room.presentation.viewmodel.admin.AddRoomUiState
import com.example.hotelbooking.features.room.presentation.viewmodel.admin.AddRoomTypeViewModel
import com.example.hotelbooking.features.upload_image.domain.model.ImageModel
import com.example.hotelbooking.features.upload_image.presentation.viewmodel.GalleryViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.OrangeVibrant
import com.example.hotelbooking.ui.theme.RoyalBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaStep(
    state: AddRoomUiState,
    viewModel: AddRoomTypeViewModel,
    onImageChange: (String) -> Unit,
    galleryViewModel: GalleryViewModel = hiltViewModel()
) {
    val images by galleryViewModel.images.collectAsState()
    val isGalleryLoading by galleryViewModel.isLoading.collectAsState()

    var showGallery by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.M)
    ) {
        Text(
            text = stringResource(R.string.room_thumbnail),
            style = AfacadTypography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimen.HeightXL4 - 10.dp)
                .clip(RoundedCornerShape(AppShape.ShapeM))
                .background(Color(0xFFF5F5F5))
                .clickable { showGallery = true },
            contentAlignment = Alignment.Center
        ) {
            if (state.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = state.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f))
                )
                Icon(
                    Icons.Default.Collections,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(Dimen.SizeXLPlus)
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.AddPhotoAlternate,
                        null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.LightGray
                    )
                    Text(
                        stringResource(R.string.select_from_gallery),
                        style = AfacadTypography.bodySmall,
                        color = Color.LightGray
                    )
                }
            }
        }

        if (showGallery) {
            ModalBottomSheet(
                onDismissRequest = { showGallery = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                GalleryPickerContent(
                    images = images,
                    isLoading = isGalleryLoading,
                    selectedImageUrl = state.imageUrl,
                    onImageSelected = { selectedImage ->
                        viewModel.updateUiState { it.copy(imageUrl = selectedImage.imageUrl) }
                        showGallery = false
                        onImageChange(selectedImage.id)
                    }
                )
            }
        }
    }
}

@Composable
fun GalleryPickerContent(
    images: List<ImageModel>,
    isLoading: Boolean,
    selectedImageUrl: String,
    onImageSelected: (ImageModel) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .padding(Dimen.PaddingM)
    ) {
        Text(
            stringResource(id = R.string.select_from_gallery),
            style = AfacadTypography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(AppSpacing.M))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.S),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
            ) {
                items(images) { image ->
                    val isSelected = image.imageUrl == selectedImageUrl

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(AppShape.ShapeS))
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) RoyalBlue else Color.Transparent,
                                shape = RoundedCornerShape(AppShape.ShapeS)
                            )
                            .clickable { onImageSelected(image) }
                    ) {
                        AsyncImage(
                            model = image.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Box(modifier = Modifier.padding(4.dp)) {
                            when {
                                image.hotelId != null -> ImageBadge(
                                    text = stringResource(id = R.string.badge_hotel),
                                    color = OrangeVibrant
                                )

                                image.roomId != null -> ImageBadge(
                                    text = stringResource(id = R.string.badge_room),
                                    color = AvailableGreen
                                )

                                image.isUsed -> ImageBadge(
                                    text = stringResource(id = R.string.badge_used),
                                    color = Color.Gray
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
                                    modifier = Modifier.size(Dimen.SizeM)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}