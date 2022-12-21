package by.yaugesha.hotelbooking.Admin.User

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import by.yaugesha.hotelbooking.Main.MainViewModel
import by.yaugesha.hotelbooking.Main.Screens.swapList
import by.yaugesha.hotelbooking.Main.SortDialogButton
import by.yaugesha.hotelbooking.R
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun SearchUserScreen(navController: NavController) {
    val vm = AdminViewModel()
    val bottomItems =
        listOf(BarItem.Users, BarItem.Hotels, BarItem.UsersBookings, BarItem.AdminProfile)
    Scaffold(
        bottomBar = { BottomBar(navController, bottomItems) }
    ) {
        val usersList = remember { mutableStateListOf<User>() }
        var allUsers = listOf<User>()
        vm.viewModelScope.launch {allUsers = setUserList(vm) }
        usersList.swapList(allUsers)
        UserParametersBar(usersList, allUsers)
        Column(modifier = Modifier
            .padding(top = 170.dp)
            .verticalScroll(rememberScrollState())
        ) {
            if(usersList.isNotEmpty()) {
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
}

/*@Composable
fun SearchUserParametersBar(navController: NavController) {
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
        val openFilterDialog = remember { mutableStateOf(false) }
        Card(
            shape = (RoundedCornerShape(24.dp)),
            elevation = 0.dp,
            border = BorderStroke(0.dp, Color.White),
            modifier = Modifier
                .padding(start = 52.dp, end = 238.dp)
                .clickable {
                    openFilterDialog.value = true
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_tune),
                contentDescription = "Filters",
                modifier = Modifier
                    .wrapContentWidth(Alignment.Start)
                    .wrapContentHeight(Alignment.CenterVertically)
            )
            Text(
                text = "Filters", fontSize = 14.sp,
                modifier = Modifier
                    .wrapContentWidth(Alignment.End)
                    .wrapContentHeight(Alignment.CenterVertically)
            )
        }
        if(openFilterDialog.value) {
        AlertDialog(
            onDismissRequest = { openFilterDialog.value = false },
            title = { Text(text = "Filter by") },
            shape = RoundedCornerShape(24.dp),
            backgroundColor = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.width(180.dp),
            buttons = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                ) {
                    SortDialogButton(openFilterDialog, "Admin")
                    SortDialogButton(openFilterDialog, "User")
                    SortDialogButton(openFilterDialog, "Active")
                    SortDialogButton(openFilterDialog, "Blocked")
                }
            }
        )
    }

        val openSortDialog = remember { mutableStateOf(false) }
        Card(
            shape = (RoundedCornerShape(24.dp)),
            elevation = 0.dp,
            border = BorderStroke(0.dp, Color.White),
            modifier = Modifier
                .padding(start = 250.dp, end = 52.dp)
                .clickable {
                    openSortDialog.value = true
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_sort),
                contentDescription = "Sort",
                modifier = Modifier
                    .wrapContentWidth(Alignment.Start)
                    .wrapContentHeight(Alignment.CenterVertically)
            )
            Text(
                text = "Sort", fontSize = 14.sp,
                modifier = Modifier
                    .wrapContentWidth(Alignment.End)
                    .wrapContentHeight(Alignment.CenterVertically)
            )
        }
        if(openSortDialog.value) {
            AlertDialog(
                onDismissRequest = { openSortDialog.value = false },
                title = { Text(text = "Sort by") },
                shape = RoundedCornerShape(24.dp),
                backgroundColor = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.width(180.dp),
                buttons = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    ) {
                        SortDialogButton(openSortDialog, "Bookings max")
                        SortDialogButton(openSortDialog, "Bookings min")
                        SortDialogButton(openSortDialog, "Paid max")
                        SortDialogButton(openSortDialog, "Paid min")
                    }
                }
            )
        }
    }
}*/
@Composable
fun UserParametersBar(usersList: SnapshotStateList<User>, allUsers: List<User>) {
    val listOfSorts = remember { listOf("Admin", "User"/*, "Old", "Canceled",*/ ) }
    val selectedOption = remember { mutableStateOf("All") }
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
                                Log.i("users", usersList.toString())
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
    //val vm = AdminViewModel()
    val result: Deferred<List<User>>
    runBlocking {
        result = async { vm.getUsers() }
    }
    return result.await()
}