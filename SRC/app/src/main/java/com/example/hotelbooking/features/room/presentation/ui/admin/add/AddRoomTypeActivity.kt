package com.example.hotelbooking.features.room.presentation.ui.admin.add

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.features.room.presentation.util.RoomValidation
import com.example.hotelbooking.features.room.presentation.viewmodel.admin.AddRoomState
import com.example.hotelbooking.features.room.presentation.viewmodel.admin.AddRoomTypeViewModel
import com.example.hotelbooking.features.upload_image.presentation.viewmodel.GalleryViewModel
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.RoyalBlue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddRoomTypeActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val hotelId = intent.getStringExtra("hotelId") ?: ""
        val roomId = intent.getStringExtra("roomId")
        setContent {
            AddRoomTypeScreen(
                hotelId = hotelId,
                roomId = roomId,
                onBackClick = { finish() },
                onSuccess = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddRoomTypeScreen(
    hotelId: String,
    roomId: String? = null,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    galleryViewModel: GalleryViewModel = hiltViewModel(),
    viewModel: AddRoomTypeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val roomState by viewModel.roomState.collectAsState()
    var currentStep by remember { mutableIntStateOf(0) }
    var imageId by remember { mutableStateOf("") }

    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(roomId) {
        if (roomId != null) {
            viewModel.loadRoomForEdit(roomId)
        }
    }

    LaunchedEffect(roomState) {
        val state = roomState
        if (state is AddRoomState.Success) {
            showSuccessDialog = true

            if (imageId.isNotEmpty()) {
                galleryViewModel.assignImage(
                    imageId = imageId, hotelId = null, roomId = state.roomId, onComplete = {
                        Log.d("DEBUG_IMAGE", "Gắn ảnh thành công cho Room: ${state.roomId}")
                    }
                )
            }
        }
    }

    val isNextEnabled = when (currentStep) {
        0 -> RoomValidation.validateOverview(
            uiState.nameVi, uiState.descriptionVi, uiState.price
        ) && RoomValidation.validateOverview(
            uiState.nameEn, uiState.descriptionEn, uiState.price
        )

        1 -> RoomValidation.validateTechnical(
            uiState.capacity, uiState.roomSize, uiState.bedTypeVi, uiState.bathroomTypeVi
        ) && RoomValidation.validateTechnical(
            uiState.capacity, uiState.roomSize, uiState.bedTypeEn, uiState.bathroomTypeEn
        )

        2 -> RoomValidation.validateInventory(uiState.roomNumbersString)

        3 -> RoomValidation.validateMedia(uiState.imageUrl)

        else -> false
    }

    if (showSuccessDialog) {
        RoomSuccessAlertDialog(onAddMore = {
            showSuccessDialog = false
            currentStep = 0
            viewModel.resetState()
            imageId = ""
        }, onFinish = {
            showSuccessDialog = false
            onSuccess()
        })
    }

    Scaffold(topBar = {
        RoomAdminTopBar(
            title = if (roomId == null) stringResource(R.string.add_room_type)
            else stringResource(R.string.edit_room_type),
            currentStep = currentStep,
            onBack = { onBackClick() })
    }, bottomBar = {
        RoomBottomNavigation(
            currentStep = currentStep,
            isLastStep = currentStep == 3,
            isLoading = roomState is AddRoomState.Loading,
            isNextEnabled = isNextEnabled,
            onNext = {
                if (currentStep < 3) currentStep++
                else {
                    viewModel.submitRoom(hotelId, roomId)
                }
            },
            onBack = { if (currentStep > 0) currentStep-- })
    }) { padding ->
        if (roomId != null && roomState is AddRoomState.Loading && currentStep == 0) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RoyalBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {

                LinearProgressIndicator(
                    progress = (currentStep + 1) / 4f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimen.PaddingXS),
                    color = RoyalBlue,
                    trackColor = RoyalBlue.copy(alpha = 0.1f)
                )

                AnimatedContent(
                    targetState = currentStep, transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally { it } + fadeIn() with slideOutHorizontally { -it } + fadeOut()
                        } else {
                            slideInHorizontally { -it } + fadeIn() with slideOutHorizontally { it } + fadeOut()
                        }.using(SizeTransform(clip = false))
                    }, label = "StepTransition"
                ) { step ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(Dimen.PaddingM)
                    ) {
                        when (step) {
                            0 -> OverviewStep(uiState, viewModel)
                            1 -> TechnicalDetailsStep(uiState, viewModel)
                            2 -> InventoryStep(uiState, viewModel)
                            3 -> MediaStep(
                                uiState,
                                viewModel,
                                onImageChange = { id -> imageId = id })
                        }
                    }
                }
            }
        }
    }
}