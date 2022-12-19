package by.yaugesha.hotelbooking.Main.Screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.Booking
import by.yaugesha.hotelbooking.DataClasses.Hotel
import by.yaugesha.hotelbooking.DataClasses.Room
import by.yaugesha.hotelbooking.DataClasses.Search
import by.yaugesha.hotelbooking.Main.LittleNumberInputField
import by.yaugesha.hotelbooking.Main.MainViewModel
import by.yaugesha.hotelbooking.R
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
fun OrderScreen(navController: NavController, searchData: Search, room: Room, hotel: Hotel) {
    val amenities: Map<String, Boolean> = room.amenities + hotel.amenities
    val context = LocalContext.current
    val vm = MainViewModel()
    val formatter = SimpleDateFormat("dd.MM.yyyy")
    val rooms = rememberSaveable { mutableStateOf(searchData.rooms.toString()) }
    val arrivalDate = rememberSaveable { mutableStateOf(formatter.format(searchData.checkInDate)) }
    val departureDate = rememberSaveable { mutableStateOf(formatter.format(searchData.checkOutDate)) }
    val nights = rememberSaveable { mutableStateOf((abs(formatter.parse(arrivalDate.value)!!.time
            - formatter.parse(departureDate.value)!!.time) / (1000 * 60 * 60 * 24)).toInt())}
    val cost = rememberSaveable { mutableStateOf(nights.value * room.price * rooms.value.toInt()) }
    val checkBookingData = rememberSaveable { mutableStateOf(false) }
    val guests = rememberSaveable { mutableStateOf(searchData.guests.toString()) }
    val openConfirmDialog = rememberSaveable { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    openConfirmDialog.value = true
                    /*navController.navigate(Screen.UserSearchResultScreen.route)*/
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                shape = (RoundedCornerShape(32.dp)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 32.dp, bottom = 4.dp)
                    .height(62.dp)
            ) {
                Text(text = "Confirm", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            if(openConfirmDialog.value) {
                AlertDialog(
                    onDismissRequest = { openConfirmDialog.value = false },
                    title = { Text(text = "Are you sure?", textAlign = TextAlign.Center)  },
                    shape = RoundedCornerShape(24.dp),
                    backgroundColor = Color.White,
                    buttons = {
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp).fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    vm.viewModelScope.launch {checkBookingData.value = checkBookingData(vm, room, Search
                                        (searchData.location, searchData.guests, formatter.parse(arrivalDate.value)!!,
                                        formatter.parse(departureDate.value)!!, rooms.value.toInt()
                                    ))}

                                    Log.i("got value:",  "${checkBookingData.value}")
                                    if(checkBookingData.value == true &&
                                        guests.value.toInt() > room.numberOfDoubleBeds * 3 + room.numberOfSingleBeds) {
                                        val booking = Booking(
                                            bookingId = UUID.randomUUID().toString(),
                                            user = "user",
                                            room = room.roomId,
                                            guests = guests.value.toInt(),
                                            checkInDate = arrivalDate.value,
                                            checkOutDate = departureDate.value,
                                            amountOfRooms = rooms.value.toInt(),
                                            cost = (cost.value),
                                            date = LocalDate.now()
                                                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
                                        )
                                        vm.setBooking(booking)
                                    }
                                    else
                                        Toast.makeText(context, "Your request doesn't match", Toast.LENGTH_LONG).show()
                                    openConfirmDialog.value = false
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                                shape = (RoundedCornerShape(32.dp)),
                                modifier = Modifier
                                    .width(80.dp)
                                    .padding(/*start = 8.dp, end = 32.dp, */bottom = 4.dp)
                            ) {
                                Text(text = "Yes", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    openConfirmDialog.value = false
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                                shape = (RoundedCornerShape(32.dp)),
                                modifier = Modifier
                                    .width(80.dp)
                                    .padding(/*start = 8.dp, end = 32.dp, */bottom = 4.dp)
                            ) {
                                Text(text = "No", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            //SortDialogButton(openSortDialog, "Amenities")
                        }
                    }
                )
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
                    //.width(397.dp)
                    //modifier = Modifier.clip(RoundedCornerShape(24.dp))
                )
            }
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(start = 14.dp, end = 14.dp)
            )
            {
                Spacer(modifier = Modifier.padding(108.dp))
                //Spacer(modifier = Modifier.padding(12.dp))
                Text(text = "Dates:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Row {
                    Column {
                        Text(text = "From", fontSize = 14.sp)

                        Spacer(modifier = Modifier.padding(4.dp))

                        ShowDateChangePicker1(context, arrivalDate, cost, room.price, departureDate,rooms, nights)
                    }

                    Spacer(modifier = Modifier.padding(start = 64.dp))

                    Column {
                        Text(text = "To", fontSize = 14.sp)

                        Spacer(modifier = Modifier.padding(4.dp))

                        ShowDateChangePicker2(context, arrivalDate, cost, room.price, departureDate,rooms, nights)

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

                        LittleNumberInputField(guests)

                        if(guests.value.toInt() > room.numberOfDoubleBeds * 3 + room.numberOfSingleBeds)
                            Text(
                                text = "Number of guests can't be more then ${room.numberOfDoubleBeds * 3 + room.numberOfSingleBeds}",
                                color = Color.Red, fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.padding(start = 68.dp))

                    Column {
                        Text(text = "Rooms", fontSize = 14.sp)

                        Spacer(modifier = Modifier.padding(4.dp))

                        RoomsNumberInputField(rooms, cost, nights, room.price)

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

@Composable
fun RoomsNumberInputField(value: MutableState<String>, cost: MutableState<Int>, nights: MutableState<Int>, price: Int) {
    OutlinedTextField(
        value.value, {
            value.value = it
            if(it != null && it != "")
                cost.value = it.toInt() * price * nights.value},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = (RoundedCornerShape(24.dp)),
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
        modifier = Modifier
            .height(52.dp)
            .width(124.dp)
    )
}

@Composable
fun ShowDateChangePicker1(context: Context, arrivalDate: MutableState<String>, cost: MutableState<Int>,
                          price: Int, departureDate: MutableState<String>, rooms: MutableState<String>,
                          nights: MutableState<Int>,){
    val formatter = SimpleDateFormat("dd.MM.yyyy")
    val year: Int
    val month: Int
    val day: Int
    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            arrivalDate.value = "$dayOfMonth.${month+1}.$year"
            nights.value = (abs(formatter.parse(arrivalDate.value)!!.time
                    - formatter.parse(departureDate.value)!!.time) / (1000 * 60 * 60 * 24)).toInt()
            cost.value = nights.value.toInt() * price * rooms.value.toInt()
        }, year, month, day
    )
    Row {
        Button(
            onClick = { datePickerDialog.show() },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            shape = (RoundedCornerShape(24.dp)),
            modifier = Modifier
                .height(52.dp)
                .width(124.dp)
        ) {
            Text(text = arrivalDate.value)
        }
    }
}

@Composable
fun ShowDateChangePicker2(context: Context, arrivalDate: MutableState<String>, cost: MutableState<Int>,
                          price: Int, departureDate: MutableState<String>, rooms: MutableState<String>,
                          nights: MutableState<Int>,){
    val formatter = SimpleDateFormat("dd.MM.yyyy")
    val year: Int
    val month: Int
    val day: Int
    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            departureDate.value = "$dayOfMonth.${month+1}.$year"
            nights.value = (abs(formatter.parse(arrivalDate.value)!!.time
                    - formatter.parse(departureDate.value)!!.time) / (1000 * 60 * 60 * 24)).toInt()
            cost.value = nights.value.toInt() * price * rooms.value.toInt()
        }, year, month, day
    )
    Row {
        Button(
            onClick = { datePickerDialog.show() },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            shape = (RoundedCornerShape(24.dp)),
            modifier = Modifier
                .height(52.dp)
                .width(124.dp)
        ) {
            Text(text = departureDate.value)
        }
    }
}

@Composable
fun TopAmenitiesInOrder(amenities: Map<String, Boolean>) {
    Row() {
        Column() {
            TopAmenity(R.drawable.ic_wifi, "Wi-fi in room", amenities["Wi-fi in room"]!!)
            Spacer(Modifier.padding(20.dp))
            TopAmenity(R.drawable.ic_ac_unit, "AC unit", amenities["AC unit"]!!)
            Spacer(Modifier.padding(20.dp))
            TopAmenity(R.drawable.ic_smoke_free, "No smoking", amenities["No smoking"]!!)
            Spacer(Modifier.padding(20.dp))
            TopAmenity(R.drawable.ic_bar, "Hotel bar", amenities["Hotel bar"]!!)
        }
        Spacer(modifier = Modifier.padding(end = 80.dp))
        Column(/*modifier = Modifier.padding(start = 24.dp, end = 24.dp)*/) {
            TopAmenity(R.drawable.ic_parking, "Free parking", amenities["Free parking"]!!)
            Spacer(Modifier.padding(20.dp))
            TopAmenity(R.drawable.ic_pets, "Pet friendly", amenities["Pet friendly"]!!)
            Spacer(Modifier.padding(20.dp))
            TopAmenity(R.drawable.ic_gym, "Gym", amenities["Gym"]!!)
            Spacer(Modifier.padding(20.dp))
            TopAmenity(R.drawable.ic_restaurant, "Restaurant", amenities["Restaurant"]!!)
        }
    }
}

suspend fun checkBookingData(vm: MainViewModel, room: Room, searchData: Search): Boolean {
    val result: Deferred<Boolean>
    runBlocking {
        result = async { vm.checkIfRoomsFree(room, searchData)}
    }
    return result.await()
}