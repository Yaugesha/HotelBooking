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
    val showSearchLocationHelp = rememberSaveable { mutableStateOf(false) }
    val showSearchHotelHelp = rememberSaveable { mutableStateOf(false) }
    val bottomItems =
        listOf(BarItem.Users, BarItem.Hotels, BarItem.UsersBookings, BarItem.AdminProfile)
    Scaffold(
        bottomBar = { BottomBar(navController, bottomItems) }
    ) {
        val hotelImage = (painterResource(id = R.drawable.stokehotel))
        val location = rememberSaveable { mutableStateOf("") }
        val hotelName = rememberSaveable { mutableStateOf("") }
        val parameter = rememberSaveable { mutableStateOf(location.value) }
        val searchConf = rememberSaveable { mutableStateOf(1) }
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
                        .padding(bottom = 52.dp, start = 36.dp, end = 36.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.padding(top = 36.dp))

                    Text(text = "Search Hotel", fontSize = 24.sp)

                    Spacer(modifier = Modifier.padding(top = 12.dp))

                    Text(text = "City", fontSize = 20.sp)

                    Spacer(modifier = Modifier.padding(top = 8.dp))

                    Column {
                        HotelSearchField(location, showSearchLocationHelp)
                        if(showSearchLocationHelp.value)
                            HelpList(state = location, showSearchHelp = showSearchLocationHelp)
                    }


                    Spacer(modifier = Modifier.padding(top = 16.dp))

                    Text(text = "Hotel", fontSize = 20.sp)

                    Spacer(modifier = Modifier.padding(top = 8.dp))

                    Column {
                        HotelSearchField(hotelName, showSearchHotelHelp)
                        if (showSearchHotelHelp.value)
                            HelpList(state = hotelName, showSearchHelp = showSearchHotelHelp, field = "names")
                    }

                    Spacer(modifier = Modifier.padding(top = 24.dp))

                    if(location.value != "" && hotelName.value != "") {
                        parameter.value = hotelName.value + ", " + location.value
                        searchConf.value = 3
                    }
                    if(location.value == ""){
                        parameter.value = hotelName.value
                        searchConf.value = 2
                    }
                    if(hotelName.value == ""){
                        parameter.value = location.value
                        searchConf.value = 1
                    }
                    //Edit
                    Button(
                        onClick = {
                            Log.i("config", "Got:  ${searchConf.value}")
                            navController.navigate(Screen.HotelSearchResultScreen.route + "/" + parameter.value + "/" + searchConf.value)
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
                            navController.navigate(Screen.HotelSearchResultScreen.route + "/" + "no location" + "/" + 4)
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

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HelpList(state: MutableState<String>, showSearchHelp: MutableState<Boolean>, field: String = "location" ) {
    val vm = AdminViewModel()
    var items = mutableListOf<String>()  //=getListOfCountries()
    if (field == "location")
        vm.viewModelScope.launch {items = setListOfLocations(vm).toMutableList() }
    else
        vm.viewModelScope.launch {items = setListOfNames(vm).toMutableList() }
    var filteredItems = mutableListOf<String>()
    val cardSize = rememberSaveable {mutableStateOf(100)}
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(cardSize.value.dp)
        ) {
        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .height(cardSize.value.dp)) {
            val searchedText = state.value
            filteredItems = if (searchedText.isEmpty()) {
                items
            } else {
                val resultList = mutableListOf<String>()
                for (i in items) {
                    if (i.lowercase(Locale.getDefault())
                            .contains(searchedText.lowercase(Locale.getDefault()))
                    ) {
                        resultList.add(i)
                    }
                }
                resultList
            }

            items(filteredItems) { filteredItem ->
                HelpListItem(
                    countryText = filteredItem,
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
fun HelpListItem(countryText: String, onItemClick: (String) -> Unit) {
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

suspend fun setListOfNames(vm: AdminViewModel): List<String> {
    val result: Deferred<List<String>>
    runBlocking {
        result = async { vm.getListOfHotelNames() }
    }
    return result.await()
}