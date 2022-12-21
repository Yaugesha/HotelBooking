package by.yaugesha.hotelbooking.Admin.Bookings

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Authorization.ui.theme.AdminCardColor
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.*
import by.yaugesha.hotelbooking.Main.MainViewModel
import by.yaugesha.hotelbooking.Main.Screens.*
import by.yaugesha.hotelbooking.Main.setHotelForRoom
import coil.compose.AsyncImage
import com.google.gson.Gson
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UserBookingsScreen(navController: NavController) {
    val vm = MainViewModel()
    val bookingsList = remember { mutableStateListOf<Booking>() }
    var allBookings = listOf<Booking>()
    val sort = remember { mutableStateOf("") }
    vm.viewModelScope.launch { allBookings = setListOfUserBookings(vm, "user") }
    bookingsList.swapList(allBookings)

    Scaffold {
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
            Column(
                modifier = Modifier
                    .background(AdminCardColor)
                    .fillMaxWidth()
                    .height(30.dp)
            ) {
                Spacer(Modifier.padding(2.dp))
                Text(
                    text = "user's bookings", color = Color.White, textAlign = TextAlign.Center,
                    fontSize = 18.sp, modifier = Modifier.fillMaxWidth()
                )
            }
            BookingsParametersBar(bookingsList, allBookings)
            Column(
                modifier = Modifier
                    .padding(top = 114.dp/*, bottom = 68.dp*/)
                    .verticalScroll(rememberScrollState())
            ) {
                for (i in bookingsList.indices) {
                    var room = Room()
                    vm.viewModelScope.launch { room = getBookingRoom(vm, bookingsList[i].room) }
                    Log.i("got room:", room.toString())
                    val hotel = rememberSaveable { mutableStateOf(Hotel()) }
                    vm.viewModelScope.launch { hotel.value = setHotelForRoom(vm, room.hotelID) }
                    if (sort.value == "" || sort.value == vm.defineStatusOfBooking(bookingsList[i]))
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
                            }
                            BookingDescriptionCardForAdmin(
                                navController, bookingsList[i], room, hotel.value,
                                vm.defineStatusOfBooking(bookingsList[i])
                            )
                        }
                    Spacer(modifier = Modifier.padding(top = 20.dp))
                }
            }
        }
    }
}

@Composable
fun BookingDescriptionCardForAdmin(navController: NavController, booking: Booking, room: Room, hotel: Hotel, status: String) {
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