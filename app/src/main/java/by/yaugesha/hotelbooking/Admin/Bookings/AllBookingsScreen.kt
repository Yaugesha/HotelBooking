package by.yaugesha.hotelbooking.Admin

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.*
import by.yaugesha.hotelbooking.Main.MainViewModel
import by.yaugesha.hotelbooking.Main.Screens.BookingsParametersBar
import by.yaugesha.hotelbooking.Main.Screens.getBookingRoom
import by.yaugesha.hotelbooking.Main.Screens.swapList
import by.yaugesha.hotelbooking.Main.setHotelForRoom
import coil.compose.AsyncImage
import com.google.gson.Gson
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun AllBookingsScreen(navController: NavController) {
    val bottomItems = listOf(BarItem.Users, BarItem.Hotels, BarItem.UsersBookings, BarItem.AdminProfile)
    val vm = AdminViewModel()
    val sort = remember { mutableStateOf("") }
    val bookingsList = remember { mutableStateListOf<Booking>() }
    var allBookings = listOf<Booking>()
    vm.viewModelScope.launch {allBookings = setListOfBookings(vm) }
    bookingsList.swapList(allBookings)

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
                    text = "No bookings found", fontSize = 40.sp,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                        .wrapContentHeight(Alignment.CenterVertically)
                )
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
                    vm.viewModelScope.launch { room = getBookingRoom(MainViewModel(), bookingsList[i].room) }
                    Log.i("got room:", room.toString())
                    val hotel = rememberSaveable { mutableStateOf(Hotel()) }
                    vm.viewModelScope.launch { hotel.value = setHotelForRoom(MainViewModel(), room.hotelID) }
                    if (sort.value  == "" || sort.value == MainViewModel().defineStatusOfBooking(bookingsList[i]))
                        Card(
                            shape = (RoundedCornerShape(24.dp)),
                            backgroundColor = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .height(196.dp)
                                .width(360.dp)
                        ) {
                            Box {
                                Card(
                                    modifier = Modifier
                                        .wrapContentWidth(Alignment.Start)
                                        .height(196.dp)
                                        .width(140.dp)
                                        .fillMaxHeight()
                                ) {
                                    AsyncImage( //height(513.dp).width(396.dp)
                                        model = hotel.value.photoURI, contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.clip(RoundedCornerShape(24.dp))
                                    )
                                }
                            }
                        BookingDescriptionCard(navController, bookingsList[i], room, hotel.value,
                            MainViewModel().defineStatusOfBooking(bookingsList[i]))
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
                .height(96.dp)
                .width(192.dp)
        ) {
            Column(modifier = Modifier.wrapContentHeight(Alignment.CenterVertically)) {
                Spacer(modifier = Modifier.padding(top = 2.dp))
                Text(text = "Booked by ${booking.user}", fontSize = 14.sp, modifier = Modifier.padding(start = 6.dp))
                Spacer(modifier = Modifier.padding(top = 2.dp))
                Row {
                    Text(
                        text = "$",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 6.dp, top = 3.dp)
                    )
                    Text(text = "${booking.cost}", fontSize = 14.sp)
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

                    navController.navigate(Screen.BookingDescriptionScreen.route + "/" + roomJson.toString()
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

suspend fun setListOfBookings(vm: AdminViewModel): List<Booking> {
    val result: Deferred<List<Booking>>
    runBlocking {
        result = async { vm.getBookings()}
    }
    return result.await()
}