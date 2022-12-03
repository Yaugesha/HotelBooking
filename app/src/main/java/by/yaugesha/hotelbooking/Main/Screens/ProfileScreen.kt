package by.yaugesha.hotelbooking.Main.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Authorization.ui.theme.AdminCardColor
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.BarItem
import by.yaugesha.hotelbooking.DataClasses.BottomBar
import by.yaugesha.hotelbooking.DataClasses.Screen

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfileScreen(navController: NavController) {
    val bottomItems = listOf(BarItem.Search, BarItem.Favorites, BarItem.Bookings, BarItem.Profile)
    Scaffold(
        bottomBar = { BottomBar(navController, bottomItems) }
    ) {
        Column(
            modifier = Modifier
                .background(AdminCardColor)
                .fillMaxWidth()
                .height(140.dp)
        ) {
            Spacer(Modifier.padding(12.dp))
            Text(
                text = "Shishkoloby Gnidodav", color = Color.White,
                fontSize = 32.sp, modifier = Modifier.padding(start = 10.dp)
            )
            Text(
                text = "Login: Pedrachkov", color = Color.White,
                modifier = Modifier.padding(start = 10.dp)
            )
            Text(
                text = "Email: vasya@gmail.com", color = Color.White,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 140.dp, start = 16.dp, end = 16.dp, bottom = 68.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.padding(top = 22.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.EditProfileScreen.route)
                },
                shape = (RoundedCornerShape(24.dp)),
                colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                modifier = Modifier
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Edit profile", fontSize = 40.sp, color = Color.White,
                    modifier = Modifier
                        .height(100.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .wrapContentHeight(Alignment.CenterVertically)
                )
            }
            Spacer(modifier = Modifier.padding(top = 40.dp))

            val openDeleteDialog = remember { mutableStateOf(false) }
            Button(
                onClick = {
                    openDeleteDialog.value = true
                },
                shape = (RoundedCornerShape(24.dp)),
                colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                modifier = Modifier
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Delete profile", fontSize = 40.sp, color = Color.White,
                    modifier = Modifier
                        .height(100.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .wrapContentHeight(Alignment.CenterVertically)
                )
            }
            if (openDeleteDialog.value) {
                AlertDialog(
                    onDismissRequest = { openDeleteDialog.value = false },
                    title = { Text(text = "Are you sure?") },
                    shape = RoundedCornerShape(24.dp),
                    backgroundColor = Color.White,
                    modifier = Modifier.width(280.dp),
                    buttons = {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                        ) {
                            Button(
                                shape = (RoundedCornerShape(24.dp)),
                                colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                                modifier = Modifier.weight(1f).width(100.dp),
                                onClick = { openDeleteDialog.value = false }
                            ) {
                                Text("Delete", color = Color.White)
                            }
                            Spacer(modifier = Modifier.padding(12.dp))
                            Button(
                                shape = (RoundedCornerShape(24.dp)),
                                colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                                modifier = Modifier.weight(1f).width(100.dp),
                                onClick = { openDeleteDialog.value = false }
                            ) {
                                Text("Cancel", color = Color.White)
                            }
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.padding(top = 40.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.LoginScreen.route)
                },
                shape = (RoundedCornerShape(24.dp)),
                colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                modifier = Modifier
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Exit", fontSize = 40.sp, color = Color.White,
                    modifier = Modifier
                        .height(100.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .wrapContentHeight(Alignment.CenterVertically)
                )
            }
        }
    }
}