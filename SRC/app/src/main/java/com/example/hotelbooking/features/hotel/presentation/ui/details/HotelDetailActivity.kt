package com.example.hotelbooking.features.hotel.presentation.ui.details

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.features.chat.presentation.ui.ChatActivity
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

            HotelDetailSection(
                hotelId = hotelId,
                onBackClick = { finish() },
                onRoomClick = { roomId ->
                    val intent = Intent(context, RoomDetailActivity::class.java)
                        .putExtra("roomId", roomId)
                    context.startActivity(intent)
                },
                onChatClick = { hotelId, hotelName, shortAddress ->
                    val intent = Intent(context, ChatActivity::class.java)
                        .putExtra("hotelId", hotelId)
                        .putExtra("hotelName", hotelName)
                        .putExtra("shortAddress", shortAddress)
                        .putExtra("userId", userId)
                    context.startActivity(intent)
                }
            )
        }
    }
}