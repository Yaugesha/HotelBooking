package by.yaugesha.hotelbooking.Main.Screens

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Authorization.ui.theme.AdminCardColor
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.Hotel
import by.yaugesha.hotelbooking.DataClasses.Room
import by.yaugesha.hotelbooking.DataClasses.Screen
import by.yaugesha.hotelbooking.R
import coil.compose.AsyncImage

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RoomScreen(navController: NavController, room: Room, hotel: Hotel) {
    //val amenites: Map<String, Boolean> = room.amenities + hotel.am
    Scaffold(
        topBar = {
            Card(
                backgroundColor = AdminCardColor,
                shape = (RoundedCornerShape(0.dp)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
            ) {
                Text(
                    text = "${hotel.city}, ${hotel.country}\n\n20 dec. - 22 dec.\tGuests: 3, Rooms: 1",
                    color = Color.White, fontSize = 16.sp, textAlign = TextAlign.Center,
                )
            }
        },
        bottomBar = {
            Button(
                onClick = { navController.navigate(Screen.OrderScreen.route) },
                colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                shape = (RoundedCornerShape(24.dp)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 18.dp, bottom = 4.dp)
                    .height(62.dp)
//                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                Text(text = "Book", color = Color.White, fontSize = 20.sp )
            }
        }
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 70.dp)
            .verticalScroll(rememberScrollState())) {

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

            Row(modifier = Modifier.padding(top = 22.dp, end = 18.dp)) {
                Text(text = hotel.name, fontSize = 20.sp, modifier = Modifier.padding(start = 18.dp))
                Spacer(Modifier.padding(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_location_on),
                    contentDescription = "location",
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(text = hotel.street + " " + hotel.building, fontSize = 16.sp, modifier = Modifier.padding(top = 4.dp))
            }
            Divider(color = Color.Black, modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 12.dp))
            Spacer(modifier = Modifier.padding(8.dp))
            Text(text = "Description", fontSize = 20.sp, modifier = Modifier.padding(start = 18.dp))
            Spacer(modifier = Modifier.padding(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 18.dp, end = 18.dp)
                    .fillMaxWidth()
            ) {
                BedsInRoom(room)
                //Spacer(Modifier.padding(10.dp))
                Row( modifier = Modifier.padding(end = 18.dp)) {
                    Text(text = "Square: ${room.square}sqm", fontSize = 16.sp)
                    val favouriteVisible = rememberSaveable { mutableStateOf(false) }
                    Spacer(Modifier.padding(6.dp))

                    if (favouriteVisible.value)
                    {
                        IconButton(
                            onClick = {  favouriteVisible.value = ! favouriteVisible.value },
                            modifier = Modifier
                                .height(24.dp)
                                .width(24.dp)
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_heart_red),
                                contentDescription = "Favorite",
                                tint = Color.Red
                            )
                        }
                    }
                    else
                        IconButton(
                            onClick = {  favouriteVisible.value = ! favouriteVisible.value },
                            modifier = Modifier
                                .height(24.dp)
                                .width(24.dp)
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_favorite),
                                contentDescription = "Favorite"
                            )
                        }
                }
            }
            Divider(color = Color.Black, modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, start = 18.dp, end = 18.dp)
            )
            Spacer(Modifier.padding(8.dp))
            Text(text = "Popular amenities", fontSize = 20.sp, modifier = Modifier.padding(start = 18.dp))
            Spacer(Modifier.padding(12.dp))
            TopAmenities()
            Spacer(Modifier.padding(16.dp))
            Text(text = "+ Show all amenities", modifier = Modifier.padding(start = 18.dp))
            Spacer(Modifier.padding(10.dp))
            Text("Arrival/Departure", fontSize = 20.sp, fontWeight = Bold, modifier = Modifier.padding(start = 18.dp))
            Text("Check in: ${hotel.checkIn}\nCheck out: ${hotel.checkOut}", fontSize = 20.sp, modifier = Modifier.padding(start = 18.dp))
            Spacer(Modifier.padding(14.dp))
            Text("GETTING HERE\n" +
                    "${hotel.name}\n" +
                    "\n" +
                    "${hotel.building} ${hotel.street}, ${hotel.city}, ${hotel.country}, ${hotel.postCode}\n" +
                    "\n" +
                    "Tel: ${hotel.phone}", modifier = Modifier.padding(start = 18.dp, end = 18.dp))
            Spacer(Modifier.padding(16.dp))

        }
    }

}

@Composable
fun TopAmenities(/*amenites: Map<String, Boolean>*/) {
    Row(modifier = Modifier.padding(start = 32.dp)) {
        Column() {
            TopAmenity(R.drawable.ic_wifi, "Wi-fi in room", true)
            Spacer(Modifier.padding(20.dp))
            TopAmenity(R.drawable.ic_ac_unit, "AC unit", false)
            Spacer(Modifier.padding(20.dp))
            TopAmenity(R.drawable.ic_smoke_free, "non-smoking", true)
            Spacer(Modifier.padding(20.dp))
            TopAmenity(R.drawable.ic_bar, "Bar", true)
        }
        Spacer(modifier = Modifier.padding(end = 80.dp))
        Column(/*modifier = Modifier.padding(start = 24.dp, end = 24.dp)*/) {
            TopAmenity(R.drawable.ic_parking, "Free parking", true)
            Spacer(Modifier.padding(20.dp))
            TopAmenity(R.drawable.ic_pets, "Pets friendly", false)
            Spacer(Modifier.padding(20.dp))
            TopAmenity(R.drawable.ic_gym, "Gym", true)
            Spacer(Modifier.padding(20.dp))
            TopAmenity(R.drawable.ic_restaurant, "Restaurant", true)
        }
    }
}

@Composable
fun TopAmenity(icon: Int, description: String, isAvailable: Boolean) {
    var alpha = 1f
    if (!isAvailable)
        alpha = 0.2f
    Row {
       Icon(painter = painterResource(icon), contentDescription = description, tint = Color.Black.copy(alpha))
        Text(text = description, color = Color.Black.copy(alpha))
    }
}

@Composable
fun BedsInRoom(room: Room)
{
    Row {
        if(room.numberOfSingleBeds == 0) {
            if(room.numberOfDoubleBeds > 1)
                Text(text = room.numberOfDoubleBeds.toString() + "×", fontSize = 28.sp)
            Icon(painter = painterResource(id = R.drawable.ic_big_double_bed), contentDescription = "location"
            )
        }
        if(room.numberOfDoubleBeds != 0 && room.numberOfSingleBeds != 0) {
            if(room.numberOfDoubleBeds > 1)
                Text(text = room.numberOfDoubleBeds.toString() + "×", fontSize = 28.sp)
            Icon(
                painter = painterResource(id = R.drawable.ic_big_double_bed),
                contentDescription = "location"
            )
            Text(text = " + ", fontSize = 28.sp)
            if(room.numberOfSingleBeds > 1)
                Text(text = room.numberOfSingleBeds.toString() + "×", fontSize = 28.sp)
            Icon(
                painter = painterResource(id = R.drawable.ic_big_single_bed),
                contentDescription = "location"
            )
        }
        if(room.numberOfDoubleBeds == 0) {
            if(room.numberOfSingleBeds > 1)
                Text(text = room.numberOfSingleBeds.toString() + "×", fontSize = 28.sp)
            Icon(
                painter = painterResource(id = R.drawable.ic_big_single_bed),
                contentDescription = "location"
            )
        }
    }
}

