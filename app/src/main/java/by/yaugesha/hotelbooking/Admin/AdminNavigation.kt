package by.yaugesha.hotelbooking.Admin

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import by.yaugesha.hotelbooking.Admin.Hotel.Add.AddHotelScreen
import by.yaugesha.hotelbooking.Admin.Hotel.Add.AddRoom
import by.yaugesha.hotelbooking.Admin.Hotel.AdminSearchHotelScreen
import by.yaugesha.hotelbooking.Admin.Hotel.Edit.EditHotelScreen
import by.yaugesha.hotelbooking.Admin.Hotel.Edit.EditRoomScreen
import by.yaugesha.hotelbooking.Admin.Hotel.Edit.HotelScreen
import by.yaugesha.hotelbooking.Admin.Hotel.HotelSearchResultScreen
import by.yaugesha.hotelbooking.Admin.User.SearchUserScreen
import by.yaugesha.hotelbooking.DataClasses.*

@Composable
fun AdminNavigation(login: String) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.SearchUserScreen.route) {
        composable(
            route = Screen.AdminSearchHotelScreen.route
        ) {
            AdminSearchHotelScreen(navController = navController)
        }
        /*composable(
            route = Screen.AdminSearchHotelScreen.route
        ) {
            AdminSearchHotelScreen(navController = navController)
        }*/
        composable(
            route = Screen.HotelSearchResultScreen.route + "/{searchParameter}/{show}",
            arguments = listOf(
                navArgument("searchParameter") {
                    type = NavType.StringType
                }, navArgument("show") {
                    type = NavType.IntType
                }
            )
        ) { entry ->
            val searchParameter = entry.arguments?.getString("searchParameter")
            val show = entry.arguments?.getInt("show")
            if (searchParameter != null) {
                if (show != null) {
                    HotelSearchResultScreen(navController = navController, searchParameter = searchParameter, show = show)
                }
            }
        }
        composable(
            route = Screen.AddHotelScreen.route
        ) {
            AddHotelScreen(navController = navController)
        }
        composable(
            route = Screen.AllBookingsScreen.route
        ) {
            AllBookingsScreen(navController = navController)
        }
        composable(
            route = Screen.AddRoomScreen.route + "/{hotelId}",
            arguments = listOf(
                navArgument("hotelId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            entry.arguments?.getString("hotelId")
                ?.let { AddRoom(navController = navController, hotelId = it) }
        }
        composable(
            route = Screen.HotelScreen.route + "/{hotelId}",
            arguments = listOf(
                navArgument("hotelId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            HotelScreen(
                navController = navController,
                hotelId = entry.arguments?.getString("hotelId")!!
            )
        }
        composable(
            route = Screen.EditRoomScreen.route + "/{room}",
            arguments = listOf(
                navArgument("room") {
                    type = RoomType()
                }
            )
        ) { entry ->
            val room = entry.arguments?.getParcelable<Room>("room")
            EditRoomScreen(
                navController = navController, room = room!!
            )
        }
        composable(
            route = Screen.EditHotelScreen.route + "/{hotel}",
            arguments = listOf(
                navArgument("hotel") {
                    type = HotelType()
                }
            )
        ) { entry ->
            val hotel = entry.arguments?.getParcelable<Hotel>("hotel")
            EditHotelScreen(
                navController = navController, hotel = hotel!!
            )
        }
        composable(
            route = Screen.SearchUserScreen.route
        ) {
            SearchUserScreen(navController = navController)
        }
    }
}
