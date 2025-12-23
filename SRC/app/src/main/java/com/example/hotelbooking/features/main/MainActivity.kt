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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.features.auth.presentation.ui.SignInScreen
import com.example.hotelbooking.features.auth.presentation.ui.SignUpScreen
import com.example.hotelbooking.features.home.HomeScreen
import com.example.hotelbooking.features.onboarding.presentation.ui.OnboardingScreen
import com.example.hotelbooking.ui.theme.HotelBookingTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
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

    NavHost(
        navController = navController,
        startDestination = "splash_root"
    ) {
        splashGraph(navController)
        authGraph(navController)
        onboardingGraph(navController)
        userGraph(navController)
        adminGraph(navController)
    }
}

fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation(
        startDestination = "sign_in",
        route = "auth"
    ) {
        composable("sign_in") {
            SignInScreen(navController = navController)
        }

        composable("sign_up") {
            SignUpScreen(navController = navController)
        }
    }
}

fun NavGraphBuilder.onboardingGraph(navController: NavController) {
    navigation(
        startDestination = "onboarding",
        route = "onboarding_root"
    ) {
        composable("onboarding") {
            OnboardingScreen(navController)
        }
    }
}

fun NavGraphBuilder.userGraph(navController: NavController) {
    navigation(
        startDestination = "user_home",
        route = "user_root"
    ) {
        composable("user_home") {
            HomeScreen()
        }
    }
}

fun NavGraphBuilder.adminGraph(navController: NavController) {
    navigation(
        startDestination = "admin_home",
        route = "admin_root"
    ) {
//        composable("admin_home") {
//            AdminHomeScreen()
//        }
//
//        composable("manage_hotel") {
//            ManageHotelScreen()
//        }
    }
}

fun NavGraphBuilder.splashGraph(navController: NavController) {
    navigation(
        startDestination = "splash",
        route = "splash_root"
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
    }
}

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F5AA8)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.app_name),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
    }

    LaunchedEffect(Unit) {
        delay(500)
        viewModel.decideStartDestination { destination ->
            navController.navigate(destination) {
                popUpTo("splash_root") { inclusive = true }
            }
        }
    }
}