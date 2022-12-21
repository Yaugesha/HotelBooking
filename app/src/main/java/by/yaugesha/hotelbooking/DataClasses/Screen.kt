package by.yaugesha.hotelbooking.DataClasses

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import by.yaugesha.hotelbooking.Authorization.ui.theme.AdminCardColor
import by.yaugesha.hotelbooking.R

sealed class Screen(val route: String) {

    // Authorization
    object LoginScreen : Screen("login_screen")
    object RegistrationScreen : Screen("registration_screen")

    // Admin
    object AdminUsersMenuScreen : Screen("admin_users_menu_screen")
    object AddHotelScreen : Screen("add_hotel_screen")
    object AddRoomScreen : Screen("add_room_screen")
    object HotelScreen : Screen("hotel_screen")
    object EditHotelScreen : Screen("edit_hotel_screen")
    object EditRoomScreen : Screen("edit_room_screen")
    object AdminSearchHotelScreen : Screen("admin_search_hotel_screen")
    object HotelSearchResultScreen : Screen("hotel_search_result_screen")
    object SearchUserScreen : Screen("search_hotel_result_screen")
    object AllBookingsScreen : Screen("all_bookings_screen")
    object UserBookingsScreen : Screen("user_bookings_screen")
    object BookingDescriptionScreen : Screen("booking_description_screen")

    //User
    object UserSearchScreen : Screen("user_search_screen")
    object UserSearchResultScreen : Screen("user_search_result_screen")
    object EditProfileScreen : Screen("edit_profile_screen")
    object FavoritesScreen : Screen("favorites_screen")
    object ProfileScreen : Screen("profile_screen")
    object BookingsScreen : Screen("bookings_screen")
    object RoomScreen : Screen("room_screen")
    object OrderScreen : Screen("order_screen")
    object SortScreen : Screen("sort_screen")
    object EditBookingScreen : Screen("edit_booking_screen")


}
    @Composable
    fun BottomBar(navController: NavController, bottomItems: List<BarItem>) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        BottomNavigation(
            backgroundColor = AdminCardColor,
            contentColor = Color.White
        ) {
            bottomItems.forEach { item ->
                BottomNavigationItem(
                    icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                    label = { Text(text = item.title) },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.White.copy(0.4f),
                    alwaysShowLabel = true,
                    selected = currentRoute == item.screen_route,
                    onClick = {
                        navController.navigate(item.screen_route) {

                            navController.graph.startDestinationRoute?.let { screen_route ->
                                popUpTo(screen_route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }


    sealed class BarItem(var screen_route: String, var icon: Int, var title: String) {
        object Search: BarItem(Screen.UserSearchScreen.route, R.drawable.ic_search,"Search" )
        object Favorites: BarItem(Screen.FavoritesScreen.route, R.drawable.ic_favorite,"Favourites" )
        object Bookings: BarItem(Screen.BookingsScreen.route, R.drawable.ic_luggage,"Bookings" )
        object Profile: BarItem(Screen.ProfileScreen.route, R.drawable.ic_account,"Profile" )


        object Users: BarItem(Screen.SearchUserScreen.route, R.drawable.ic_users,"Users" )
        object Hotels: BarItem(Screen.AdminSearchHotelScreen.route, R.drawable.ic_hotel,"Hotels" )
        object UsersBookings: BarItem(Screen.AllBookingsScreen.route, R.drawable.ic_bookings,"Bookings" )
        object AdminProfile: BarItem(Screen.ProfileScreen.route, R.drawable.ic_account,"Profile" )
    }

    /*fun withArgs(args: String): String {
        return buildString {
            append(route)
            args.forEach { arg->
                append("/$arg")
            }
        }
    }*/
