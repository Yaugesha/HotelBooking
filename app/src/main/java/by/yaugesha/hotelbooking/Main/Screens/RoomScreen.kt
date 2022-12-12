package by.yaugesha.hotelbooking.Main.Screens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Authorization.ui.theme.AdminCardColor
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.Hotel
import by.yaugesha.hotelbooking.DataClasses.Room
import by.yaugesha.hotelbooking.DataClasses.Screen
import by.yaugesha.hotelbooking.DataClasses.Search
import by.yaugesha.hotelbooking.Main.MainViewModel
import by.yaugesha.hotelbooking.Main.isRoomInFavorites
import by.yaugesha.hotelbooking.R
import coil.compose.AsyncImage
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun RoomScreen(navController: NavController, searchData: Search, room: Room, hotel: Hotel) {
    val amenities: Map<String, Boolean> = room.amenities + hotel.amenities
    val vm = MainViewModel()
    val nights = Math.abs(searchData.checkOutDate.getTime() - searchData.checkInDate.getTime()) / (1000 * 60 * 60 * 24)
    val formatter = SimpleDateFormat("dd.MMM")
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
                    text = "${hotel.city}, ${hotel.country}\n\n${formatter.format(searchData.checkInDate)} - " +
                           "${formatter.format(searchData.checkOutDate)}\tGuests: ${searchData.guests}, Rooms: ${searchData.rooms}",
                    color = Color.White, fontSize = 16.sp, textAlign = TextAlign.Center,
                )
            }
        },
        bottomBar = {
            Button(
                onClick = {
                    val roomJson = Uri.encode(Gson().toJson(room))
                    val hotelJson = Uri.encode(Gson().toJson(hotel))
                    val searchJson = Uri.encode(Gson().toJson(searchData))
                    navController.navigate(Screen.OrderScreen.route + "/" + roomJson.toString()
                            + "/" + hotelJson.toString() + "/" + searchJson.toString())
                },
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

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(start = 18.dp, end = 18.dp)
                    .fillMaxWidth()
            ) {
                BedsInRoom(room)
                Spacer(Modifier.padding(4.dp))
                Row( modifier = Modifier.padding(end = 18.dp)) {
                    Text(text = "Square: ${room.square}sqm", fontSize = 16.sp)

                    Spacer(Modifier.padding(6.dp))

                    val favouriteVisible = remember { mutableStateOf(false) }
                    vm.viewModelScope.launch {
                        favouriteVisible.value = isRoomInFavorites(vm, room.roomId, "user")
                    }
                    if (favouriteVisible.value)
                    {
                        IconButton(
                            onClick = {
                                vm.deleteFavorite(room.roomId, "user")
                                favouriteVisible.value = ! favouriteVisible.value
                            },
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
                            onClick = {
                                vm.setFavorite(room.roomId, "user")
                                favouriteVisible.value = !favouriteVisible.value
                            },
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
                Spacer(Modifier.padding(4.dp))
                Row {
                    Text(text = "Price: ",fontSize = 20.sp)
                    Spacer(modifier = Modifier.padding(start = 1.dp))
                    Text(text = "$",fontSize = 16.sp, modifier = Modifier.padding(start = 6.dp, top = 3.dp))
                    Spacer(modifier = Modifier.padding(start = 1.dp))
                    Text(text = "${room.price} per night", fontSize = 20.sp)
                }
            }
            Divider(color = Color.Black, modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, start = 18.dp, end = 18.dp)
            )
            Spacer(Modifier.padding(8.dp))
            Text(text = "Popular amenities", fontSize = 20.sp, modifier = Modifier.padding(start = 18.dp))
            Spacer(Modifier.padding(12.dp))
            TopAmenities(amenities)
            Spacer(Modifier.padding(16.dp))
            val showAllAmenities = remember { mutableStateOf(false) }
            Text(text = "+ Show all amenities", textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .padding(start = 18.dp)
                    .clickable { showAllAmenities.value = true }
            )
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

@Composable
fun AmenityDialogField(amenityState: Boolean, text: String) {
    val color = remember { mutableStateOf(Color.White) }
    val textColor = remember { mutableStateOf(Color.Black) }
    Card(
        shape = RoundedCornerShape(32.dp),
        backgroundColor = color.value,
    ) {
        Text(text, fontSize = 16.sp, color = textColor.value, modifier = Modifier.padding(8.dp))
    }
}

@Composable
fun TopAmenities(amenities: Map<String, Boolean>) {
    Row(modifier = Modifier.padding(start = 32.dp)) {
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

