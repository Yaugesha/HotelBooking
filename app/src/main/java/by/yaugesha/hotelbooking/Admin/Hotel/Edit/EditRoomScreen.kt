package by.yaugesha.hotelbooking.Admin.Hotel.Edit

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Admin.AdminViewModel
import by.yaugesha.hotelbooking.Admin.Hotel.Add.BedNumberInputField
import by.yaugesha.hotelbooking.Admin.Hotel.Add.NumberInputField
import by.yaugesha.hotelbooking.Admin.Hotel.Add.RoomAmenities
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.Room
import by.yaugesha.hotelbooking.DataClasses.Screen
import coil.compose.AsyncImage
import kotlinx.coroutines.*

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun EditRoomScreen(navController: NavController, room: Room) {
    val vm = AdminViewModel()
    val peopleCapacity = rememberSaveable { mutableStateOf("${room.peopleCapacity}") }
    val numberOfRooms = rememberSaveable { mutableStateOf("${room.numberOfRooms}") }
    val square = rememberSaveable { mutableStateOf("${room.square}") }
    val numberOfDoubleBeds = rememberSaveable { mutableStateOf(room.numberOfDoubleBeds) }
    val numberOfSingleBeds = rememberSaveable { mutableStateOf(room.numberOfSingleBeds) }
    val price = rememberSaveable { mutableStateOf("${room.price}") }
    val amountOfRooms = rememberSaveable { mutableStateOf("${room.amountOfRooms}") }
    val mapOfRoomAmenities = rememberSaveable { room.amenities }

    Column(
        modifier = Modifier
            .padding(start = 45.dp, end = 45.dp, top = 34.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        NumberInputField(peopleCapacity, "People capacity")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        NumberInputField(numberOfRooms, "Number of rooms")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        NumberInputField(square, "Square")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            BedNumberInputField(numberOfDoubleBeds, 2, numberOfDoubleBeds.value)
            Spacer(modifier = Modifier.padding(start = 42.dp))
            BedNumberInputField(numberOfSingleBeds, 1,numberOfSingleBeds.value)
        }
        Spacer(modifier = Modifier.padding(top = 20.dp))

        val context = LocalContext.current
        val bitmap = remember { mutableStateOf<Bitmap?>(null) }
        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent())
            {
                //uri: Uri? -> imageUriState = uri
                val source =
                    it?.let { it1 -> ImageDecoder.createSource(context.contentResolver, it1) }
                bitmap.value = source?.let { it1 -> ImageDecoder.decodeBitmap(it1) }
            }
        Button(
            onClick = { launcher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            shape = (RoundedCornerShape(16.dp)),
            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally)
        ) {
            if (bitmap.value != null)
                Image(
                    bitmap.value!!.asImageBitmap(),
                    contentDescription = "Hotel image",
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                )
            else
                AsyncImage( //height(513.dp).width(396.dp)
                    model = room.photoURI, contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.clip(RoundedCornerShape(32.dp))
                )
        }
        Spacer(modifier = Modifier.padding(top = 20.dp))
        RoomAmenities(mapOfRoomAmenities)
        Spacer(modifier = Modifier.padding(top = 20.dp))
        NumberInputField(price, "Price")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        NumberInputField(amountOfRooms, "Amount of rooms")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        Button(
            onClick =
            {
                Log.i("edit room", "fragment:  $room")
                val editedRoom = Room(
                    roomId = room.roomId, hotelID =  room.hotelID, peopleCapacity = peopleCapacity.value.toInt(),
                    numberOfRooms = numberOfRooms.value.toInt(), square = square.value.toInt(),
                    price = price.value.toInt(), amountOfRooms = amountOfRooms.value.toInt(),
                    numberOfDoubleBeds = numberOfDoubleBeds.value, numberOfSingleBeds = numberOfSingleBeds.value,
                    photoURI = room.photoURI, status =  room.status, amenities = mapOfRoomAmenities
                )
                vm.updateRoom(editedRoom, bitmap.value)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
            shape = (RoundedCornerShape(16.dp)),
            enabled = peopleCapacity.value.isNotEmpty() && numberOfRooms.value.isNotEmpty() &&
                    square.value.isNotEmpty() && price.value.isNotEmpty() &&
                    amountOfRooms.value.isNotEmpty() && (numberOfDoubleBeds.value != 0 ||
                    numberOfSingleBeds.value != 0),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Refresh",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.padding(top = 20.dp))

        Button(
            onClick =
            {
                vm.deleteRoom(room.roomId)
                navController.navigate(Screen.HotelScreen.route + "/" + room.hotelID)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
            shape = (RoundedCornerShape(16.dp)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Delete",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}


suspend fun getRoom(vm: AdminViewModel, roomId: String): Room {
    val result: Deferred<Room>
    runBlocking {
        result = async { vm.getRoomById(roomId) }
    }
    return result.await()
}