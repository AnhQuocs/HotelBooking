package com.example.hotelbooking.features.vouchers.presentation.ui.admin.add

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.presentation.viewmodel.admin.AdminHotelState
import com.example.hotelbooking.features.hotel.presentation.viewmodel.admin.AdminHotelViewModel
import com.example.hotelbooking.features.vouchers.domain.model.DiscountType
import com.example.hotelbooking.features.vouchers.presentation.util.DateUtils
import com.example.hotelbooking.features.vouchers.presentation.util.VoucherValidationUtil
import com.example.hotelbooking.features.vouchers.presentation.viewmodel.AdminVoucherViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.LightBlue
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateVoucherActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val adminId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            CreateVoucherScreen(
                adminId = adminId,
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateVoucherScreen(
    adminId: String,
    onBackClick: () -> Unit,
    viewModel: AdminVoucherViewModel = hiltViewModel(),
    hotelViewModel: AdminHotelViewModel = hiltViewModel()
) {
    val state by viewModel.addVoucherState.collectAsState()
    val hotelState by hotelViewModel.adminHotelState.collectAsState()

    var selectedLanguageTab by remember { mutableIntStateOf(0) }
    val tabs =
        listOf(stringResource(id = R.string.vietnamese), stringResource(id = R.string.english))

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.endDate
    )

    LaunchedEffect(adminId) { hotelViewModel.observeHotels(adminId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.create_voucher),
                        style = AfacadTypography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            null,
                            modifier = Modifier.size(Dimen.SizeSM)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = Color.White) {
                Button(
                    onClick = { viewModel.submitVouchers(adminId) { if (it) onBackClick() } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimen.PaddingM)
                        .height(50.dp),
                    enabled = with(VoucherValidationUtil) { state.canSubmit() },
                    shape = RoundedCornerShape(AppShape.ShapeM),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueNavy)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text(
                            text = stringResource(R.string.publish_now),
                            style = AfacadTypography.titleMedium
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(color = LightBlue),
            contentPadding = PaddingValues(Dimen.PaddingM),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.MediumLarge)
        ) {
            item {
                VoucherSectionCard(title = stringResource(id = R.string.basic_information)) {
                    OutlinedTextField(
                        value = state.code,
                        onValueChange = viewModel::onCodeChange,
                        label = {
                            Text(stringResource(R.string.voucher_code_label))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(AppShape.ShapeM),
                        placeholder = { Text("SUMMER66") }
                    )

                    Spacer(Modifier.height(AppSpacing.MediumLarge))

                    PrimaryTabRow(
                        selectedTabIndex = selectedLanguageTab,
                        containerColor = Color.Transparent
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedLanguageTab == index,
                                onClick = { selectedLanguageTab = index }) {
                                Text(title, modifier = Modifier.padding(vertical = Dimen.PaddingS))
                            }
                        }
                    }

                    Spacer(Modifier.height(AppSpacing.S))

                    if (selectedLanguageTab == 0) {
                        OutlinedTextField(
                            value = state.titleVi,
                            onValueChange = viewModel::onTitleViChange,
                            label = { Text(stringResource(R.string.title_vietnamese)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(AppShape.ShapeM)
                        )
                    } else {
                        OutlinedTextField(
                            value = state.titleEn,
                            onValueChange = viewModel::onTitleEnChange,
                            label = { Text(stringResource(R.string.title_english)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(AppShape.ShapeM)
                        )
                    }
                }
            }

            item {
                VoucherSectionCard(title = stringResource(R.string.voucher_discount_config)) {
                    Text(
                        stringResource(R.string.discount_type),
                        style = AfacadTypography.labelMedium,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(AppSpacing.S))

                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        val types = listOf(DiscountType.PERCENTAGE, DiscountType.FIXED_AMOUNT)
                        types.forEachIndexed { index, type ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = types.size
                                ),
                                onClick = { viewModel.onDiscountTypeChange(type) },
                                selected = state.discountType == type,
                                label = {
                                    Text(
                                        if (type == DiscountType.PERCENTAGE)
                                            stringResource(R.string.discount_percentage)
                                        else
                                            stringResource(R.string.discount_fixed_amount)
                                    )
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(AppSpacing.MediumLarge))

                    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.S)) {
                        OutlinedTextField(
                            value = state.discountValue,
                            onValueChange = viewModel::onDiscountValueChange,
                            label = {
                                Text(
                                    if (state.discountType == DiscountType.PERCENTAGE)
                                        stringResource(R.string.discount_percent_label)
                                    else
                                        stringResource(R.string.discount_amount_label)
                                )
                            },
                            suffix = { Text(if (state.discountType == DiscountType.PERCENTAGE) "%" else "$") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(AppShape.ShapeM)
                        )
                        OutlinedTextField(
                            value = state.totalQuantity,
                            onValueChange = viewModel::onTotalQuantityChange,
                            label = { Text(stringResource(R.string.voucher_quantity)) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(AppShape.ShapeM)
                        )
                    }

                    Spacer(Modifier.height(AppSpacing.S))

                    OutlinedTextField(
                        value = state.minOrderValue,
                        onValueChange = viewModel::onMinOrderValueChange,
                        label = { Text(stringResource(R.string.min_order_value)) },
                        suffix = { Text("USD") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(AppShape.ShapeM)
                    )
                }
            }

            item {
                VoucherSectionCard(
                    title = stringResource(R.string.voucher_validity_period)
                ) {
                    OutlinedTextField(
                        value = DateUtils.formatLongToDate(state.endDate),
                        onValueChange = {},
                        label = { Text(stringResource(R.string.voucher_expiry_date)) },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        shape = RoundedCornerShape(AppShape.ShapeM),
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = null)
                            }
                        },
                        interactionSource = remember { MutableInteractionSource() }
                            .also { interactionSource ->
                                LaunchedEffect(interactionSource) {
                                    interactionSource.interactions.collect {
                                        if (it is PressInteraction.Release) showDatePicker = true
                                    }
                                }
                            }
                    )
                }
            }

            item {
                VoucherSectionCard(
                    title = stringResource(R.string.voucher_apply_hotels)
                ) {
                    when (val hState = hotelState) {
                        is AdminHotelState.Loading -> CircularProgressIndicator()
                        is AdminHotelState.Success -> {
                            val hotels = hState.data

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = state.selectedHotelIds.size == hotels.size,
                                    onCheckedChange = {
                                        hotels.forEach { h ->
                                            if (state.selectedHotelIds.size != hotels.size) {
                                                if (!state.selectedHotelIds.contains(h.id)) viewModel.toggleHotelSelection(
                                                    h.id
                                                )
                                            } else {
                                                viewModel.toggleHotelSelection(h.id)
                                            }
                                        }
                                    }
                                )
                                Text(
                                    stringResource(R.string.select_all_hotels),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            hotels.forEach { hotel ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.toggleHotelSelection(hotel.id) }
                                ) {
                                    Checkbox(
                                        checked = state.selectedHotelIds.contains(hotel.id),
                                        onCheckedChange = { viewModel.toggleHotelSelection(hotel.id) }
                                    )
                                    Text(hotel.name)
                                }
                            }
                        }

                        else -> Text(stringResource(id = R.string.msg_no_hotels_found))
                    }
                }
            }

            item { Spacer(Modifier.height(AppSpacing.XL)) }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.onEndDateChange(it) }
                    showDatePicker = false
                }) { Text(stringResource(id = R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDatePicker = false
                }) { Text(stringResource(id = R.string.cancel)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun VoucherSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(AppShape.ShapeL),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(Dimen.PaddingM)) {
            Text(title, style = AfacadTypography.titleMedium, color = Color.Gray)
            Spacer(Modifier.height(AppSpacing.MediumLarge))
            content()
        }
    }
}