package by.yaugesha.hotelbooking.Main

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import by.yaugesha.hotelbooking.Admin.Hotel.HelpList
import by.yaugesha.hotelbooking.Admin.Hotel.HotelSearchField
import by.yaugesha.hotelbooking.Authorization.ui.theme.AdminCardColor
import by.yaugesha.hotelbooking.Authorization.ui.theme.BackgroundColor
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.BarItem
import by.yaugesha.hotelbooking.DataClasses.BottomBar
import by.yaugesha.hotelbooking.DataClasses.Screen
import by.yaugesha.hotelbooking.DataClasses.Search
import by.yaugesha.hotelbooking.R
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "SimpleDateFormat")
@Composable
fun UserSearchScreen(navController: NavController) {
    val context = LocalContext.current
    val hotelImage = (painterResource(id = R.drawable.stokehotel))
    val bottomItems = listOf(BarItem.Search, BarItem.Favorites, BarItem.Bookings, BarItem.Profile)

    val location = rememberSaveable { mutableStateOf("") }
    val arrivalDate = rememberSaveable { mutableStateOf("Date") }
    val departureDate = rememberSaveable { mutableStateOf("Date") }
    val guests = rememberSaveable { mutableStateOf("") }
    val rooms = rememberSaveable { mutableStateOf("") }

    Scaffold(
        bottomBar = { BottomBar(navController, bottomItems) }
    ) {
        Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Image(
                painter = hotelImage,
                contentDescription = "Stoke image",
                modifier = Modifier.wrapContentSize(Alignment.TopCenter)
            )

            Card(
                shape = (RoundedCornerShape(24.dp)),
                backgroundColor = BackgroundColor,
                modifier = Modifier
                    .padding(top = 246.dp)
                    .wrapContentHeight(Alignment.Top)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .height(528.dp)
            ) {

                Button(
                    onClick = {
                        val formatter = SimpleDateFormat("dd.MM.yyyy")
                        val searchData = Search(location = location.value, checkInDate = formatter.parse(arrivalDate.value)!!,
                            checkOutDate = formatter.parse(departureDate.value)!!, guests = guests.value.toInt(), rooms = rooms.value.toInt()
                            )
                        val searchJson = Uri.encode(Gson().toJson(searchData))
                        navController.navigate(Screen.UserSearchResultScreen.route + "/" + searchJson.toString())
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                    shape = (RoundedCornerShape(24.dp)),
                    modifier = Modifier
                        .padding(top = 416.dp, bottom = 68.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .height(44.dp)
                        .width(182.dp)
                )
                {
                    Text(
                        text = "Search",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }
                SearchButton(context, "SearchResultActivity")

                Text(
                    text = "Search Hotel", fontSize = 24.sp,
                    modifier = Modifier.padding(start = 36.dp, top = 30.dp)
                )

                Text(
                    text = "Find hotel as you need with demand.", fontSize = 12.sp,
                    modifier = Modifier.padding(start = 36.dp, top = 64.dp)
                )
            }
            UsersSearchHotelFields(location, arrivalDate, departureDate, guests, rooms)
        }
    }

}


@Composable
fun ShowDatePicker1(context: Context, arrivalDate: MutableState<String>){

    val year: Int
    val month: Int
    val day: Int

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            arrivalDate.value = "$dayOfMonth.${month+1}.$year"
        }, year, month, day
    )
    Row {
        Button(
            onClick = {
                datePickerDialog.show() },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            shape = (RoundedCornerShape(24.dp)),
            modifier = Modifier
                .height(52.dp)
                .width(124.dp)
        ) {
            Text(text = arrivalDate.value)
        }
    }
}

@Composable
fun ShowDatePicker2(context: Context, departureDate: MutableState<String>){

    val year: Int
    val month: Int
    val day: Int

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            departureDate.value = "$dayOfMonth.${month+1}.$year"
        }, year, month, day
    )
    Row {
        Button(
            onClick = {
                datePickerDialog.show()},
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            shape = (RoundedCornerShape(24.dp)),
            modifier = Modifier
                .height(52.dp)
                .width(126.dp)
        ) {
            Text(text = departureDate.value)
        }
    }
}

@Composable
fun SearchButton(context: Context, interaction: String) {
    Button(
        onClick = { context.startActivity(Intent(context, interaction::class.java) ) },
        colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
        shape = (RoundedCornerShape(24.dp)),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .height(44.dp)
            .width(182.dp)
            .padding(top = 490.dp, bottom = 44.dp)
    )
    {
        Text(
            text = "Search",
            fontSize = 16.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }

}

//City search
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchField(text: MutableState<String>) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = text.value, onValueChange = {text.value = it},
        placeholder = { Text(text = "Minsk, Belarus", fontSize = 14.sp, color = Color.Black.copy(0.5f)) },
        shape = (RoundedCornerShape(24.dp)),
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
        modifier = Modifier
            .padding(top = 8.dp)
            .height(52.dp)
            .fillMaxWidth(),
        keyboardActions = KeyboardActions(onDone = {keyboardController?.hide()})
    )
}

//Arrival + Departure search
@Composable
fun LittleNumberInputField(value: MutableState<String>) {

    OutlinedTextField(
        value.value, { value.value = it },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = (RoundedCornerShape(24.dp)),
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
        modifier = Modifier
            .height(52.dp)
            .width(124.dp)
    )
}

@Composable
fun UsersSearchHotelFields(
    location: MutableState<String>, arrivalDate: MutableState<String>,
    departureDate: MutableState<String>, guests: MutableState<String>, rooms: MutableState<String>
) {
    val context = LocalContext.current
    val showSearchLocationHelp = rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.padding(start = 36.dp, end = 36.dp, top = 350.dp)) {
        Text(text = "City", fontSize = 20.sp,)

        Spacer(modifier = Modifier.padding(top = 8.dp))

        Column {
            HotelSearchField(location, showSearchLocationHelp)
            if(showSearchLocationHelp.value)
                HelpList(state = location, showSearchHelp = showSearchLocationHelp)
        }

        Spacer(modifier = Modifier.padding(top = 20.dp))

        Row {
            Column {
                Text(text = "Arrival", fontSize = 14.sp)

                Spacer(modifier = Modifier.padding(4.dp))

                ShowDatePicker1(context, arrivalDate)
            }

            Spacer(modifier = Modifier.padding(start = 68.dp))

            Column {
                Text(text = "Departure", fontSize = 14.sp)

                Spacer(modifier = Modifier.padding(4.dp))

                ShowDatePicker2(context, departureDate)

            }
        }
        //if date incorrect...

        Spacer(modifier = Modifier.padding(top = 20.dp))

        Row {
            Column {
                Text(text = "Guests", fontSize = 14.sp)

                Spacer(modifier = Modifier.padding(4.dp))

                LittleNumberInputField(guests)
            }

            Spacer(modifier = Modifier.padding(start = 68.dp))

            Column {
                Text(text = "Rooms", fontSize = 14.sp)

                Spacer(modifier = Modifier.padding(4.dp))

                LittleNumberInputField(rooms)

            }
        }
    }
}
