package com.example.hotelbooking.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R

enum class TabItem(@DrawableRes val iconRes: Int, @StringRes val label: Int) {
    Home(R.drawable.ic_home, R.string.home),
    Booking(R.drawable.ic_booking, R.string.booking),
    Message(R.drawable.ic_chat, R.string.message),
    Profile(R.drawable.ic_profile, R.string.profile)
}

@Composable
fun UserBottomAppBar(
    currentIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = TabItem.entries.toTypedArray()
    val jostFont = FontFamily(Font(R.font.jost_font))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
    ) {
        Surface(
            shadowElevation = 8.dp,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NavigationBar(
                containerColor = Color.White,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(80.dp)
            ) {
                tabs.forEachIndexed { index, tab ->
                    val selected = currentIndex == index

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onTabSelected(index) },
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = tab.iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                colorFilter = if (selected) ColorFilter.tint(Color(0xFF2853AF)) else ColorFilter.tint(Color(0xFFA0AAB8))
                            )
                            Text(
                                text = stringResource(id = tab.label),
                                fontFamily = jostFont,
                                fontSize = 12.sp,
                                color = if (selected) Color(0xFF2853AF) else Color(0xFFA0AAB8)
                            )
                        }
                    }
                }
            }
        }
    }
}