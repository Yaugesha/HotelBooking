package by.yaugesha.hotelbooking.Main

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.*
import by.yaugesha.hotelbooking.R
import coil.compose.AsyncImage
import com.google.gson.Gson
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun FavoritesScreen(navController: NavController) {
    val user = User(login = "user")
    val vm = MainViewModel()
    var favoritesList = listOf<Room>()
    vm.viewModelScope.launch { favoritesList = getListOfFavorites(vm, user.login) }
    Log.i("List of favorites", favoritesList.toString())

    val bottomItems = listOf(BarItem.Search, BarItem.Favorites, BarItem.Bookings, BarItem.Profile)
    Scaffold(
        bottomBar = { BottomBar(navController, bottomItems) }
    ) {
        SearchHotelParametersBar(navController)
        Column(
            modifier = Modifier
                .padding(top = 120.dp, bottom = 68.dp)
                .verticalScroll(rememberScrollState())
        ) {
            for (i in favoritesList.indices) {
                val hotel = rememberSaveable { mutableStateOf(Hotel()) }
                vm.viewModelScope.launch { hotel.value = setHotelForRoom(vm, favoritesList[i].hotelID) }

                FavouriteHotelCardDescription(navController, vm, favoritesList[i], hotel.value, user.login)

                Spacer(modifier = Modifier.padding(top = 20.dp))
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun FavouriteHotelCardDescription(navController: NavController, vm: MainViewModel, room: Room, hotel: Hotel, login: String) {
    Card(
        shape = (RoundedCornerShape(24.dp)),
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .height(180.dp)
            .width(360.dp)
    ) {
        Box {
            Card(
                modifier = Modifier
                    .wrapContentWidth(Alignment.Start)
                    .height(180.dp)
                    .width(140.dp)
                    .fillMaxHeight()
            ) {
                AsyncImage( //height(513.dp).width(396.dp)
                    model = hotel.photoURI, contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.clip(RoundedCornerShape(24.dp))
                )
            }
            FavoriteButton(vm, login, room.roomId)
        }
        Column(
            modifier = Modifier
                .padding(top = 8.dp, start = 152.dp, bottom = 8.dp, end = 18.dp)
        ) {
            Text(text = "Hotel", fontSize = 20.sp)

            Spacer(modifier = Modifier.padding(top = 12.dp))

            //Here will be street and building and add changing icon
            Row {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_location_on),
                        contentDescription = "location"
                    )

                    Text(
                        text = "${hotel.street} ${hotel.building}, ${hotel.city}, ${hotel.country}",
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.padding(top = 8.dp))

            Card(
                shape = (RoundedCornerShape(24.dp)),
                backgroundColor = ButtonColor,
                modifier = Modifier
                    .height(70.dp)
                    .width(192.dp)
                    .clickable {
                        val formatter = SimpleDateFormat("dd.MM.yyyy")
                        val cal = Calendar.getInstance()
                        cal.time = Date()
                        cal.add(Calendar.DATE, 1)
                        val roomJson = Uri.encode(Gson().toJson(room))
                        val hotelJson = Uri.encode(Gson().toJson(hotel))
                        val searchJson = Uri.encode(Gson().toJson(Search(checkOutDate = cal.time)))
                        navController.navigate(
                            Screen.RoomScreen.route + "/" + roomJson.toString()
                                    + "/" + hotelJson.toString() + "/" + searchJson.toString()
                        )
                    }
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                ) {
                    Row {
                        Text(
                            text = "$",
                            fontSize = 10.sp,
                            color = Color.White,
                            modifier = Modifier.padding(start = 6.dp, top = 3.dp)
                        )
                        Text(text = room.price.toString(), fontSize = 14.sp, color = Color.White)
                        Text(
                            text = "cost per night",
                            fontSize = 14.sp,
                            color = Color.White,
                            modifier = Modifier
                                .padding(start = 8.dp)
                        )
                    }
                    var beds = ""
                    if (room.numberOfDoubleBeds == 0) {
                        beds = "${room.numberOfSingleBeds} single"
                    }
                    if (room.numberOfSingleBeds == 0) {
                        beds = "${room.numberOfDoubleBeds} double"
                    }
                    if (room.numberOfDoubleBeds != 0 && room.numberOfSingleBeds != 0)
                        beds =
                            "${room.numberOfDoubleBeds} double + ${room.numberOfSingleBeds} single"

                    Text(
                        text = "For $beds",
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun FavoriteButton(vm: MainViewModel, login: String, roomId: String/*, isFavorite: Boolean*/) {
    Row(modifier = Modifier
        .wrapContentHeight(Alignment.Top)
        .padding(top = 8.dp, start = 96.dp)
    ) {
        val favouriteVisible = remember { mutableStateOf(false) }
        vm.viewModelScope.launch { favouriteVisible.value = isRoomInFavorites(vm, roomId, login) }

        if (favouriteVisible.value) {
            IconButton(
                onClick = {
                    vm.deleteFavorite(roomId, login)
                    favouriteVisible.value = !favouriteVisible.value
                },
                modifier = Modifier
                    .background(Color.White.copy(0.4f), CircleShape)
                    .height(28.dp)
                    .width(28.dp)
            ) {
                Icon(
                    painterResource(R.drawable.ic_heart_red),
                    contentDescription = "Favorite",
                    tint = Color.Red
                )
            }
        } else {
            IconButton(
                onClick = {
                    vm.setFavorite(roomId, login)
                    favouriteVisible.value = !favouriteVisible.value
                },
                modifier = Modifier
                    .background(Color.White.copy(0.4f), CircleShape)
                    .height(28.dp)
                    .width(28.dp)
            ) {
                Icon(
                    painterResource(R.drawable.ic_favorite),
                    contentDescription = "Favorite"
                )

            }
        }
    }
}

suspend fun getListOfFavorites(vm: MainViewModel, login: String) : List<Room>{
    val result: Deferred<MutableList<Room>>
    runBlocking {
        result = async {vm.loadListOfFavorites(login)}
    }
    return result.await().toList()
}

suspend fun isRoomInFavorites(vm: MainViewModel, roomId: String, login: String): Boolean{
    val result: Deferred<Boolean>
    runBlocking {
        result = async {vm.checkIsRoomInFavorites(roomId, login)}
    }
    return result.await()
}
