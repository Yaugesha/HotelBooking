package by.yaugesha.hotelbooking.Admin.Hotel.Edit

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Admin.AdminViewModel
import by.yaugesha.hotelbooking.Authorization.ui.theme.BackgroundColor
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.Hotel
import by.yaugesha.hotelbooking.DataClasses.Room
import by.yaugesha.hotelbooking.DataClasses.Screen
import coil.compose.AsyncImage
import com.google.gson.Gson
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


//NOW: show rooms in hotel  ADD: info about hotel
@SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")
@Composable
fun HotelScreen(navController: NavController, hotelId: String) {
    val vm = AdminViewModel()
    var roomList: MutableList<Room> = mutableListOf()
    var hotel = Hotel()
    vm.viewModelScope.launch { roomList = setRoomList(vm, hotelId).toMutableList(); hotel = setHotel(vm, hotelId) }
    Log.i("Hotel", "Got:  $hotel")

    Column(modifier = Modifier.background(BackgroundColor).fillMaxSize())
    {
        if(roomList != null)
        Row(modifier = Modifier.padding(top = 36.dp)) {
            Button(
                onClick = {
                    val json = Uri.encode(Gson().toJson(hotel))
                    navController.navigate(Screen.EditHotelScreen.route + "/" + json.toString())
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                shape = (RoundedCornerShape(16.dp)),
                modifier = Modifier
                    .padding(start = 24.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .height(44.dp)
                    .width(160.dp)
            )
            {
                Text(
                    text = "Edit hotel",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.padding(start = 14.dp))
            Button(
                onClick = {
                    navController.navigate(Screen.AddRoomScreen.route + "/" + hotelId)
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                shape = (RoundedCornerShape(16.dp)),
                modifier = Modifier
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .height(44.dp)
                    .width(160.dp)
            )
            {
                Text(
                    text = "Add room",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Column(modifier = Modifier
            .padding(top = 58.dp)
            .verticalScroll(rememberScrollState())) {
            for(i in roomList.indices step 2){
                Row {
                    RoomCard(navController, roomList[i])
                    if(roomList.size>i+1)
                        RoomCard(navController, roomList[i+1])
                }
                Spacer(Modifier.padding(top = 20.dp))
            }
        }
    }
}

@Composable
fun RoomCard(navController: NavController, room: Room) {
    val context = LocalContext.current
    Card(
        backgroundColor = Color.White,
        shape = (RoundedCornerShape(32.dp)),
        modifier = Modifier
            .padding(start = 20.dp)
            .width(160.dp)
            .height(222.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Card(backgroundColor = Color.White,
                shape = (RoundedCornerShape(32.dp)),
                modifier = Modifier
                    .wrapContentWidth(Alignment.Start)
                    .height(100.dp)
                    .fillMaxWidth())
            {
                AsyncImage( //height(513.dp).width(396.dp)
                    model = room.photoURI, contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.clip(RoundedCornerShape(32.dp))
                )
            }

            Spacer(Modifier.padding(top = 10.dp))

            var beds = ""
            if(room.numberOfDoubleBeds == 0) {
                beds = "${room.numberOfSingleBeds} single"
            }
            if(room.numberOfSingleBeds == 0) {
                beds = "${room.numberOfDoubleBeds} double"
            }
            if(room.numberOfDoubleBeds != 0 && room.numberOfSingleBeds != 0)
                beds = "${room.numberOfDoubleBeds} double + ${room.numberOfSingleBeds} single"
            Text(
                text = "Beds: $beds\nAvaileble: \nSqure: ${room.square}\n" +
                        "Price: ${room.price}$",
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(Modifier.padding(top = 6.dp))

            Button(
                onClick =
                {
                    val json = Uri.encode(Gson().toJson(room))
                    navController.navigate(Screen.EditRoomScreen.route + "/" + json.toString())
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                shape = (RoundedCornerShape(16.dp)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, start = 22.dp, end = 22.dp)
            ) {
                Text(text  = "Edit",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentSize(Alignment.Center))
            }
        }

    }
}

suspend fun setRoomList(vm: AdminViewModel, hotelId: String): List<Room> {
    val result: Deferred<List<Room>>
    runBlocking {
        result = async { vm.getRooms(hotelId) }
    }
    return result.await()
}

suspend fun setHotel(vm: AdminViewModel, hotelId: String): Hotel {
    val result: Deferred<Hotel>
    runBlocking {
        result = async { vm.getHotelById(hotelId) }
    }
    return result.await()
}