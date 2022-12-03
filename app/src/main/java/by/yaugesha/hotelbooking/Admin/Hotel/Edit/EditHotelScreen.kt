package by.yaugesha.hotelbooking.Admin.Hotel.Edit

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
import by.yaugesha.hotelbooking.Admin.Hotel.Add.HotelAmenities
import by.yaugesha.hotelbooking.Admin.Hotel.Add.PhoneNumberInputField
import by.yaugesha.hotelbooking.Admin.Hotel.Add.TextField
import by.yaugesha.hotelbooking.Admin.Hotel.Add.TimePicker
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.Hotel
import coil.compose.AsyncImage

@Composable
fun EditHotelScreen(navController: NavController, hotel: Hotel) {
    Log.i("edit hotel", "Got:  $hotel")
    val hotelName = rememberSaveable { mutableStateOf(hotel.name) }
    val country = rememberSaveable { mutableStateOf(hotel.country) }
    val city = rememberSaveable { mutableStateOf(hotel.city) }
    val street = rememberSaveable { mutableStateOf(hotel.street) }
    val building = rememberSaveable { mutableStateOf(hotel.building) }
    val postCode = rememberSaveable { mutableStateOf(hotel.postCode) }
    val phoneNumber = rememberSaveable { mutableStateOf(hotel.phone) }
    val checkIn = rememberSaveable { mutableStateOf(hotel.checkIn) }
    val checkOut = rememberSaveable { mutableStateOf(hotel.checkOut) }

    val context = LocalContext.current
    val vm = AdminViewModel()
    Column(modifier = Modifier
        .padding(start = 45.dp, end = 45.dp, top = 34.dp)
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
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
                    model = hotel.photoURI, contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.clip(RoundedCornerShape(32.dp))
                )
        }

        Spacer(modifier = Modifier.padding(top = 20.dp))
        HotelAmenities()
        Spacer(modifier = Modifier.padding(top = 20.dp))
        Button(
            onClick =
            {
                val editedHotel = Hotel( hotelId = hotel.hotelId,name = hotelName.value,country = country.value,
                    street = street.value, building = building.value,phone = phoneNumber.value,postCode = postCode.value,
                    checkIn = checkIn.value,checkOut = checkOut.value, city = city.value, photoURI = hotel.photoURI,
                    status = hotel.status
                )
                vm.updateHotel(editedHotel, bitmap.value)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
            shape = (RoundedCornerShape(16.dp)),
            enabled = hotelName.value.isNotEmpty() && country.value.isNotEmpty() &&
                    city.value.isNotEmpty() && street.value.isNotEmpty() &&
                    building.value.isNotEmpty() && postCode.value.isNotEmpty() &&
                    phoneNumber.value.isNotEmpty() && checkIn.value.isNotEmpty() &&
                    checkOut.value.isNotEmpty(),
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
    }
}
