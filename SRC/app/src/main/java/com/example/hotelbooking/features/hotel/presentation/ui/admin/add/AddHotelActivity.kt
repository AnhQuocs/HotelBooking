package com.example.hotelbooking.features.hotel.presentation.ui.admin.add

import android.R.id.message
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.presentation.util.AddHotelValidation
import com.example.hotelbooking.features.hotel.presentation.viewmodel.admin.AddHotelState
import com.example.hotelbooking.features.hotel.presentation.viewmodel.admin.AddHotelViewModel
import com.example.hotelbooking.features.room.presentation.ui.admin.AddRoomTypeActivity
import com.example.hotelbooking.features.upload_image.presentation.viewmodel.GalleryViewModel
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.RoyalBlue
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddHotelActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val hotelId = intent.getStringExtra("hotelId")

        setContent {
            AddHotelScreen(hotelId = hotelId, onBackClick = { finish() })
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddHotelScreen(
    hotelId: String? = null,
    addHotelViewModel: AddHotelViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    galleryViewModel: GalleryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val validate = AddHotelValidation

    var currentStep by rememberSaveable { mutableIntStateOf(0) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showSaveDraftDialog by remember { mutableStateOf(false) }
    var imageId by remember { mutableStateOf("") }

    LaunchedEffect(hotelId) {
        if (hotelId != null) {
            addHotelViewModel.loadHotelForEdit(hotelId)
        }
    }

    val uiState by addHotelViewModel.uiState.collectAsState()
    val addHotelState by addHotelViewModel.addHotelState.collectAsState()
    val customAmenities by addHotelViewModel.customAmenities.collectAsState()

    val hasUnsavedChanges = addHotelViewModel.hasUnsavedChanges()

    LaunchedEffect(Unit) {
        val adminId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (adminId.isNotEmpty()) {
            addHotelViewModel.loadCustomAmenities(adminId)
        }
    }

    val hotelIdState = hotelId
    val successText = stringResource(id = R.string.success)
//    LaunchedEffect(addHotelState) {
//        when (val state = addHotelState) {
//            is AddHotelState.Success -> {
//                if (imageId.isNotEmpty()) {
//                    galleryViewModel.assignImage(
//                        imageId = imageId,
//                        hotelId = state.hotelId,
//                        roomId = null,
//                        onComplete = {}
//                    )
//                }
//
//                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//
//                if (hotelIdState == null) {
//                    val intent = Intent(context, AddRoomTypeActivity::class.java).apply {
//                        putExtra("hotelId", state.hotelId)
//                    }
//                    context.startActivity(intent)
//                    (context as? Activity)?.finish()
//                } else {
//                    onBackClick()
//                }
//            }
//            is AddHotelState.Error -> {
//                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
//            }
//            else -> {}
//        }
//    }

    LaunchedEffect(addHotelState) {
        when (addHotelState) {
            is AddHotelState.Success -> {
                Toast.makeText(context, successText, Toast.LENGTH_SHORT).show()
                val intent = Intent(context, AddRoomTypeActivity::class.java)
                    .putExtra("hotelId", hotelId)
                context.startActivity(intent)
            }

            is AddHotelState.Error -> {
                Toast.makeText(
                    context,
                    (addHotelState as AddHotelState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }
    }

    BackHandler(enabled = hasUnsavedChanges) {
        showExitDialog = true
    }

    if (showExitDialog) {
        ExitDialog(
            onConfirm = {
                val adminId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                addHotelViewModel.submitHotel(adminId = adminId, hotelId = hotelId, isDraft = true)
                showExitDialog = false
            },
            onDismiss = {
                showExitDialog = false
                onBackClick()
            }
        )
    }

    if (showSaveDraftDialog) {
        SaveDraftDialog(
            onConfirm = {
                val adminId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                addHotelViewModel.submitHotel(adminId = adminId, hotelId = hotelId, isDraft = true)
                showSaveDraftDialog = false
            },
            onDismiss = { showSaveDraftDialog = false }
        )
    }

    val isButtonEnabled = when (currentStep) {
        0 -> {
            validate.validateBasicInfo(uiState.nameVi, uiState.descriptionVi) &&
                    validate.validateBasicInfo(uiState.nameEn, uiState.descriptionEn)
        }

        1 -> uiState.isLocationConfirmed

        2 -> uiState.checkInTime.isNotBlank() && uiState.checkOutTime.isNotBlank() && uiState.amenities.isNotEmpty()

        3 -> uiState.thumbnailUrl.isNotBlank()

        else -> false
    }

    Scaffold(
        topBar = {
            AddHotelTopBar(
                isEdit = hotelId != null,
                hasUnsavedChanges = hasUnsavedChanges,
                onBackClick = {
                    if (hasUnsavedChanges) showExitDialog = true else onBackClick()
                },
                onSaveDraftClick = { showSaveDraftDialog = true }
            )
        },
        bottomBar = {
            AddHotelBottomBar(
                currentStep = currentStep,
                isNextEnabled = isButtonEnabled,
                isLoading = addHotelState is AddHotelState.Loading,
                onBackStep = { currentStep-- },
                onNextStep = {
                    if (currentStep < 3) {
                        currentStep++
                    } else {
                        val adminId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        addHotelViewModel.submitHotel(
                            adminId = adminId,
                            hotelId = hotelId,
                            isDraft = false
                        )
                    }
                }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(Dimen.PaddingM)
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() with slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() with slideOutHorizontally { it } + fadeOut()
                    }.using(SizeTransform(clip = false))
                },
                label = "AddHotelSteps"
            ) { step ->
                when (step) {
                    0 -> BasicInfoScreen(
                        uiState = uiState,
                        onValueChange = { nameVi, nameEn, desVi, desEn ->
                            addHotelViewModel.updateBasicInfo(nameVi, nameEn, desVi, desEn)
                        }
                    )

                    1 -> UpdateLocationScreen(
                        isLoading = uiState.isLocationLoading,
                        isConfirmed = uiState.isLocationConfirmed,
                        initialAddressVi = uiState.addressVi,
                        initialAddressEn = uiState.addressEn,
                        onLocationConfirmed = { lat, lng, addressVi, addressEn, shortAddressVi, shortAddressEn, cityVi, cityEn ->
                            addHotelViewModel.updateLocation(
                                addressVi = addressVi, addressEn = addressEn,
                                shortAddressVi = shortAddressVi, shortAddressEn = shortAddressEn,
                                cityVi = cityVi, cityEn = cityEn,
                                lat = lat, lng = lng
                            )
                        }
                    )

                    2 -> UpdateDetailsScreen(
                        uiState = uiState,
                        customAmenities = customAmenities,
                        onValueChange = { amenities, checkIn, checkOut ->
                            addHotelViewModel.updateDetails(amenities, checkIn, checkOut)
                        },
                        onAddCustomAmenity = { newAmenity ->
                            val adminId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                            addHotelViewModel.saveCustomAmenity(adminId, newAmenity)
                        }
                    )

                    3 -> UpdateThumbnailScreen(
                        addHotelViewModel = addHotelViewModel,
                        onImageChange = { id -> imageId = id }
                    )
                }
            }
        }
    }

    if (addHotelState is AddHotelState.Loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {},
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = RoyalBlue)
        }
    }
}