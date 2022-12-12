package by.yaugesha.hotelbooking.Main.Screens

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.*
import by.yaugesha.hotelbooking.Main.FavoriteButton
import by.yaugesha.hotelbooking.Main.MainViewModel
import by.yaugesha.hotelbooking.Main.SortDialogButton
import by.yaugesha.hotelbooking.Main.setHotelForRoom
import by.yaugesha.hotelbooking.R
import coil.compose.AsyncImage
import com.google.gson.Gson
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition",
    "SuspiciousIndentation"
)
@Composable
fun BookingsScreen(navController: NavController) {
    val context = LocalContext.current
    val vm = MainViewModel()
    var bookingsList: List<Booking> = listOf()
    vm.viewModelScope.launch {bookingsList = setListOfUserBookings(vm, "user")}
    Log.i("got UserBookings:",  "$bookingsList")

    val bottomItems = listOf(BarItem.Search, BarItem.Favorites, BarItem.Bookings, BarItem.Profile)
        Scaffold(
            bottomBar = { BottomBar(navController, bottomItems) }
        ) {
            if (bookingsList.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentHeight(Alignment.CenterVertically)
                ) {
                    Text(
                        text = "No rooms found", fontSize = 40.sp,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                            /*.wrapContentWidth(Alignment.CenterHorizontally)*/
                            .wrapContentHeight(Alignment.CenterVertically)
                    )
                }
            } else {
                BookingsParametersBar(navController)
                Column(
                    modifier = Modifier
                        .padding(top = 120.dp, bottom = 68.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    for (i in bookingsList.indices) {
                        var room = Room()
                        vm.viewModelScope.launch { room = getBookingRoom(vm, bookingsList[i].room) }
                        Log.i("got room:", room.toString())
                        val hotel = rememberSaveable { mutableStateOf(Hotel()) }
                        vm.viewModelScope.launch { hotel.value = setHotelForRoom(vm, room.hotelID) }
                        Card(
                            shape = (RoundedCornerShape(24.dp)),
                            backgroundColor = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .height(180.dp)
                                .width(360.dp)
                        ) {
                            Box {
                                Card(
                                    modifier = Modifier
                                        .wrapContentWidth(Alignment.Start)
                                        .height(180.dp)
                                        .width(140.dp)
                                        .fillMaxHeight()
                                ) {
                                    AsyncImage( //height(513.dp).width(396.dp)
                                        model = hotel.value.photoURI, contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.clip(RoundedCornerShape(24.dp))
                                    )
                                }
                                FavoriteButton(vm, "user", room.roomId)
                            }
                            BookingDescriptionCard(
                                navController,
                                bookingsList[i],
                                room,
                                hotel.value
                            )
                        }
                        Spacer(modifier = Modifier.padding(top = 20.dp))
                    }
                }
            }
        }
}

@Composable
fun BookingDescriptionCard(navController: NavController, booking: Booking, room: Room, hotel: Hotel) {
    Column(
        modifier = Modifier
            .padding(top = 8.dp, start = 152.dp, bottom = 8.dp, end = 18.dp)
    ) {
        Text(text = hotel.name, fontSize = 20.sp)

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
                    Text(text = booking.cost.toString(), fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.padding(top = 2.dp))

                val formatter = SimpleDateFormat("dd.MM.yyyy")
                val status: String = if(booking.status == "active") {
                    if (formatter.parse(booking.checkInDate).time >= Date.from(
                            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
                        ).time
                    )
                        "current"
                    else
                        "old"
                } else {
                    booking.status
                }
                Text(
                    text = hotel.city + ", " + hotel.country + "\n" + booking.checkInDate + "-" + booking.checkOutDate
                    + "\nStatus: " + status,
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
                    val formatter = SimpleDateFormat("dd.MM.yyyy")
                    val roomJson = Uri.encode(Gson().toJson(room))
                    val hotelJson = Uri.encode(Gson().toJson(hotel))
                    val bookingJson = Uri.encode(Gson().toJson(booking))
                    navController.navigate(Screen.EditBookingScreen.route + "/" + roomJson.toString()
                            + "/" + hotelJson.toString() + "/" + bookingJson.toString())
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                shape = (RoundedCornerShape(16.dp)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)
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

suspend fun setListOfUserBookings(vm: MainViewModel, login: String): List<Booking> {
    val result: Deferred<List<Booking>>
    runBlocking {
        result = async { vm.getUserBookings(login)}
    }
    return result.await()
}

suspend fun getBookingRoom(vm: MainViewModel, roomId: String): Room {
    Log.i("room id:", roomId)
    val result: Deferred<Room>
    runBlocking {
        result = async { vm.getRoomById(roomId)}
    }
    return result.await()
}