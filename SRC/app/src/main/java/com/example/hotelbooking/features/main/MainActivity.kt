package com.example.hotelbooking.features.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.features.home.HomeScreen
import com.example.hotelbooking.features.onboarding.ui.OnboardingScreen
import com.example.hotelbooking.ui.theme.HotelBookingTheme

class MainActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HotelBookingTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val start = "splash"

    NavHost(
        navController = navController,
        startDestination = start
    ) {
        composable("splash") {
            SplashScreen(navController = navController)
        }

        composable("onboarding") {
            OnboardingScreen(navController)
        }

        composable("main") {
            HomeScreen()
        }
    }
}

@Composable
fun SplashScreen(
    navController: NavController,
) {
    val splashViewModel = SplashViewModel()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F5AA8)), contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(R.string.app_name),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
    }

    LaunchedEffect(Unit) {
        splashViewModel.checkUserRole { destination ->
            navController.navigate(destination) {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
}