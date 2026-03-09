package com.example.hotelbooking.features.hotel.presentation.ui.admin

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.R
import com.example.hotelbooking.features.home.viewmodel.SearchViewModel
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.presentation.ui.admin.add.AddHotelActivity
import com.example.hotelbooking.features.hotel.presentation.ui.admin.detail.AdminHotelDetailActivity
import com.example.hotelbooking.features.hotel.presentation.viewmodel.admin.AdminHotelState
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.RoyalBlue
import com.example.hotelbooking.ui.theme.SurfaceGray
import com.example.hotelbooking.ui.theme.TextTertiary

@Composable
fun MyHotelsScreen(
    state: AdminHotelState<List<Hotel>>,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val query by searchViewModel.searchQuery.collectAsState()
    val searchState by searchViewModel.searchResultState.collectAsState()

    LaunchedEffect(Unit) {
        searchViewModel.initSearchMode(isAdmin = true)
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = stringResource(id = R.string.my_hotels),
                    style = AfacadTypography.titleMedium.copy(
                        fontSize = 20.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, AddHotelActivity::class.java)
                    context.startActivity(intent)
                },
                containerColor = RoyalBlue,
                contentColor = Color.White
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null
                )
            }
        },
        containerColor = Color.White,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxSize()
                .padding(horizontal = Dimen.PaddingM)
                .padding(top = Dimen.PaddingM)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { searchViewModel.onSearchQueryChange(it) },
                label = {
                    Text(
                        stringResource(id = R.string.search),
                        style = AfacadTypography.labelLarge,
                        color = Color.Black
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(TextTertiary),
                        modifier = Modifier.size(Dimen.SizeSM)
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { searchViewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                textStyle = TextStyle(color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = SurfaceGray,
                    focusedBorderColor = PrimaryBlue
                ),
                shape = RoundedCornerShape(AppShape.ShapeL),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            if(query.isBlank()) {
                MyHotelsSection(
                    state,
                    onEditClick = { hotelId ->
                        val intent = Intent(context, AddHotelActivity::class.java)
                            .putExtra("hotelId", hotelId)
                        context.startActivity(intent)
                    },
                    onHotelClick = { hotelId ->
                        val intent = Intent(context, AdminHotelDetailActivity::class.java)
                            .putExtra("hotelId", hotelId)
                        context.startActivity(intent)
                    }
                )
            } else {
                SearchManagedHotelsSection(
                    isNoHotelSearch = query.isNotEmpty(),
                    searchState = searchState,
                    query = query,
                    onDetailClick = { hotelId ->
                        val intent = Intent(context, AdminHotelDetailActivity::class.java)
                            .putExtra("hotelId", hotelId)
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}