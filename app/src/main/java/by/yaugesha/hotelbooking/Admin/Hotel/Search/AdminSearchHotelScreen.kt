package by.yaugesha.hotelbooking.Admin.Hotel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Admin.AdminViewModel
import by.yaugesha.hotelbooking.Authorization.ui.theme.BackgroundColor
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.BarItem
import by.yaugesha.hotelbooking.DataClasses.BottomBar
import by.yaugesha.hotelbooking.DataClasses.Hotel
import by.yaugesha.hotelbooking.DataClasses.Screen
import by.yaugesha.hotelbooking.Main.SearchField
import by.yaugesha.hotelbooking.R
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition",
    "SuspiciousIndentation"
)
@Composable
fun AdminSearchHotelScreen(navController: NavController) {
    val vm = AdminViewModel()
    val showSearchHelp = rememberSaveable { mutableStateOf(false) }
    val bottomItems =
        listOf(BarItem.Users, BarItem.Hotels, BarItem.UsersBookings, BarItem.AdminProfile)
    Scaffold(
        bottomBar = { BottomBar(navController, bottomItems) }
    ) {
        val hotelImage = (painterResource(id = R.drawable.stokehotel))
        val location = rememberSaveable { mutableStateOf("") }
        val hotelName = rememberSaveable { mutableStateOf("") }
        Box(modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            //.background(BackgroundColor)
        ) {

            Image(
                painter = hotelImage,
                contentDescription = "Stoke image",
                modifier = Modifier.wrapContentSize(Alignment.TopCenter)
            )

            Card(
                shape = (RoundedCornerShape(24.dp)),
                backgroundColor = BackgroundColor,
                modifier = Modifier
                    .padding(top = 272.dp)
                    .wrapContentHeight(Alignment.Top)
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {

                Column(
                    modifier = Modifier
                        .padding(/*top = 284.dp, */start = 36.dp, end = 36.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.padding(top = 36.dp))

                    Text(text = "Search Hotel", fontSize = 24.sp)

                    Spacer(modifier = Modifier.padding(top = 12.dp))

                    Text(text = "City", fontSize = 20.sp)

                    Spacer(modifier = Modifier.padding(top = 8.dp))

                    Column {
                        HotelSearchField(location, showSearchHelp)
                    if(showSearchHelp.value)
                        CountryList(state = location, showSearchHelp = showSearchHelp)
                    }


                    Spacer(modifier = Modifier.padding(top = 16.dp))

                    Text(text = "Hotel", fontSize = 20.sp)

                    Spacer(modifier = Modifier.padding(top = 8.dp))

                    SearchField(hotelName)

                    Spacer(modifier = Modifier.padding(top = 24.dp))


                    //Edit
                    Button(
                        onClick = {
                            navController.navigate(Screen.HotelSearchResultScreen.route)
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                        shape = (RoundedCornerShape(24.dp)),
                        modifier = Modifier
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

                    Spacer(modifier = Modifier.padding(top = 52.dp))

                    Button(
                        onClick = {
                            navController.navigate(Screen.HotelSearchResultScreen.route)
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                        shape = (RoundedCornerShape(24.dp)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .height(44.dp)
                            .width(182.dp)
                    )
                    {
                        Text(
                            text = "Show all hotels",
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }

                    Spacer(modifier = Modifier.padding(top = 44.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HotelSearchField(text: MutableState<String>, showSearchHelp: MutableState<Boolean>) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = text.value, onValueChange = {text.value = it; showSearchHelp.value = true},
        placeholder = { Text(text = "Minsk, Belarus", fontSize = 14.sp, color = Color.Black) },
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

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CountryList(state: MutableState<String>, showSearchHelp: MutableState<Boolean>) {
    val vm = AdminViewModel()
    var cities = mutableListOf<String>()  //=getListOfCountries()
    vm.viewModelScope.launch {cities = setListOfLocations(vm).toMutableList() }
    var filteredCountries: MutableList<String>
    var cardSize = 100
    if(cities.size == 1)
        cardSize = 52
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(cardSize.dp)
        ) {
        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .height(cardSize.dp)) {
            val searchedText = state.value
            filteredCountries = if (searchedText.isEmpty()) {
                cities
            } else {
                val resultList = mutableListOf<String>()
                for (country in cities) {
                    if (country.lowercase(Locale.getDefault())
                            .contains(searchedText.lowercase(Locale.getDefault()))
                    ) {
                        resultList.add(country)
                    }
                }
                resultList
            }

            items(filteredCountries) { filteredCountry ->
                CountryListItem(
                    countryText = filteredCountry,
                    onItemClick = { selectedCountry ->
                        state.value = selectedCountry
                        showSearchHelp.value = false
                    }
                )
            }
        }
    }
}

@Composable
fun CountryListItem(countryText: String, onItemClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = { onItemClick(countryText) })
            .background(Color.White)
            .height(48.dp)
            .fillMaxWidth()
            .padding(PaddingValues(8.dp, 16.dp))
    ) {
        Text(text = countryText, fontSize = 14.sp, color = Color.Black)
    }
}

suspend fun setListOfLocations(vm: AdminViewModel): List<String> {
    val result: Deferred<List<String>>
    runBlocking {
        result = async { vm.getListOfCities() }
    }
    return result.await()
}
