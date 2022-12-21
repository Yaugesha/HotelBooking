package by.yaugesha.hotelbooking.Admin.Hotel

import android.annotation.SuppressLint
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
import by.yaugesha.hotelbooking.Authorization.ui.theme.AdminCardColor
import by.yaugesha.hotelbooking.Authorization.ui.theme.BackgroundColor
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.Hotel
import by.yaugesha.hotelbooking.DataClasses.Screen
import by.yaugesha.hotelbooking.DataClasses.Search
import by.yaugesha.hotelbooking.Main.SearchHotelParametersBar
import coil.compose.AsyncImage
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun HotelCardDescriptionForAdmin(navController: NavController, hotel: Hotel) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 18.dp, end = 12.dp)
    ) {

        Spacer(modifier = Modifier.padding(top = 4.dp))

        Text(text = hotel.name, modifier = Modifier.fillMaxWidth(1f), fontSize = 22.sp, color = Color.White,
            textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.padding(top = 2.dp))

        Text(
            text = hotel.building + " " + hotel.street + ", " + hotel.city + ", " + hotel.country
                    + ", " + hotel.postCode + ",\n" + "Tel: ${hotel.phone}",
            fontSize = 14.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.padding(8.dp))

        Button(
            onClick = { navController.navigate(Screen.HotelScreen.route + "/" + hotel.hotelId) },
            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
            shape = (RoundedCornerShape(16.dp)),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text  = "View",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,)
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HotelSearchResultScreen(navController: NavController, searchParameter: String, show: Int) {
    val vm = AdminViewModel()
    var hotelList: MutableList<Hotel> = mutableListOf()
    when(show) {
        1 -> vm.viewModelScope.launch { hotelList = searchHotelByLocation(vm, searchParameter).toMutableList() }
        2 -> vm.viewModelScope.launch { hotelList = searchHotelByItName(vm, searchParameter).toMutableList() }
        3 -> vm.viewModelScope.launch { hotelList = searchHotelByLocationAndName(vm, searchParameter).toMutableList() }
        4 -> vm.viewModelScope.launch { hotelList = setHotelList(vm).toMutableList() }
    }
    if (hotelList.isEmpty()) {
        Row(modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight(Alignment.CenterVertically)) {
            Text(
                text = "No hotels found", fontSize = 40.sp,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                    /*.wrapContentWidth(Alignment.CenterHorizontally)*/
                    .wrapContentHeight(Alignment.CenterVertically)
            )
        }
    } else {
        val context = LocalContext.current
        Column( modifier = Modifier
            .background(BackgroundColor)
            .fillMaxHeight()) {
           // SearchHotelParametersBar(navController, Search())
            Button(
                onClick = {
                    navController.navigate(Screen.AddHotelScreen.route)
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
                shape = (RoundedCornerShape(16.dp)),
                modifier = Modifier
                    .fillMaxWidth()
                    //.padding(top = 111.dp)
                    .padding(top = 18.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .height(44.dp)
                    .width(358.dp)
            )
            {
                Text(
                    text = "Add new hotel",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.padding(top = 2.dp))
            Column(modifier = Modifier
                //.padding(top = 164.dp)
                .verticalScroll(rememberScrollState())
            ) {

                Spacer(modifier = Modifier.padding(top = 18.dp))
                for (i in hotelList.indices) {
                    Card(
                        shape = (RoundedCornerShape(32.dp)),
                        backgroundColor = AdminCardColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .height(182.dp)
                            .width(358.dp)
                    ) {
                        Row{
                            Card(backgroundColor = AdminCardColor,
                                modifier = Modifier
                                    .wrapContentWidth(Alignment.Start)
                                    .width(158.dp)
                                    .fillMaxHeight()){
                                AsyncImage( //height(513.dp).width(396.dp)
                                    model = hotelList[i].photoURI, contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.clip(RoundedCornerShape(32.dp))
                                )
                            }
                            Spacer(modifier = Modifier.padding(start = 4.dp))
                            HotelCardDescriptionForAdmin(navController, hotelList[i])
                        }

                    }
                    Spacer(modifier = Modifier.padding(top = 20.dp))
                }
            }
        }
    }
}

suspend fun setHotelList(vm: AdminViewModel): List<Hotel> {
    val result: Deferred<List<Hotel>>
    runBlocking {
        result = async { vm.getHotels() }
    }
    return result.await()
}

suspend fun searchHotelByLocation(vm: AdminViewModel, location: String): List<Hotel> {
    val result: Deferred<List<Hotel>>
    runBlocking {
        result = async { vm.searchHotelsByLocation(location) }
    }
    return result.await()
}

suspend fun searchHotelByItName(vm: AdminViewModel, hotelName: String): List<Hotel> {
    val result: Deferred<List<Hotel>>
    runBlocking {
        result = async { vm.searchHotelByItName(hotelName)!! }
    }
    return result.await()
}

suspend fun searchHotelByLocationAndName(vm: AdminViewModel, searchParameter: String): List<Hotel> {
    val result: Deferred<List<Hotel>>
    runBlocking {
        result = async { vm.searchHotelsByLocationAndName(searchParameter) }
    }
    return result.await()
}