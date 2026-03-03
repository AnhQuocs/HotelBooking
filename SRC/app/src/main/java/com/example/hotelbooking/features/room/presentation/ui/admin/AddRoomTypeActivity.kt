package com.example.hotelbooking.features.room.presentation.ui.admin

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.features.room.presentation.viewmodel.admin.AddRoomState
import com.example.hotelbooking.features.room.presentation.viewmodel.admin.AdminRoomViewModel
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
                onBack = {},
                onSuccess = {}
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddRoomTypeScreen(
    hotelId: String,
    roomId: String? = null,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: AdminRoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val roomState by viewModel.roomState.collectAsState()
    var currentStep by remember { mutableIntStateOf(0) }

    LaunchedEffect(roomState) {
        if (roomState is AddRoomState.Success) {
            onSuccess()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            RoomAdminTopBar(
                title = if (roomId == null) "Thêm loại phòng" else "Sửa loại phòng",
                currentStep = currentStep,
                onBack = { if (currentStep > 0) currentStep-- else onBack() }
            )
        },
        bottomBar = {
            RoomBottomNavigation(
                currentStep = currentStep,
                isLastStep = currentStep == 3,
                isLoading = roomState is AddRoomState.Loading,
                onNext = {
                    if (currentStep < 3) currentStep++
                    else viewModel.submitRoom(hotelId, roomId)
                },
                onBack = { if (currentStep > 0) currentStep-- }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            LinearProgressIndicator(
                progress = (currentStep + 1) / 4f,
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = RoyalBlue,
                trackColor = RoyalBlue.copy(alpha = 0.1f)
            )

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() with slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() with slideOutHorizontally { it } + fadeOut()
                    }.using(SizeTransform(clip = false))
                },
                label = "StepTransition"
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
                        3 -> MediaStep(uiState, viewModel)
                    }
                }
            }
        }
    }
}