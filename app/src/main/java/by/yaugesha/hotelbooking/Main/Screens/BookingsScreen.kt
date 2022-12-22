package by.yaugesha.hotelbooking.Main.Screens

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Admin.LoadingAnimation
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.*
import by.yaugesha.hotelbooking.Main.FavoriteButton
import by.yaugesha.hotelbooking.Main.MainViewModel
import by.yaugesha.hotelbooking.Main.setHotelForRoom
import coil.compose.AsyncImage
import com.google.gson.Gson
import kotlinx.coroutines.*

fun <T> SnapshotStateList<T>.swapList(newList: List<T>){
    clear()
    addAll(newList)
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition",
    "SuspiciousIndentation"
)
@Composable
fun BookingsScreen(navController: NavController) {
    val vm = MainViewModel()
    val context = LocalContext.current
    val login = remember {vm.getLogin(context)!!}
    val bookingsList = remember { mutableStateListOf<Booking>()}
    var allBookings = listOf<Booking>()
    val sort = remember { mutableStateOf("")}
    vm.viewModelScope.launch {
        allBookings = setListOfUserBookings(vm, login)
        delay(1000)
        bookingsList.swapList(allBookings)
    }
    Log.i("allBookings:",  "$allBookings")

    val bottomItems = listOf(BarItem.Search, BarItem.Favorites, BarItem.Bookings, BarItem.Profile)
        Scaffold(
            bottomBar = { BottomBar(navController, bottomItems) }
        ) {
            if (bookingsList.isEmpty()) {
                /*Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentHeight(Alignment.CenterVertically)
                ) {
                    Text(
                        text = "No bookings found", fontSize = 40.sp,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                            .wrapContentHeight(Alignment.CenterVertically)
                    )
                }*/
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoadingAnimation()
                }
            } else {
                BookingsParametersBar(bookingsList, allBookings)
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
                        if (sort.value  == "" || sort.value == vm.defineStatusOfBooking(bookingsList[i]))
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
                                FavoriteButton(vm, login, room.roomId)
                            }
                            BookingDescriptionCard(navController, bookingsList[i], room, hotel.value,
                                vm.defineStatusOfBooking(bookingsList[i]))
                        }
                        Spacer(modifier = Modifier.padding(top = 20.dp))
                    }
                }
            }
        }
}

@Composable
fun BookingDescriptionCard(navController: NavController, booking: Booking, room: Room, hotel: Hotel, status: String) {
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
                    val roomJson = Uri.encode(Gson().toJson(room))
                    val hotelJson = Uri.encode(Gson().toJson(hotel))
                    val bookingJson = Uri.encode(Gson().toJson(booking))
                    navController.navigate(Screen.EditBookingScreen.route + "/" + roomJson.toString()
                            + "/" + hotelJson.toString() + "/" + bookingJson.toString() + "/" + status)
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
fun BookingsParametersBar(bookingsList: SnapshotStateList<Booking>, allBooking: List<Booking>) {
    val listOfSorts = remember { listOf("Booked", "Current", "Old", "Canceled", ) }
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
                val vm = MainViewModel()
                Card(
                    backgroundColor = if(selected) {ButtonColor} else {Color.White},
                    shape = (RoundedCornerShape(32.dp)),
                    modifier = Modifier
                        .width(80.dp)
                        .height(36.dp)
                        .selectable(
                        selected = selected,
                        onClick = {
                            if(!selected) {
                                bookingsList.swapList(allBooking)
                                bookingsList.swapList(vm.sortBookings(bookingsList.toMutableList(), it))
                                selectedOption.value = it
                            }
                            else {
                                bookingsList.swapList(allBooking)
                                selectedOption.value = ""
                            }
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


suspend fun setListOfUserBookings(vm: MainViewModel, login: String): List<Booking> {
    val result: Deferred<List<Booking>> = vm.viewModelScope.async { vm.getUserBookings(login)}
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