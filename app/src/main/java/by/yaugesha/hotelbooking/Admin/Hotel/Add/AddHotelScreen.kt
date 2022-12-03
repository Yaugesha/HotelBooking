package by.yaugesha.hotelbooking.Admin.Hotel.Add

import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Admin.AdminViewModel
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.Screen
import by.yaugesha.hotelbooking.Main.Screens.SortAmenities
import java.util.*

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun AddHotelScreen(navController: NavController) {
    val vm = AdminViewModel()

    val hotelName = rememberSaveable { mutableStateOf("") }
    val country = rememberSaveable { mutableStateOf("") }
    val city = rememberSaveable { mutableStateOf("") }
    val street = rememberSaveable { mutableStateOf("") }
    val building = rememberSaveable { mutableStateOf("") }
    val postCode = rememberSaveable { mutableStateOf("") }
    val phoneNumber = rememberSaveable { mutableStateOf("") }
    val checkIn = rememberSaveable { mutableStateOf("Check in") }
    val checkOut = rememberSaveable { mutableStateOf("Check Out") }

    val imageURI = remember{ mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent())
        {
            //uri: Uri? -> imageUriState = uri
            val source = it?.let { it1 -> ImageDecoder.createSource(context.contentResolver, it1) }
            bitmap.value = source?.let { it1 -> ImageDecoder.decodeBitmap(it1) }
            if (it != null) {
                imageURI.value = it
            }
        }

    Column(
        modifier = Modifier
            .padding(start = 45.dp, end = 45.dp, top = 34.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TextField(hotelName, "Hotel name")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        TextField(country, "Country")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        TextField(city, "City")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        TextField(street, "Street")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        TextField(building, "Building")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        TextField(postCode, "Post code")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        PhoneNumberInputField(phoneNumber, "Phone number")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            TimePicker(context, checkIn)
            Spacer(modifier = Modifier.padding(start = 42.dp))
            TimePicker(context, checkOut)
        }
        Spacer(modifier = Modifier.padding(top = 20.dp))

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
        HotelAmenities()
        Spacer(modifier = Modifier.padding(top = 20.dp))
        Button(
            onClick =
            {
                vm.setHotel (
                    hotelName.value, country.value,  city.value, street.value, building.value,
                    postCode.value, phoneNumber.value, checkIn.value, checkOut.value, bitmap.value!!
                )
                navController.navigate(Screen.AddRoomScreen.route + "/" + vm.getHotelId())
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
            shape = (RoundedCornerShape(16.dp)),
            enabled = hotelName.value.isNotEmpty() && country.value.isNotEmpty() &&
                    city.value.isNotEmpty() && street.value.isNotEmpty() &&
                    building.value.isNotEmpty() && postCode.value.isNotEmpty() &&
                    phoneNumber.value.isNotEmpty() && checkIn.value.isNotEmpty() &&
                    checkOut.value.isNotEmpty() && bitmap.value != null,
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = "Next step",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }

}

@Composable
fun HotelAmenities() {
    Text(text = "Amenities", fontSize = 16.sp)
    Column{
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HotelAmenity("Free parking")
            HotelAmenity("Hotel bar")
            HotelAmenity("Spa")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HotelAmenity("Departure from airport")
            HotelAmenity("Casino")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HotelAmenity("Swimming pool")
            SortAmenities("Cribs")
            HotelAmenity("Laundry")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HotelAmenity("Business services")
            HotelAmenity("Outdoor space")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HotelAmenity("Wi-fi in lobby")
            HotelAmenity("Restaurant")
            HotelAmenity("Gym")
        }
    }
}

@Composable
fun HotelAmenity(text: String) {
    val color = remember { mutableStateOf(Color.White) }
    val textColor = remember { mutableStateOf(Color.Black) }
    Button(
        onClick = {
            if(color.value == Color.White) {
                color.value = ButtonColor
                textColor.value = Color.White
            }
            else {
                color.value = Color.White
                textColor.value = Color.Black
            }
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = color.value),
        shape = (RoundedCornerShape(32.dp)),
//        modifier = Modifier
//            .
    ) {
        Text(text = text, fontSize = 12.sp, color = textColor.value)
    }
}

@Composable
fun TextField(string: MutableState<String>, field: String) {
    OutlinedTextField(
        string.value, onValueChange = { newText -> string.value = newText },
        label = { Text(field, color = Color.Black) },
        shape = (RoundedCornerShape(24.dp)),
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors( textColor = Color.Black, backgroundColor = Color.White ),
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Composable
fun PhoneNumberInputField(string: MutableState<String>, field: String) {
    OutlinedTextField(
        string.value, onValueChange = { newText -> string.value = newText },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        label = { Text(field, color = Color.Black) },
        shape = (RoundedCornerShape(24.dp)),
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors( textColor = Color.Black, backgroundColor = Color.White ),
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Composable
fun TimePicker(context: Context, string: MutableState<String>){
    val mCalendar = Calendar.getInstance()
    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]

    val mTimePickerDialog = TimePickerDialog(
        context,
        {_, mHour : Int, mMinute: Int ->
            string.value = "$mHour:$mMinute"
        }, mHour, mMinute, false
    )
    Button(
        onClick = { mTimePickerDialog.show() },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
        shape = (RoundedCornerShape(24.dp)),
        modifier = Modifier
            .height(48.dp)
            .width(130.dp)
    ) {
        Text(text = string.value, color = Color.Black)
    }
}