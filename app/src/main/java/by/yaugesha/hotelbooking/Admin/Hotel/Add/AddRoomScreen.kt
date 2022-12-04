package by.yaugesha.hotelbooking.Admin.Hotel.Add

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Admin.AdminViewModel
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.Screen
import by.yaugesha.hotelbooking.Main.Screens.SortAmenities
import by.yaugesha.hotelbooking.R
import com.commandiron.wheel_picker_compose.WheelTextPicker
import okhttp3.internal.immutableListOf

@Composable
fun AddRoom(navController: NavController, hotelId: String) {
    val vm = AdminViewModel()

    val peopleCapacity = rememberSaveable { mutableStateOf("") }
    val numberOfRooms = rememberSaveable { mutableStateOf("") }
    val square = rememberSaveable { mutableStateOf("") }
    val numberOfDoubleBeds = rememberSaveable { mutableStateOf(0) }
    val numberOfSingleBeds = rememberSaveable { mutableStateOf(0) }
    val price = rememberSaveable { mutableStateOf("") }
    val amountOfRooms = rememberSaveable { mutableStateOf("") }
    val mapOfRoomAmenities = rememberSaveable { hashMapOf<String, Boolean>(
        "Kitchen" to false,
        "Bathroom" to false,
        "Wi-fi in room" to false,
        "Wheelchair accessible" to false,
        "TV in room" to false,
        "AC unit" to false,
        "Pet friendly" to false,
        "Balcony" to false,
        "No smoking" to false,
        "Breakfast included" to false
    ) }

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
            BedNumberInputField(numberOfDoubleBeds, 2)
            Spacer(modifier = Modifier.padding(start = 42.dp))
            BedNumberInputField(numberOfSingleBeds, 1)
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
                Text(text = "+ Photo", color = Color.Black, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.padding(top = 20.dp))
        RoomAmenities(mapOfRoomAmenities)
        Spacer(modifier = Modifier.padding(top = 20.dp))
        NumberInputField(price, "Price")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        NumberInputField(amountOfRooms, "Amount of rooms")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        Button(
            onClick = {navController.navigate(Screen.AddRoomScreen.route + "/" + hotelId) },
            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
            shape = (RoundedCornerShape(16.dp)),
            enabled = peopleCapacity.value.isNotEmpty() && numberOfRooms.value.isNotEmpty() &&
                    square.value.isNotEmpty() && price.value.isNotEmpty() &&
                    amountOfRooms.value.isNotEmpty() && (numberOfDoubleBeds.value != 0 ||
                    numberOfSingleBeds.value != 0) && bitmap.value != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Add another room",
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
               vm.setRoom(hotelId, peopleCapacity.value.toInt(), numberOfRooms.value.toInt(),  square.value.toInt(), price.value.toInt(),
                    amountOfRooms.value.toInt(), numberOfDoubleBeds.value, numberOfSingleBeds.value, bitmap.value!!, mapOfRoomAmenities)
                navController.navigate(Screen.HotelScreen.route + "/" + hotelId)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
            shape = (RoundedCornerShape(16.dp)),
            enabled = peopleCapacity.value.isNotEmpty() && numberOfRooms.value.isNotEmpty() &&
                    square.value.isNotEmpty() && price.value.isNotEmpty() &&
                    amountOfRooms.value.isNotEmpty() && (numberOfDoubleBeds.value != 0 ||
                    numberOfSingleBeds.value != 0) && bitmap.value != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Finish",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun RoomAmenities(mapOfRoomAmenities: HashMap<String, Boolean>) {
    Text(text = "Amenities", fontSize = 16.sp)
    Column{
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RoomAmenity("Kitchen", mapOfRoomAmenities)
            RoomAmenity("Bathroom", mapOfRoomAmenities)
            RoomAmenity("Wi-fi in room", mapOfRoomAmenities)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RoomAmenity("Wheelchair accessible", mapOfRoomAmenities)
            RoomAmenity("TV in room", mapOfRoomAmenities)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RoomAmenity("AC unit", mapOfRoomAmenities)
            RoomAmenity("Pet friendly", mapOfRoomAmenities)
            RoomAmenity("Balcony", mapOfRoomAmenities)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RoomAmenity("No smoking", mapOfRoomAmenities)
            RoomAmenity("Breakfast included", mapOfRoomAmenities)
        }
    }
}

@Composable
fun RoomAmenity(text: String, mapOfRoomAmenities: HashMap<String, Boolean>) {
    val color = remember { mutableStateOf(Color.White) }
    val textColor = remember { mutableStateOf(Color.Black) }
    if(mapOfRoomAmenities[text] == true){
        color.value = ButtonColor
        textColor.value = Color.White
    }
    else{
        color.value = Color.White
        textColor.value = Color.Black
    }

    Button(
        onClick = {
            if(color.value == Color.White) {
                color.value = ButtonColor
                textColor.value = Color.White
                mapOfRoomAmenities[text] = true
            }
            else {
                color.value = Color.White
                textColor.value = Color.Black
                mapOfRoomAmenities[text] = false
            }
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = color.value),
        shape = (RoundedCornerShape(32.dp))
    ) {
        Text(text = text, fontSize = 12.sp, color = textColor.value)
    }
}

@Composable
fun NumberInputField(string: MutableState<String>, field: String) {
    OutlinedTextField(
        string.value, onValueChange = { newText -> string.value = newText },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        //placeholder = { Text(text = "$field:", color = Color.Black, fontSize = 14.sp) },
        label = { Text(field, color = Color.Black) },
        shape = (RoundedCornerShape(24.dp)),
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            backgroundColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Composable
fun BedNumberInputField(number: MutableState<Int>, bed: Int, start: Int = 0) {
    Card(shape = (RoundedCornerShape(24.dp)),modifier = Modifier
        .height(54.dp)
        .width(130.dp)) {
        WheelTextPicker(
            size = DpSize(86.dp, 54.dp),
            selectedIndex = start,
            texts = (0..6).map { it.toString() },
            infiniteLoopEnabled = true,
            modifier = Modifier
                .background(Color.White)
                .wrapContentWidth(Alignment.End)
                .padding(end = 16.dp),
            selectorColor = Color.White.copy(alpha = 0.2f),
            selectorBorder = BorderStroke(1.dp, Color.White)
        ){ number.value = it}
        if (bed == 2)
            Icon(
                painter = painterResource(id = R.drawable.double_bed),
                tint = Color.Black,
                contentDescription = "double bed",
                modifier = Modifier
                    .wrapContentWidth(Alignment.Start)
                    .padding(start = 8.dp, end = 100.dp)
            )
        else
            Icon(
                painter = painterResource(id = R.drawable.single_bed),
                tint = Color.Black,
                contentDescription = "single bed",
                modifier = Modifier
                    .wrapContentWidth(Alignment.Start)
                    .padding(start = 8.dp)
            )
        //Text(text = "$field:", color = Color.Black, fontSize = 14.sp)
    }
}