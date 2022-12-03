package by.yaugesha.hotelbooking.Main.Screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.Admin.Hotel.Add.BedNumberInputField
import by.yaugesha.hotelbooking.Main.LittleNumberInputField

@SuppressLint("UnrememberedMutableState")
@Composable
fun SortScreen(navController: NavController, isBook: Boolean = true) {
    Log.i("got", isBook.toString())
    val guests = 3
    val numberOfDoubleBeds = remember { mutableStateOf(0) }
    val numberOfSingleBeds = remember { mutableStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 12.dp, end = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Price", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.padding(8.dp))
        //Divider(color = Color.Black, modifier = Modifier.fillMaxWidth())
        Row {
            Column {
                Text(text = "From", fontSize = 14.sp)

                Spacer(modifier = Modifier.padding(4.dp))

                LittleNumberInputField(mutableStateOf(" "))
            }

            Spacer(modifier = Modifier.padding(start = 68.dp))

            Column {
                Text(text = "To", fontSize = 14.sp)

                Spacer(modifier = Modifier.padding(4.dp))

                LittleNumberInputField(mutableStateOf(" "))

            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Divider(color = Color.Black, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.padding(8.dp))

        Text(text = "Amenities", fontSize = 16.sp)
        Column{
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SortAmenities("Wi-fi in room")
                SortAmenities("Bathroom")
                SortAmenities("Wi-fi in lobby")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SortAmenities("Departure from airport")
                SortAmenities("Casino")
                SortAmenities("Spa")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SortAmenities("Kitchen")
                SortAmenities("Wheelchair accessible")
                SortAmenities("Gym")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SortAmenities("Swimming pool")
                SortAmenities("Pet friendly")
                SortAmenities("Restaurant")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SortAmenities("No smoking")
                SortAmenities("AC unit")
                SortAmenities("Balcony")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SortAmenities("Free parking")
                SortAmenities("Hotel bar")
                SortAmenities("Noise isolation")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SortAmenities("Business services")
                SortAmenities("TV in room")
                SortAmenities("Laundry")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SortAmenities("Cribs")
                SortAmenities("Outdoor space")
                SortAmenities("Breakfast included")
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))
        Divider(color = Color.Black, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.padding(8.dp))
        if(isBook == true) {
            Text(text = "Beds", fontSize = 16.sp)
            Spacer(modifier = Modifier.padding(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                BedNumberInputField(numberOfDoubleBeds, 2)
                Spacer(modifier = Modifier.padding(start = 42.dp))
                BedNumberInputField(numberOfSingleBeds, 1)
            }
            if (
                (numberOfDoubleBeds.value * 2 + numberOfSingleBeds.value) < guests &&
                (numberOfDoubleBeds.value != 0 && numberOfSingleBeds.value != 0)
            )
                Text(
                    text = "Number of beds is les then number of guests",
                    fontSize = 12.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 4.dp, start = 30.dp)
                )
        }
        else{
            Text(text = "Status", fontSize = 16.sp)
            Spacer(modifier = Modifier.padding(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SortAmenities("Booked")
                SortAmenities("Old")
                SortAmenities("Canceled")
            }
        }
        Spacer(modifier = Modifier.padding(12.dp))

        Button(
            onClick = {  },
            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
            shape = (RoundedCornerShape(16.dp)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(start = 12.dp, end = 12.dp)
        ) {
            Text(text = "Apply", fontSize = 20.sp, color = Color.White)
        }
        Spacer(modifier = Modifier.padding(12.dp))
        /*Button(
            onClick = {  },
            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
            shape = (RoundedCornerShape(16.dp)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(start = 12.dp, end = 12.dp)
        ) {
            Text(text = "Clear", fontSize = 20.sp, color = Color.White)
        }*/
        Spacer(modifier = Modifier.padding(24.dp))

    }
}

@Composable
fun SortAmenities(text: String) {
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