package by.yaugesha.hotelbooking.Admin.User

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Admin.AdminViewModel
import by.yaugesha.hotelbooking.Authorization.ui.theme.AdminCardColor
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.*
import by.yaugesha.hotelbooking.Main.Screens.swapList
import kotlinx.coroutines.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun SearchUserScreen(navController: NavController) {
    val vm = AdminViewModel()
    val bottomItems =
        listOf(BarItem.Users, BarItem.Hotels, BarItem.UsersBookings, BarItem.AdminProfile)
    val usersList = remember { mutableStateListOf<User>() }
    var allUsers = listOf<User>()
    vm.viewModelScope.launch(Dispatchers.Main) {
        allUsers = setUserList(vm)
        usersList.swapList(allUsers)
    }
    Scaffold(
        bottomBar = { BottomBar(navController, bottomItems) }
    ) {
        UserParametersBar(usersList, allUsers)
        Column(
            modifier = Modifier
                .padding(top = 102.dp)
                .verticalScroll(rememberScrollState())
        ) {
            for (i in usersList.indices) {
                Card(
                    shape = (RoundedCornerShape(32.dp)),
                    backgroundColor = AdminCardColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .height(134.dp)
                        .width(296.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 12.dp, start = 18.dp, end = 18.dp, bottom = 12.dp)
                            .fillMaxWidth()
                        //.wrapContentWidth(Alignment.CenterHorizontally)

                    ) {
                        Text(
                            text = "Login: ${usersList[i].login}",
                            fontSize = 14.sp,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.padding(top = 4.dp))

                        Text(
                            text = "email: ${usersList[i].email}",
                            fontSize = 14.sp,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.padding(top = 4.dp))

                        if (usersList[i].role == "user") {
                            Row {
                                Text(
                                    text = "Name: ${usersList[i].name}",
                                    fontSize = 14.sp,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.padding(start = 8.dp))

                                Text(
                                    text = "Surname: ${usersList[i].surname}",
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.padding(top = 8.dp))

                        Row {
                            Text(
                                "Role: ${usersList[i].role}",
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.padding(32.dp))
                            if (usersList[i].role == "user") {
                                TinyButton(navController, "History")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(top = 20.dp))
            }
        }

    }
}

@Composable
fun UserParametersBar(usersList: SnapshotStateList<User>, allUsers: List<User>) {
    val listOfSorts = remember { listOf("Admin", "User"/*, "Old", "Canceled",*/ ) }
    val selectedOption = remember { mutableStateOf("All") }
    Log.i("bar got", usersList.toString())
    Card(
        shape = (RoundedCornerShape(24.dp)),
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
            .width(360.dp)
            .height(60.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(),Arrangement.SpaceEvenly, Alignment.CenterVertically) {
            listOfSorts.forEach {
                val selected = selectedOption.value == it
                val vm = AdminViewModel()
                Card(
                    backgroundColor = if(selected) {ButtonColor} else {Color.White},
                    shape = (RoundedCornerShape(32.dp)),
                    modifier = Modifier
                        .width(80.dp)
                        .height(36.dp)
                        .selectable(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    usersList.swapList(allUsers)
                                    usersList.swapList(vm.sortUsers(usersList.toMutableList(), it))
                                    selectedOption.value = it
                                } else {
                                    usersList.swapList(allUsers)
                                    selectedOption.value = ""
                                }
                                //Log.i("users", usersList.toString())
                            }
                        )
                ) {
                    Text(
                        text = it, fontSize = 12.sp, color = if(selected) {Color.White} else {Color.Black},
                        modifier = Modifier.wrapContentSize(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun TinyButton(navController: NavController, action: String) {
    Button(
        onClick = {
            navController.navigate(Screen.UserBookingsScreen.route)
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
        shape = (RoundedCornerShape(12.dp)),
        modifier = Modifier
            //.fillMaxWidth()
            .height(30.dp)
            .width(82.dp)
        /*.wrapContentWidth(Alignment.CenterHorizontally)
        .wrapContentHeight(Alignment.CenterVertically)*/
        //.padding(bottom = 6.dp)
    )
    {
        Text(
            text = action,
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(Alignment.CenterVertically)
        )
    }

}


suspend fun setUserList(vm: AdminViewModel): List<User> {
    val result: Deferred<List<User>> = vm.viewModelScope.async {vm.getUsers()}
    return result.await()
}