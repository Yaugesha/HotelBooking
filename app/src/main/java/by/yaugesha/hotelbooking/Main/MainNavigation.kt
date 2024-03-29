package by.yaugesha.hotelbooking.Main

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.NavType.Companion.StringType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import by.yaugesha.hotelbooking.Authorization.Screens.LoginScreen
import by.yaugesha.hotelbooking.DataClasses.*
import by.yaugesha.hotelbooking.Main.Screens.*

@Composable
fun MainNavigation(login: String) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.UserSearchScreen.route) {
        composable(route = Screen.UserSearchScreen.route) {
            UserSearchScreen(navController = navController)
        }
        /*{ entry ->
            entry.arguments?.getString("login")?.let { AdminMenuScreen(navController = navController*//*, login = login*//*) }
        }*/

        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController)
        }

        composable(route = Screen.FavoritesScreen.route) {
            FavoritesScreen(navController = navController)
        }

        composable(route = Screen.ProfileScreen.route) {
            ProfileScreen(navController = navController)
        }

        composable(route = Screen.UserSearchResultScreen.route + "/{searchData}",
            arguments = listOf(
                navArgument("searchData") {
                    type = SearchType()
                }
            )
        ){ entry ->
            val searchData = entry.arguments?.getParcelable<Search>("searchData")
            if (searchData != null) {
                UserSearchResultScreen(navController = navController, searchData = searchData )
            }
        }

        composable(route = Screen.BookingsScreen.route) {
            BookingsScreen(navController = navController)
        }

        composable(
            route = Screen.RoomScreen.route + "/{room}/{hotel}/{searchData}",
            arguments = listOf(
                navArgument("room") {
                    type = RoomType()
                },
                navArgument("hotel") {
                    type = HotelType()
                },
                navArgument("searchData") {
                    type = SearchType()
                }
            )
        ) { entry ->
            val room = entry.arguments?.getParcelable<Room>("room")
            val hotel = entry.arguments?.getParcelable<Hotel>("hotel")
            val searchData = entry.arguments?.getParcelable<Search>("searchData")
            RoomScreen(navController = navController, room = room!!, hotel = hotel!!, searchData = searchData!!)
        }

        composable(route = Screen.OrderScreen.route + "/{room}/{hotel}/{searchData}",
            arguments = listOf(
            navArgument("room") {
                type = RoomType()
            },
            navArgument("hotel") {
                type = HotelType()
            },
                navArgument("searchData") {
                    type = SearchType()
                }
        )
        ) { entry ->
            val room = entry.arguments?.getParcelable<Room>("room")
            val hotel = entry.arguments?.getParcelable<Hotel>("hotel")
            val searchData = entry.arguments?.getParcelable<Search>("searchData")
            OrderScreen( navController = navController, room = room!!, hotel = hotel!!, searchData = searchData!!)
        }

        composable(route = Screen.EditProfileScreen.route) {
            EditProfileScreen(navController = navController)
        }

        composable(
            route = Screen.SortScreen.route + "/{searchData}/{isBook}",
            arguments = listOf(
                navArgument("searchData") {
                    type = SearchType()
                },
                navArgument("isBook") {
                    type = NavType.BoolType
                }
            )
            ) { entry ->
            val searchData = entry.arguments?.getParcelable<Search>("searchData")
            entry.arguments?.getBoolean("isBook")
                ?.let { SortScreen(navController = navController, searchData = searchData!!, isBook = it) }
            }

        composable(route = Screen.EditBookingScreen.route + "/{room}/{hotel}/{booking}/{status}",
            arguments = listOf(
                navArgument("room") {
                    type = RoomType()
                },
                navArgument("hotel") {
                    type = HotelType()
                },
                navArgument("booking") {
                    type = BookingType()
                },
                navArgument("status") {
                    type = StringType
                }
            )
        ) { entry ->
            val room = entry.arguments?.getParcelable<Room>("room")
            val hotel = entry.arguments?.getParcelable<Hotel>("hotel")
            val booking = entry.arguments?.getParcelable<Booking>("booking")
            val status = entry.arguments?.getString("status")
            EditBookingScreen( navController = navController, room = room!!, hotel = hotel!!, booking = booking!!, status = status!!)
        }

    }
}