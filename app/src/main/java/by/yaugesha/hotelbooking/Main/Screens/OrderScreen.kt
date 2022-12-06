package by.yaugesha.hotelbooking.Main.Screens

import android.annotation.SuppressLint
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
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.Booking
import by.yaugesha.hotelbooking.DataClasses.Hotel
import by.yaugesha.hotelbooking.DataClasses.Room
import by.yaugesha.hotelbooking.DataClasses.Search
import by.yaugesha.hotelbooking.Main.LittleNumberInputField
import by.yaugesha.hotelbooking.Main.MainViewModel
import by.yaugesha.hotelbooking.Main.ShowDatePicker1
import by.yaugesha.hotelbooking.Main.ShowDatePicker2
import by.yaugesha.hotelbooking.R
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState",
    "SimpleDateFormat"
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
    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    val booking = Booking(id = UUID.randomUUID().toString(), user = "user", room = room.roomId,
                        checkInDate = arrivalDate.value, checkOutDate = departureDate.value, amountOfRooms = rooms.value.toInt(),
                        cost = (room.price * ((searchData.checkOutDate.getTime() - searchData.checkInDate.getTime())
                                / (1000 * 60 * 60 * 24))).toInt(), date = LocalDate.now()
                    )
                    vm.setBooking(booking)
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
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(start = 14.dp, end = 14.dp)
            )
            {
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
                Spacer(modifier = Modifier.padding(12.dp))
                Text(text = "Dates:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Row {
                    Column {
                        Text(text = "From", fontSize = 14.sp)

                        Spacer(modifier = Modifier.padding(4.dp))

                        ShowDatePicker1(context, arrivalDate)
                    }

                    Spacer(modifier = Modifier.padding(start = 64.dp))

                    Column {
                        Text(text = "To", fontSize = 14.sp)

                        Spacer(modifier = Modifier.padding(4.dp))

                        ShowDatePicker2(context, departureDate)

                    }
                }
                Spacer(modifier = Modifier.padding(8.dp))

                Divider(color = Color.Black, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.padding(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    BedsInRoom(room)
                    Text(text = "Square: ${room.square}sqm", fontSize = 16.sp)
                }
                Spacer(Modifier.padding(12.dp))
                Column {
                    Text(text = "Rooms", fontSize = 14.sp)

                    Spacer(modifier = Modifier.padding(4.dp))

                    LittleNumberInputField(rooms)

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