package com.example.hotelbooking.features.room.presentation.ui.detail

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hotelbooking.BaseComponentActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoomDetailActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val roomId = intent.getStringExtra("roomId") ?: ""

        setContent {
            val navController = rememberNavController()
            val start = "roomDetail"

            NavHost(
                navController = navController,
                startDestination = start
            ) {
                composable(
                    route = "roomDetail",
                    exitTransition = {
                        fadeOut(animationSpec = tween(300))
                    },
                    popEnterTransition = {
                        fadeIn(animationSpec = tween(300))
                    }
                ) {
                    RoomDetailSection(
                        roomId = roomId,
                        onBackClick = { finish() },
                        navController = navController
                    )
                }

                composable(
                    route = "booking_screen/{roomId}?start={start}&end={end}&hotelId={hotelId}&stock={stock}&roomName={roomName}&price={price}&capacity={capacity}",
                    arguments = listOf(
                        navArgument("roomId") { type = NavType.StringType },
                        navArgument("start") { type = NavType.StringType },
                        navArgument("end") { type = NavType.StringType },
                        navArgument("hotelId") { type = NavType.StringType },
                        navArgument("stock") { type = NavType.IntType },
                        navArgument("roomName") { type = NavType.StringType },
                        navArgument("price") { type = NavType.StringType },
                        navArgument("capacity") { type = NavType.IntType }
                    ),
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(400)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(400)
                        )
                    }
                ) { backStackEntry ->
                    val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
                    val startStr = backStackEntry.arguments?.getString("start") ?: ""
                    val endStr = backStackEntry.arguments?.getString("end") ?: ""
                    val hotelId = backStackEntry.arguments?.getString("hotelId") ?: ""
                    val stock = backStackEntry.arguments?.getInt("stock") ?: 0
                    val roomName = Uri.decode(backStackEntry.arguments?.getString("roomName") ?: "")
                    val price = backStackEntry.arguments?.getString("price") ?: ""
                    val capacity = backStackEntry.arguments?.getInt("capacity") ?: 0

//                    BookingScreen(
//                        navController = navController,
//                        roomId = roomId,
//                        startDate = LocalDate.parse(startStr),
//                        endDate = LocalDate.parse(endStr),
//                        hotelId = hotelId,
//                        availableStock = stock,
//                        roomName = roomName,
//                        price = price,
//                        capacity = capacity
//                    )
                }

                composable(
                    route = "checkout?date={date}&hotelId={hotelId}&bookingId={bookingId}&roomName={roomName}&guestName={guestName}&numberOfGuest={numberOfGuest}&phone={phone}&totalPrice={totalPrice}",
                    arguments = listOf(
                        navArgument("date") { type = NavType.StringType },
                        navArgument("hotelId") { type = NavType.StringType },
                        navArgument("bookingId") { type = NavType.StringType },
                        navArgument("roomName") { type = NavType.StringType },
                        navArgument("guestName") { type = NavType.StringType },
                        navArgument("numberOfGuest") { type = NavType.IntType },
                        navArgument("phone") { type = NavType.StringType },
                        navArgument("totalPrice") { type = NavType.StringType },
                    )
                ) { backStackEntry ->
                    val date = Uri.decode(backStackEntry.arguments?.getString("date") ?: "")
                    val hotelId = backStackEntry.arguments?.getString("hotelId") ?: ""
                    val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
                    val roomName = Uri.decode(backStackEntry.arguments?.getString("roomName") ?: "")
                    val guestName = Uri.decode(backStackEntry.arguments?.getString("guestName") ?: "")
                    val numberOfGuest = backStackEntry.arguments?.getInt("numberOfGuest") ?: 1
                    val phone = backStackEntry.arguments?.getString("phone") ?: ""
                    val totalPrice = backStackEntry.arguments?.getString("totalPrice") ?: ""

//                    CheckoutScreen(
//                        date = date,
//                        hotelId = hotelId,
//                        bookingId = bookingId,
//                        roomName = roomName,
//                        guestName = guestName,
//                        numberOfGuest = numberOfGuest,
//                        phone = phone,
//                        totalPrice = totalPrice,
//                        navController = navController
//                    )
                }

                composable("payment_complete") {
//                    PaymentCompleteScreen(
//                        onBackClick = {
//                            finish()
//                        },
//                        onHomeClick = {
//                            val intent = Intent(this@RoomDetailActivity, MainActivity::class.java)
//                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//                            startActivity(intent)
//                            finish()
//                        }
//                    )
                }
            }
        }
    }
}