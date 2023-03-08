package by.yaugesha.hotelbooking.Main

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Admin.AdminViewModel
import by.yaugesha.hotelbooking.Admin.Hotel.HelpList
import by.yaugesha.hotelbooking.Admin.Hotel.HelpListItem
import by.yaugesha.hotelbooking.Admin.Hotel.setListOfLocations
import by.yaugesha.hotelbooking.Admin.Hotel.setListOfNames
import by.yaugesha.hotelbooking.Admin.LoadingAnimation
import by.yaugesha.hotelbooking.Authorization.ui.theme.AdminCardColor
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.*
import by.yaugesha.hotelbooking.Main.Screens.swapList
import by.yaugesha.hotelbooking.R
import coil.compose.AsyncImage
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.lang.Math.random
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition",
    "SuspiciousIndentation"
)
@Composable
fun FavoritesScreen(navController: NavController) {
    val vm = MainViewModel()
    val context = LocalContext.current
    val login = remember {vm.getLogin(context)!!}
    val favoritesList = remember { mutableStateListOf<Favorites>() }
    val allFavorites = mutableListOf<Favorites>()
    var allFavoritesRoom = listOf<Room>()
    val allFavoritesHotels = mutableListOf<Hotel>()
    vm.viewModelScope.launch {
        allFavoritesRoom = getListOfFavorites(vm, login)
        allFavoritesRoom.forEach {allFavoritesHotels.add(setHotelForRoom(vm, it.hotelID))  }
        for(i in allFavoritesRoom.indices) {
            allFavorites.add(Favorites(room = allFavoritesRoom[i], hotel = allFavoritesHotels[i]) )
        }
        favoritesList.swapList(allFavorites)
        delay(1000)
    }
    Log.i("List of favorites", favoritesList.toString())
    val bottomItems = listOf(BarItem.Search, BarItem.Favorites, BarItem.Bookings, BarItem.Profile)
    val input = remember { mutableStateOf(TextFieldValue("")) }
    val showSearchHelp = rememberSaveable { mutableStateOf(false) }

    Scaffold(
            bottomBar = { BottomBar(navController, bottomItems) }
    ) {
        Column {
            if (favoritesList.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoadingAnimation()
                }
            } else {
                SearchView(state = input, showSearchHelp)
                FavoritesList(
                    navController = navController, state = input, vm = vm,
                    favoritesList = favoritesList
                )
            }
        }
    }
}

@Composable
fun SearchView(state: MutableState<TextFieldValue>, showSearchHelp: MutableState<Boolean>) {
    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
            showSearchHelp.value = true
        },
        modifier = Modifier
            .fillMaxWidth(),
        textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
        placeholder = { Text(text = "Hotel City, Country", fontSize = 14.sp, color = Color.White.copy(0.5f)) },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "",
                modifier = Modifier
                    .padding(15.dp)
                    .size(24.dp)
            )
        },
        trailingIcon = {
            if (state.value != TextFieldValue("")) {
                IconButton(
                    onClick = {
                        state.value =
                            TextFieldValue("") // Remove text from TextField when you press the 'X' icon
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(15.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RectangleShape, // The TextFiled has rounded corners top left and right by default
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            cursorColor = Color.White,
            leadingIconColor = Color.White,
            trailingIconColor = Color.White,
            backgroundColor = AdminCardColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun FavoritesList(navController: NavController, vm: MainViewModel, favoritesList: SnapshotStateList<Favorites>,
                  state: MutableState<TextFieldValue>
) {
    var filteredFavourites: List<Favorites>
    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 64.dp)
    ) {
        val searchedText = state.value.text
        filteredFavourites = if (searchedText.isEmpty()) {
            favoritesList.toList()
        } else {
            val resultList = ArrayList<Favorites>()
            for (fav in favoritesList) {
                if ("${fav.hotel.name} ${fav.hotel.city}, ${fav.hotel.country}".lowercase(Locale.getDefault())
                        .contains(searchedText.lowercase(Locale.getDefault()))
                ) {
                    resultList.add(fav)
                }
            }
            resultList
        }
        items(filteredFavourites) { favourite ->
            FavouriteHotelCardDescription(
                navController = navController,
                vm = vm,
                room = favourite.room,
                hotel = favourite.hotel,
                login = "user"
            )
            Spacer(Modifier.padding(top = 12.dp))
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
            Text(text = hotel.name, fontSize = 20.sp)

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
    Row(
        modifier = Modifier
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

suspend fun getListOfFavorites(vm: MainViewModel, login: String) : List<Room> {
    val result: Deferred<MutableList<Room>> =
        vm.viewModelScope.async { vm.loadListOfFavorites(login) }
    return result.await().toList()
}

suspend fun isRoomInFavorites(vm: MainViewModel, roomId: String, login: String): Boolean {
    val result = vm.viewModelScope.async { vm.checkIsRoomInFavorites(roomId, login) }
    return result.await()
}
