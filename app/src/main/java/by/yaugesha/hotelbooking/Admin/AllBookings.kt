package by.yaugesha.hotelbooking.Admin

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.BarItem
import by.yaugesha.hotelbooking.DataClasses.BottomBar
import by.yaugesha.hotelbooking.DataClasses.Screen
import by.yaugesha.hotelbooking.Main.SortDialogButton
import by.yaugesha.hotelbooking.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AllBookingsScreen(navController: NavController) {
    val bottomItems = listOf(BarItem.Users, BarItem.Hotels, BarItem.UsersBookings, BarItem.AdminProfile)
    Scaffold(
        bottomBar = { BottomBar(navController, bottomItems) }
    ) {
        BookingsParametersBar(navController)
        Column(
            modifier = Modifier
                .padding(top = 120.dp, bottom = 68.dp)
                .verticalScroll(rememberScrollState())
        ) {
            for (i in 1..3) {
                Card(
                    shape = (RoundedCornerShape(24.dp)),
                    backgroundColor = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .height(180.dp)
                        .width(360.dp)
                ) {
                    Image(
                            painter = painterResource(id = R.drawable.mariott),
                            contentDescription = "example of hotel img 396x513",
                            modifier = Modifier
                                .wrapContentWidth(Alignment.Start)
                        )

                    BookingDescriptionCard(navController)
                }
                Spacer(modifier = Modifier.padding(top = 20.dp))
            }
        }
    }
}

@Composable
fun BookingDescriptionCard(navController: NavController) {
    Column(
        modifier = Modifier
            .padding(top = 8.dp, start = 152.dp, bottom = 8.dp, end = 18.dp)
    ) {
        Text(text = "Marriott Hotel", fontSize = 20.sp)

        Spacer(modifier = Modifier.padding(top = 8.dp))

        Card(
            shape = (RoundedCornerShape(24.dp)),
            modifier = Modifier
                .height(82.dp)
                .width(192.dp)
        ) {
            Column(modifier = Modifier.wrapContentHeight(Alignment.CenterVertically)) {
                Row {
                    Text(
                        text = "$",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 6.dp, top = 3.dp)
                    )
                    Text(text = "450", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.padding(top = 2.dp))

                Text(
                    text = "Minsk, Belarus\nDates: 20.12.2022-23.12.2022\nStatus: Booked",
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(start = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.padding(top = 12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick =
                {
                    navController.navigate(Screen.OrderScreen.route)
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                shape = (RoundedCornerShape(16.dp)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp)
            ) {
                Text(
                    text = "Show details",
                    fontSize = 11.sp,
                    color = Color.White,
                )
            }
        }

    }
}

@Composable
fun BookingsParametersBar(navController: NavController) {
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
        Card(
            shape = (RoundedCornerShape(24.dp)),
            elevation = 0.dp,
            border = BorderStroke(0.dp, Color.White),
            modifier = Modifier
                .padding(start = 52.dp, end = 238.dp)
                .clickable {
                    navController.navigate(Screen.SortScreen.route + "/" + "false")
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
                        SortDialogButton(openSortDialog, "Price min")
                        SortDialogButton(openSortDialog, "Price max")
                        SortDialogButton(openSortDialog, "Square max")
                        SortDialogButton(openSortDialog, "Square min")
                        SortDialogButton(openSortDialog, "Date new")
                        SortDialogButton(openSortDialog, "Date old")
                        //SortDialogButton(openSortDialog, "Amenities")
                    }
                }
            )
        }
    }
}