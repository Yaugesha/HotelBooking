package by.yaugesha.hotelbooking.Main.Screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.Booking
import by.yaugesha.hotelbooking.DataClasses.Hotel
import by.yaugesha.hotelbooking.DataClasses.Room
import by.yaugesha.hotelbooking.Main.LittleNumberInputField
import by.yaugesha.hotelbooking.Main.MainViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.math.abs

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState",
    "SimpleDateFormat", "CoroutineCreationDuringComposition"
)
@Composable
fun EditBookingScreen(navController: NavController, room: Room, hotel: Hotel, booking: Booking, status: String) {
    val amenities: Map<String, Boolean> = room.amenities + hotel.amenities
    val context = LocalContext.current
    val vm = MainViewModel()
    val formatter = SimpleDateFormat("dd.MM.yyyy")
    val amountOfRooms = remember { mutableStateOf(booking.amountOfRooms.toString()) }
    val guests = rememberSaveable { mutableStateOf(booking.guests.toString()) }
    val arrivalDate = remember { mutableStateOf(booking.checkInDate) }
    val departureDate = remember { mutableStateOf(booking.checkOutDate) }
    val nights = remember { mutableStateOf((abs(formatter.parse(arrivalDate.value)!!.time
            - formatter.parse(departureDate.value)!!.time) / (1000 * 60 * 60 * 24)).toInt())}
    val cost = remember { mutableStateOf(nights.value * room.price * amountOfRooms.value.toInt()) }
    var oldBooking = booking.bookingId
    val checkBookingData = remember { mutableStateOf(false) }
    val openDeleteDialog = remember { mutableStateOf(false) }
    val openEditDialog = remember { mutableStateOf(false) }


    Scaffold(
            bottomBar = {
                if (status == "booked") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(
                            onClick = {
                                openEditDialog.value = true
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                            shape = (RoundedCornerShape(32.dp)),
                            modifier = Modifier
                                //.fillMaxWidth()
                                //.padding(start = 32.dp, end = 32.dp, bottom = 4.dp)
                                .height(62.dp)
                                .width(120.dp)
                        ) {
                            Text(
                                text = "Edit",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }


                        if (openEditDialog.value) {
                            AlertDialog(
                                onDismissRequest = { openDeleteDialog.value = false },
                                title = { Text(text = "Are you sure?\nYour booking will be edited.") },
                                shape = RoundedCornerShape(24.dp),
                                backgroundColor = Color.White,
                                buttons = {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        modifier = Modifier
                                            .padding(start = 8.dp, end = 8.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Button(
                                            onClick = {
                                                val newBooking = Booking(
                                                    bookingId = UUID.randomUUID().toString(),
                                                    user = "user",
                                                    room = room.roomId,
                                                    checkInDate = arrivalDate.value,
                                                    checkOutDate = departureDate.value,
                                                    guests = guests.value.toInt(),
                                                    amountOfRooms = amountOfRooms.value.toInt(),
                                                    cost = (cost.value),
                                                    date = LocalDate.now().format(
                                                            DateTimeFormatter.ofPattern("dd.MM.yyyy")
                                                    )
                                                )
                                                vm.viewModelScope.launch {
                                                    checkBookingData.value =
                                                        editBookingData(vm, room, newBooking)
                                                }

                                                Log.i("got value:", "${checkBookingData.value}")
                                                if (checkBookingData.value == true) {
                                                    vm.setBooking(newBooking)
                                                    vm.deleteBooking(oldBooking)
                                                    oldBooking = newBooking.bookingId
                                                } else
                                                    Toast.makeText(
                                                        context,
                                                        "Your request doesn't match",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                /*navController.navigate(Screen.UserSearchResultScreen.route)*/
                                                openEditDialog.value = false
                                            },
                                            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                                            shape = (RoundedCornerShape(32.dp)),
                                            modifier = Modifier
                                                .width(80.dp)
                                                .padding(/*start = 8.dp, end = 32.dp, */bottom = 4.dp)
                                        ) {
                                            Text(
                                                text = "Yes",
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Button(
                                            onClick = {
                                                openEditDialog.value = false
                                            },
                                            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                                            shape = (RoundedCornerShape(32.dp)),
                                            modifier = Modifier
                                                .width(80.dp)
                                                .padding(/*start = 8.dp, end = 32.dp, */bottom = 4.dp)
                                        ) {
                                            Text(
                                                text = "No",
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        //SortDialogButton(openSortDialog, "Amenities")
                                    }
                                }
                            )
                        }

                        Button(
                            onClick = { openDeleteDialog.value = true },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                            shape = (RoundedCornerShape(32.dp)),
                            modifier = Modifier
                                //.fillMaxWidth()
                                //.padding(start = 32.dp, end = 32.dp, bottom = 4.dp)
                                .height(62.dp)
                                .width(120.dp)
                        ) {
                            Text(
                                text = "Cancel",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (openDeleteDialog.value) {
                            AlertDialog(
                                onDismissRequest = { openDeleteDialog.value = false },
                                title = { Text(text = "Are you sure?\nYour booking will be canceled.") },
                                shape = RoundedCornerShape(24.dp),
                                backgroundColor = Color.White/*.copy(alpha = 0.8f)*/,
                                //modifier = Modifier.width(180.dp),
                                buttons = {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        modifier = Modifier
                                            .padding(start = 8.dp, end = 8.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Button(
                                            onClick = {
                                                vm.cancelBooking(oldBooking)
                                                openDeleteDialog.value = false
                                            },
                                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                                            shape = (RoundedCornerShape(32.dp)),
                                            modifier = Modifier
                                                .width(80.dp)
                                                .padding(/*start = 8.dp, end = 32.dp, */bottom = 4.dp)
                                        ) {
                                            Text(
                                                text = "Yes",
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Button(
                                            onClick = {
                                                openDeleteDialog.value = false
                                            },
                                            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                                            shape = (RoundedCornerShape(32.dp)),
                                            modifier = Modifier
                                                .width(80.dp)
                                                .padding(/*start = 8.dp, end = 32.dp, */bottom = 4.dp)
                                        ) {
                                            Text(
                                                text = "No",
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        //SortDialogButton(openSortDialog, "Amenities")
                                    }
                                }
                            )
                        }
                    }
                }
            }
    ) {
        Card(
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 80.dp)
                .padding(start = 24.dp, end = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(backgroundColor = Color.White,
                //shape = (RoundedCornerShape(32.dp)),
                modifier = Modifier
                    .wrapContentWidth(Alignment.Start)
                    .height(200.dp)
                    .fillMaxWidth())
            {
                AsyncImage(
                    model = room.photoURI, contentDescription = "Room img",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .width(397.dp)
                    //modifier = Modifier.clip(RoundedCornerShape(24.dp))
                )
            }
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(start = 14.dp, end = 14.dp)
            )
            {
                Spacer(modifier = Modifier.padding(108.dp))
                Text(text = "Dates:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Row {
                    Column {
                        Text(text = "From", fontSize = 14.sp)

                        Spacer(modifier = Modifier.padding(4.dp))

                        ShowDateChangePicker1(
                            context, arrivalDate, cost, room.price, departureDate,
                            amountOfRooms, nights, status == "booked")
                    }

                    Spacer(modifier = Modifier.padding(start = 64.dp))

                    Column {
                        Text(text = "To", fontSize = 14.sp)

                        Spacer(modifier = Modifier.padding(4.dp))

                        ShowDateChangePicker2(
                            context, arrivalDate, cost, room.price, departureDate,
                            amountOfRooms, nights, status == "booked"
                        )

                    }
                }
                Spacer(modifier = Modifier.padding(8.dp))

                Divider(color = Color.Black, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.padding(10.dp))

                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        //.padding(start = 18.dp, end = 18.dp)
                        .fillMaxWidth()
                ) {
                    BedsInRoom(room)
                    Spacer(Modifier.padding(4.dp))
                    Text(text = "Square: ${room.square}sqm", fontSize = 16.sp)
                    Spacer(Modifier.padding(4.dp))
                    Row {
                        Text(text = "Cost: ",fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.padding(start = 1.dp))
                        Text(text = "$",fontSize = 16.sp, modifier = Modifier.padding(start = 6.dp, top = 3.dp))
                        Spacer(modifier = Modifier.padding(start = 1.dp))
                        Text(text = "${cost.value}", fontSize = 20.sp)
                    }
                }
                Spacer(Modifier.padding(12.dp))
                Row {
                    Column {
                        Text(text = "Guests", fontSize = 14.sp)

                        Spacer(modifier = Modifier.padding(4.dp))

                        LittleNumberInputField(guests, status == "booked")

                        if(guests.value != "") {
                            if (guests.value.toInt() > room.numberOfDoubleBeds * 3 + room.numberOfSingleBeds)
                                Text(
                                    text = "Number of guests can't be more then ${room.numberOfDoubleBeds * 3 + room.numberOfSingleBeds}",
                                    color = Color.Red, fontSize = 10.sp
                                )
                        }
                    }

                    Spacer(modifier = Modifier.padding(start = 68.dp))

                    Column {
                        Text(text = "Rooms", fontSize = 14.sp)

                        Spacer(modifier = Modifier.padding(4.dp))

                        RoomsNumberInputField(amountOfRooms, cost, nights, room.price, status == "booked")

                    }
                }
                Spacer(Modifier.padding(16.dp))
                TopAmenitiesInOrder(amenities)
                Spacer(Modifier.padding(16.dp))
                val showAllAmenities = remember { mutableStateOf(false) }
                Text(text = "+ Show all amenities", textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .padding(start = 18.dp)
                        .clickable { showAllAmenities.value = true }
                )
                Spacer(Modifier.padding(16.dp))
                Text("Arrival/Departure", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 18.dp))
                Text("Check in: ${hotel.checkIn}\nCheck out: ${hotel.checkOut}", fontSize = 20.sp, modifier = Modifier.padding(start = 18.dp))
                Spacer(Modifier.padding(14.dp))
                Text("GETTING HERE\n" +
                        "${hotel.name}\n" +
                        "\n" +
                        "${hotel.building} ${hotel.street}, ${hotel.city}, ${hotel.country}, ${hotel.postCode}\n" +
                        "\n" +
                        "Tel: ${hotel.phone}", modifier = Modifier.padding(start = 18.dp, end = 18.dp))
                Spacer(Modifier.padding(36.dp))

                if(showAllAmenities.value) {
                    AlertDialog(
                        onDismissRequest = { showAllAmenities.value = false },
                        title = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(start = 8.dp, end = 8.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(text = "Amenities")
                                Column{
                                    amenities.forEach {
                                        if(it.value){
                                            Spacer(Modifier.padding(8.dp))
                                            AmenityDialogField(it.value, it.key)
                                        }
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        backgroundColor = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.width(216.dp),
                        buttons = {}
                    )
                }
            }
        }

    }
}

suspend fun editBookingData(vm: MainViewModel, room: Room, booking: Booking): Boolean {
    val result: Deferred<Boolean>
    runBlocking {
        result = async { vm.checkBookingEdition(room, booking)}
    }
    return result.await()
}