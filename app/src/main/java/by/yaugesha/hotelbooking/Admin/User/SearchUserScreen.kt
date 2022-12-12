package by.yaugesha.hotelbooking.Admin.User

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import by.yaugesha.hotelbooking.DataClasses.BarItem
import by.yaugesha.hotelbooking.DataClasses.BottomBar
import by.yaugesha.hotelbooking.DataClasses.User
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
    val context = LocalContext.current
    var userList: List<User> = listOf()
    val bottomItems =
        listOf(BarItem.Users, BarItem.Hotels, BarItem.UsersBookings, BarItem.AdminProfile)
    Scaffold(
        bottomBar = { BottomBar(navController, bottomItems) }
    ) {
        vm.viewModelScope.launch {userList = setUserList(vm) }
        SearchUserParametersBar(navController)
        Column(modifier = Modifier.padding(top = 170.dp)) {
            for (i in userList.indices) {
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
                            .padding(top = 12.dp/*, start = 16.dp, end = 12.dp*/, bottom = 12.dp)
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)

                    ) {
                        Text(
                            text = "Login: ${userList[i].login}",
                            fontSize = 14.sp,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.padding(top = 4.dp))

                        Text(
                            text = "email: ${userList[i].email}",
                            fontSize = 14.sp,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.padding(top = 4.dp))

                        if (userList[i].role == "user") {
                            Row {
                                Text(
                                    text = "Name: ${userList[i].name}",
                                    fontSize = 14.sp,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.padding(start = 8.dp))

                                Text(
                                    text = "Surname: ${userList[i].surname}",
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.padding(top = 8.dp))

                        Row {
                            Text(
                                text = "Status: ${userList[i].status}",
                                fontSize = 14.sp,
                                color = Color.White,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Spacer(modifier = Modifier.padding(start = 24.dp))

                            if (userList[i].role == "user") {
                                TinyButton(context, "", "History")

                                Spacer(modifier = Modifier.padding(start = 8.dp))

                                TinyButton(context, "", "Edit")
                            } else
                                Text(
                                    "Role: ${userList[i].role}",
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(top = 4.dp, end = 100.dp)
                                )
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(top = 20.dp))
            }
        }
    }
}

@Composable
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
}


@Composable
fun TinyButton(context: Context, interaction: String, action: String) {
    Button(
        onClick = { context.startActivity(Intent(context, interaction::class.java) ) },
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