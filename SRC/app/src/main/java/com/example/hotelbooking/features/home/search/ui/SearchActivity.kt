package com.example.hotelbooking.features.home.search.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.features.home.search.viewmodel.SearchViewModel
import com.example.hotelbooking.features.hotel.presentation.ui.details.HotelDetailActivity
import com.example.hotelbooking.features.hotel.presentation.ui.recommended.RecommendedItem
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelState
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.SurfaceGray
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SearchHotelScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
fun SearchHotelScreen(
    onBackClick: () -> Unit,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val query by searchViewModel.searchQuery.collectAsState()
    val searchState by searchViewModel.searchResultState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                text = stringResource(id = R.string.search_title),
                onBackClick = onBackClick
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(paddingValues)
                .padding(Dimen.PaddingM)
                .padding(top = Dimen.PaddingM)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { searchViewModel.onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(id = R.string.prompt_where_to_go)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = null,
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
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = SurfaceGray,
                    focusedBorderColor = SurfaceGray
                ),
                shape = RoundedCornerShape(AppShape.ShapeXL2)
            )

            Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when (val state = searchState) {
                    is HotelState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    is HotelState.Error -> {
                        Text(
                            state.message,
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    is HotelState.Success -> {
                        if (state.data.isEmpty() && query.isNotEmpty()) {
                            Text(
                                stringResource(id = R.string.msg_no_hotels_found),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyColumn {
                                items(
                                    items = state.data,
                                    key = { it.id }
                                ) { hotel ->
                                    RecommendedItem(
                                        hotel = hotel,
                                        query = query,
                                        onClick = { hotelId ->
                                            val intent =
                                                Intent(context, HotelDetailActivity::class.java)
                                                    .putExtra("hotelId", hotelId)
                                            context.startActivity(intent)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}