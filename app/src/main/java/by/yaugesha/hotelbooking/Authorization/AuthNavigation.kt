package by.yaugesha.hotelbooking.Authorization

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import by.yaugesha.hotelbooking.Athorisation.Screens.RegistrationScreen
import by.yaugesha.hotelbooking.Authorization.Screens.LoginScreen
import by.yaugesha.hotelbooking.DataClasses.Screen

@Composable
fun AuthNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.LoginScreen.route) {
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController)
        }
        composable(
            route = Screen.RegistrationScreen.route, // "/{login}/{password}"
            /*arguments = listOf(
                navArgument("login") {
                    type = NavType.StringType
                }
            )*/
        ) { RegistrationScreen(navController = navController)
            //entry ->
            //entry.arguments?.getString("login")?.let { AdminMenuScreen(login = it) }
        }
    }
}