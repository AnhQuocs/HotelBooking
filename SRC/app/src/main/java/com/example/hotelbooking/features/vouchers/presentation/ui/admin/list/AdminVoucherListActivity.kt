package com.example.hotelbooking.features.vouchers.presentation.ui.admin.list

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.presentation.viewmodel.admin.AdminHotelState
import com.example.hotelbooking.features.hotel.presentation.viewmodel.admin.AdminHotelViewModel
import com.example.hotelbooking.features.vouchers.presentation.ui.admin.add.CreateVoucherActivity
import com.example.hotelbooking.features.vouchers.presentation.viewmodel.AdminVoucherState
import com.example.hotelbooking.features.vouchers.presentation.viewmodel.AdminVoucherViewModel
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.LightBlue
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminVoucherListActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val adminId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            AdminVoucherListScreen(
                adminId = adminId,
                onNavigateToCreate = {
                    context.startActivity(
                        Intent(context, CreateVoucherActivity::class.java)
                    )
                },
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVoucherListScreen(
    adminId: String,
    onNavigateToCreate: () -> Unit,
    onBackClick: () -> Unit,
    voucherViewModel: AdminVoucherViewModel = hiltViewModel(),
    hotelViewModel: AdminHotelViewModel = hiltViewModel()
) {
    val voucherState by voucherViewModel.uiState.collectAsState()
    val hotelState by hotelViewModel.adminHotelState.collectAsState()

    var selectedHotelId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(adminId) {
        voucherViewModel.loadVouchers(adminId)
        hotelViewModel.observeHotels(adminId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.voucher_management),
                        style = AfacadTypography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = null,
                            modifier = Modifier.size(Dimen.SizeSM)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            if (voucherState is AdminVoucherState.Success && (voucherState as AdminVoucherState.Success).vouchers.isNotEmpty()) {
                FloatingActionButton(
                    onClick = onNavigateToCreate,
                    containerColor = BlueNavy,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        },
        containerColor = LightBlue
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {

            if (hotelState is AdminHotelState.Success) {
                val hotels = (hotelState as AdminHotelState.Success).data
                HotelFilterSection(
                    hotels = hotels,
                    selectedHotelId = selectedHotelId,
                    onHotelSelected = { selectedHotelId = it }
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = voucherState) {
                    is AdminVoucherState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFF0A3A7A)
                        )
                    }

                    is AdminVoucherState.Success -> {
                        val allVouchers = state.vouchers
                        val filteredVouchers = if (selectedHotelId == null) {
                            allVouchers
                        } else {
                            allVouchers.filter { it.hotelId == selectedHotelId }
                        }

                        if (allVouchers.isEmpty()) {
                            EmptyVoucherContent(onAddClick = onNavigateToCreate)
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(Dimen.PaddingM),
                                verticalArrangement = Arrangement.spacedBy(AppSpacing.M),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredVouchers) { voucher ->
                                    AdminVoucherCard(
                                        voucher = voucher,
                                        onToggle = { isActive ->
                                            voucherViewModel.toggleVoucher(voucher.id, isActive)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    is AdminVoucherState.Error -> {
                        Text(
                            text = state.message,
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}