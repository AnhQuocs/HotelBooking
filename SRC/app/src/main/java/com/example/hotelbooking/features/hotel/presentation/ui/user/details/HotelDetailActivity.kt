package com.example.hotelbooking.features.hotel.presentation.ui.user.details

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.features.chat.presentation.ui.user.ChatActivity
import com.example.hotelbooking.features.map.ui.hotel.HotelLocationActivity
import com.example.hotelbooking.features.room.presentation.ui.detail.RoomDetailActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HotelDetailActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val hotelId = intent.getStringExtra("hotelId") ?: ""

        setContent {
            val context = LocalContext.current
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            HotelDetailContainer(
                hotelId = hotelId,
                onBackClick = { finish() },
                onOpenMap = { lat, lng ->
                    val intent = Intent(context, HotelLocationActivity::class.java)
                        .putExtra("lat", lat)
                        .putExtra("lng", lng)
                        .putExtra("hotelId", hotelId)
                    context.startActivity(intent)
                },
                onRoomClick = { roomId ->
                    val intent = Intent(context, RoomDetailActivity::class.java)
                        .putExtra("roomId", roomId)
                    context.startActivity(intent)
                },
                onChatClick = { hotelId, adminId, hotelName, shortAddress ->
                    val intent = Intent(context, ChatActivity::class.java)
                        .putExtra("hotelId", hotelId)
                        .putExtra("adminId", adminId)
                        .putExtra("hotelName", hotelName)
                        .putExtra("shortAddress", shortAddress)
                        .putExtra("userId", userId)
                    context.startActivity(intent)
                }
            )
        }
    }
}