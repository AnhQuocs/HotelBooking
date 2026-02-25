package com.example.hotelbooking.features.profile.feature.promotions.presentation.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.hotelbooking.BaseComponentActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PromotionActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent { 
            NoPromotionsScreen()
        }
    }
}