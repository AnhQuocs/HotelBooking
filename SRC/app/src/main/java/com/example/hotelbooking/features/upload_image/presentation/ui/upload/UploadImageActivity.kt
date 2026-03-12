package com.example.hotelbooking.features.upload_image.presentation.ui.upload

import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.features.upload_image.domain.model.ImageModel
import com.example.hotelbooking.features.upload_image.presentation.viewmodel.GalleryViewModel
import com.example.hotelbooking.features.upload_image.presentation.viewmodel.ImageViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.NearBlack
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UploadImageActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            UploadImageScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadImageScreen(
    imageViewModel: ImageViewModel = hiltViewModel(),
    galleryViewModel: GalleryViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val adminId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val uploadState = imageViewModel.uiState
    val galleryImages by galleryViewModel.images.collectAsStateWithLifecycle()
    val isGalleryLoading by galleryViewModel.isLoading.collectAsStateWithLifecycle()

    val progressPercentage = if (uploadState.totalToUpload > 0)
        uploadState.uploadProgress.toFloat() / uploadState.totalToUpload else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progressPercentage,
        animationSpec = tween(500),
        label = "uploadProgress"
    )

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris -> imageViewModel.onImagesSelected(uris) }

    val royalBlue = Color(0xFF4169E1)
    val nearBlack = Color(0xFF1A1A1A)
    val lightBlueBg = Color(0xFFF0F4FF)

    var showDeleteDialog by remember { mutableStateOf(false) }
    var imageToDelete by remember { mutableStateOf<ImageModel?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.media_library),
                        style = AfacadTypography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !uploadState.isLoading) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (uploadState.selectedUris.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(Dimen.PaddingM)
                        .navigationBarsPadding()
                ) {
                    Button(
                        onClick = { imageViewModel.uploadMultipleImages(adminId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimen.HeightLarge),
                        enabled = !uploadState.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = royalBlue),
                        shape = RoundedCornerShape(AppShape.ShapeM)
                    ) {
                        Text(
                            if (uploadState.isLoading)
                                stringResource(
                                    R.string.uploading_progress,
                                    uploadState.uploadProgress,
                                    uploadState.totalToUpload
                                )
                            else
                                stringResource(
                                    R.string.upload_images_button,
                                    uploadState.selectedUris.size
                                ),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        containerColor = Color.White
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(Dimen.PaddingML),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.S),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
        ) {
            item(span = { GridItemSpan(3) }) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(AppShape.ShapeM))
                            .background(if (uploadState.selectedUris.isEmpty()) lightBlueBg else Color.Transparent)
                            .drawBehind {
                                drawRoundRect(
                                    color = royalBlue.copy(alpha = 0.5f),
                                    style = Stroke(
                                        width = 4f,
                                        pathEffect = PathEffect.dashPathEffect(
                                            floatArrayOf(
                                                20f,
                                                20f
                                            ), 0f
                                        )
                                    ),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
                                )
                            }
                            .clickable(enabled = !uploadState.isLoading) { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = royalBlue,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                stringResource(R.string.select_new_images),
                                fontWeight = FontWeight.SemiBold,
                                color = nearBlack
                            )

                            Text(
                                stringResource(R.string.max_images_upload),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    if (uploadState.selectedUris.isNotEmpty()) {
                        Spacer(Modifier.height(AppSpacing.MediumLarge))
                        Text(
                            stringResource(R.string.images_ready_to_upload),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        LazyRow(
                            modifier = Modifier.padding(vertical = Dimen.PaddingS),
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.S)
                        ) {
                            items(uploadState.selectedUris) { uri ->
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(Dimen.SizeMega)
                                        .clip(
                                            RoundedCornerShape(
                                                AppShape.ShapeS
                                            )
                                        ),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    if (uploadState.isLoading) {
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Dimen.PaddingS)
                                .clip(RoundedCornerShape(AppShape.ShapeXXS)),
                            color = royalBlue
                        )
                    }

                    Divider(
                        modifier = Modifier.padding(vertical = Dimen.PaddingL),
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )
                }
            }

            item(span = { GridItemSpan(3) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.your_gallery),
                        style = AfacadTypography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = nearBlack
                        )
                    )
                    if (galleryImages.isNotEmpty()) {
                        Text(
                            stringResource(R.string.gallery_image_count, galleryImages.size),
                            fontSize = 13.sp,
                            color = royalBlue
                        )
                    }
                }
            }

            if (isGalleryLoading) {
                item(span = { GridItemSpan(3) }) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = royalBlue)
                    }
                }
            } else if (galleryImages.isEmpty()) {
                item(span = { GridItemSpan(3) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.ImageNotSupported,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(AppSpacing.S))
                        Text(
                            stringResource(R.string.profile_gallery_empty),
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                items(galleryImages) { image ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(AppShape.ShapeS))
                            .background(lightBlueBg)
                    ) {
                        AsyncImage(
                            model = image.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Box(
                            modifier = Modifier
                                .size(Dimen.SizeM)
                                .padding(top = Dimen.PaddingXS, end = Dimen.PaddingXS)
                                .align(Alignment.TopEnd)
                                .clip(CircleShape)
                                .background(color = NearBlack.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable {
                                        imageToDelete = image
                                        showDeleteDialog = true
                                    }
                            )
                        }
                    }
                }
            }

            item(span = { GridItemSpan(3) }) {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showDeleteDialog && imageToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                imageToDelete = null
            },
            icon = {
                Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color.Red)
            },
            title = {
                Text(
                    text = stringResource(id = R.string.delete_image_title),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(stringResource(id = R.string.delete_image_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        imageToDelete?.let { galleryViewModel.deleteImage(it.id) }
                        showDeleteDialog = false
                        imageToDelete = null
                    }
                ) {
                    Text(
                        stringResource(id = R.string.delete),
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        imageToDelete = null
                    }
                ) {
                    Text(stringResource(id = R.string.cancel), color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(AppShape.ShapeM),
            containerColor = Color.White
        )
    }
}