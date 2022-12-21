package by.yaugesha.hotelbooking.Admin.Bookings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.DataClasses.Booking
import by.yaugesha.hotelbooking.DataClasses.Hotel
import by.yaugesha.hotelbooking.DataClasses.Room
import by.yaugesha.hotelbooking.Main.Screens.BedsInRoom
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import kotlin.math.abs

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState",
    "SimpleDateFormat", "CoroutineCreationDuringComposition"
)
@Composable
fun BookingDescriptionScreen(navController: NavController, room: Room, hotel: Hotel, booking: Booking) {
    val formatter = SimpleDateFormat("dd.MM.yyyy")
    val nights = rememberSaveable { mutableStateOf((abs(formatter.parse(booking.checkInDate)!!.time
            - formatter.parse(booking.checkOutDate)!!.time) / (1000 * 60 * 60 * 24)).toInt())}
    val cost = rememberSaveable { mutableStateOf(nights.value * room.price * booking.amountOfRooms) }

    Scaffold() {
        Card(
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 28.dp)
                .padding(start = 24.dp, end = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                backgroundColor = Color.White,
                modifier = Modifier
                    .wrapContentWidth(Alignment.Start)
                    .height(200.dp)
                    .fillMaxWidth()
            )
            {
                AsyncImage(
                    model = room.photoURI, contentDescription = "Room img",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 14.dp, end = 14.dp)
            )
            {
                Spacer(modifier = Modifier.padding(108.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Text(text = "Booked by ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = booking.user, fontSize = 16.sp)
                    }
                    Row {
                        Text(text = "Hotel: ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = hotel.name, fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.padding(8.dp))

                Divider(color = Color.Black, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.padding(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Date of booking: ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = booking.date, fontSize = 16.sp)
                    }
                    Row {
                        Text(text = "Status:  ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = booking.status, fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.padding(8.dp))

                Divider(color = Color.Black, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.padding(10.dp))


                Text(text = "Dates:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "From: ${booking.checkInDate}", fontSize = 14.sp)
                    Text(text = "To: ${booking.checkOutDate}", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.padding(8.dp))

                Divider(color = Color.Black, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.padding(10.dp))

                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    BedsInRoom(room)
                    Spacer(Modifier.padding(4.dp))
                    Row {
                        Text(text = "Cost: ", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.padding(start = 1.dp))
                        Text(
                            text = "$",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 6.dp, top = 3.dp)
                        )
                        Spacer(modifier = Modifier.padding(start = 1.dp))
                        Text(text = "${cost.value}", fontSize = 20.sp)
                    }
                }
                Spacer(Modifier.padding(8.dp))
                Row {
                    Text(text = "Guests: ", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${booking.guests}", fontSize = 20.sp)
                }

                Spacer(Modifier.padding(8.dp))
                Row {
                    Text(text = "Rooms: ", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${booking.amountOfRooms}", fontSize = 20.sp)
                }

                Spacer(Modifier.padding(14.dp))
                Text(
                    "Location:\n" +
                            "${hotel.building} ${hotel.street}, ${hotel.city}, ${hotel.country}, ${hotel.postCode}\n" +
                            "\n" +
                            "Tel: ${hotel.phone}"
                )
                Spacer(Modifier.padding(36.dp))
            }
        }
    }
}