package by.yaugesha.hotelbooking.Main.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.Room
import by.yaugesha.hotelbooking.Main.LittleNumberInputField
import by.yaugesha.hotelbooking.Main.ShowDatePicker1
import by.yaugesha.hotelbooking.Main.ShowDatePicker2
import by.yaugesha.hotelbooking.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun OrderScreen(navController: NavController) {
    val context = LocalContext.current
    Scaffold(
        bottomBar = {
            Button(
                onClick = { /*navController.navigate(Screen.UserSearchResultScreen.route)*/ },
                colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                shape = (RoundedCornerShape(32.dp)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 32.dp, bottom = 4.dp)
                    .height(62.dp)
//                    .wrapContentWidth(Alignment.CenterHorizontally)
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
                    Image(
                        painter = painterResource(R.drawable.hotelstoke), contentDescription = "Room img",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .width(397.dp)
                    )
                }
                Spacer(modifier = Modifier.padding(12.dp))
                Text(text = "Dates:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Row {
                    Column {
                        Text(text = "From", fontSize = 14.sp)

                        Spacer(modifier = Modifier.padding(4.dp))

                        ShowDatePicker1(context, mutableStateOf(" "))
                    }

                    Spacer(modifier = Modifier.padding(start = 64.dp))

                    Column {
                        Text(text = "To", fontSize = 14.sp)

                        Spacer(modifier = Modifier.padding(4.dp))

                        ShowDatePicker2(context, mutableStateOf(" "))

                    }
                }
                Spacer(modifier = Modifier.padding(8.dp))

                Divider(color = Color.Black, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.padding(10.dp))

                val room = Room(numberOfDoubleBeds = 2, numberOfSingleBeds = 2)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BedsInRoom(room)
                    Text(text = "Square: 76sqm", fontSize = 16.sp)
                }
                Spacer(Modifier.padding(12.dp))
                Column {
                    Text(text = "Rooms", fontSize = 14.sp)

                    Spacer(modifier = Modifier.padding(4.dp))

                    LittleNumberInputField(mutableStateOf(" "))

                }
                Spacer(Modifier.padding(16.dp))
                TopAmenitiesInOrder()
                Spacer(Modifier.padding(16.dp))
                Text(text = "+ Add amenities"/*, modifier = Modifier.padding(start = 18.dp)*/)
                Spacer(Modifier.padding(16.dp))
                Text(
                    "Arrival/Departure",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Check in: 14:00\nCheck out: 12:00",
                    fontSize = 20.sp
                )
                Spacer(Modifier.padding(14.dp))
                Text(
                    "GETTING HERE\n" +
                            "Minsk Marriott Hotel\n" +
                            "\n" +
                            "20 Pobediteley Avenue, Minsk, Belarus, 220020\n" +
                            "\n" +
                            "Tel: +375 17-279 30 00"
                )
                Spacer(Modifier.padding(36.dp))
            }
        }

    }

}


@Composable
fun TopAmenitiesInOrder() {
    Row() {
        Column() {
            TopAmenity(R.drawable.ic_wifi, "Wi-fi", true)
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