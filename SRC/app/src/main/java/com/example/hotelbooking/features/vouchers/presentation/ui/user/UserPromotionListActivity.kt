package com.example.hotelbooking.features.vouchers.presentation.ui.user

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.presentation.ui.user.details.HotelDetailActivity
import com.example.hotelbooking.features.hotel.presentation.viewmodel.user.HotelState
import com.example.hotelbooking.features.hotel.presentation.viewmodel.user.HotelViewModel
import com.example.hotelbooking.features.vouchers.domain.model.DiscountType
import com.example.hotelbooking.features.vouchers.domain.model.Voucher
import com.example.hotelbooking.features.vouchers.presentation.util.DateUtils
import com.example.hotelbooking.features.vouchers.presentation.viewmodel.UserVoucherState
import com.example.hotelbooking.features.vouchers.presentation.viewmodel.UserVoucherViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.LightBlueBackground
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.SurfaceSoftBlue
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserPromotionListActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            UserPromotionListScreen(
                userId = userId,
                onBackClick = { finish() },
                onNavigateToHotel = { hotelId, code ->
                    val intent = Intent(context, HotelDetailActivity::class.java)
                        .putExtra("hotelId", hotelId)
                        .putExtra("code", code)
                    context.startActivity(intent)
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UserPromotionListScreen(
    userId: String,
    onBackClick: () -> Unit,
    onNavigateToHotel: (String, String) -> Unit,
    voucherViewModel: UserVoucherViewModel = hiltViewModel(),
    hotelViewModel: HotelViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }

    val voucherState by voucherViewModel.uiState.collectAsState()
    val hotelState by hotelViewModel.hotelsState.collectAsState()
    val groupedData by voucherViewModel.groupedVouchers.collectAsState()

    LaunchedEffect(userId) {
        voucherViewModel.loadUserVouchers(userId)
        hotelViewModel.loadHotels()
    }

    LaunchedEffect(voucherState, hotelState) {
        if (voucherState is UserVoucherState.Success && hotelState is HotelState.Success) {
            val vouchers = (voucherState as UserVoucherState.Success).vouchers
            val hotels = (hotelState as HotelState.Success).data
            voucherViewModel.groupVouchersByHotel(vouchers, hotels)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.my_promotions), style = AfacadTypography.titleLarge) },
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
        containerColor = SurfaceSoftBlue
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {

            if (voucherState is UserVoucherState.Loading || hotelState is HotelState.Loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
                return@Column
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = Dimen.PaddingM, vertical = Dimen.PaddingS)
            ) {
                val filteredMap = groupedData.filterKeys {
                    it.name.contains(searchQuery, ignoreCase = true)
                }

                if (filteredMap.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(id = R.string.no_promotion_found),
                            modifier = Modifier
                                .padding(Dimen.PaddingM)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }

                filteredMap.forEach { (hotel, vouchers) ->
                    stickyHeader {
                        Surface(
                            color = SurfaceSoftBlue,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = Dimen.PaddingS)
                        ) {
                            Text(
                                text = hotel.name,
                                style = AfacadTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(vertical = Dimen.PaddingS),
                                color = BlueNavy
                            )
                        }
                    }

                    items(vouchers) { voucher ->
                        UserVoucherCard(
                            voucher = voucher,
                            onClickUse = { onNavigateToHotel(hotel.id, voucher.code) }
                        )
                        Spacer(Modifier.height(AppSpacing.M))
                    }
                }
            }
        }
    }
}

@Composable
fun UserVoucherCard(
    voucher: Voucher,
    onClickUse: () -> Unit
) {
    val isExpired = voucher.endDate < System.currentTimeMillis()
    val isAvailable = !voucher.isUsed && !isExpired
    val alpha = if (isAvailable) 1f else 0.5f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(AppShape.ShapeM)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(color = LightBlueBackground, shape = RoundedCornerShape(AppShape.ShapeS)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (voucher.discountType == DiscountType.PERCENTAGE) {
                        "${voucher.discountValue.toInt()}%"
                    } else {
                        "$${voucher.discountValue.toInt()}"
                    },
                    style = AfacadTypography.bodyLarge.copy(
                        color = BlueNavy,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.width(AppSpacing.MediumLarge))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = voucher.title,
                    style = AfacadTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(AppSpacing.XS))
                Text(
                    text = stringResource(
                        R.string.min_order,
                        voucher.minOrderValue.toInt()
                    ),
                    style = AfacadTypography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = stringResource(
                        R.string.expiry_date,
                        DateUtils.formatLongToDate(voucher.endDate)
                    ),
                    style = AfacadTypography.bodySmall
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                if (voucher.isUsed) {
                    Text(stringResource(R.string.used_status), color = Color.Gray, style = AfacadTypography.labelMedium)
                } else if (isExpired) {
                    Text(stringResource(R.string.expired_status), color = Color.Red, style = AfacadTypography.labelMedium)
                } else {
                    Button(
                        onClick = onClickUse,
                        contentPadding = PaddingValues(horizontal = Dimen.PaddingSM, vertical = Dimen.PaddingXSPlus),
                        colors = ButtonDefaults.buttonColors(containerColor = BlueNavy),
                        shape = RoundedCornerShape(AppShape.ShapeS),
                        modifier = Modifier.height(AppSpacing.XLPlus)
                    ) {
                        Text(stringResource(R.string.use_now), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}