package by.yaugesha.hotelbooking.Main

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Admin.LoadingAnimation
import by.yaugesha.hotelbooking.Authorization.ui.theme.BackgroundColor
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.*
import by.yaugesha.hotelbooking.Main.Screens.swapList
import by.yaugesha.hotelbooking.R
import coil.compose.AsyncImage
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlin.math.abs

@SuppressLint("CoroutineCreationDuringComposition", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UserSearchResultScreen(navController: NavController, searchData: Search) {

    BackPressHandler(onBackPressed = { navController.navigate(Screen.UserSearchScreen.route) })

    Log.i("Sorts", searchData.sorts.toString())
    val vm = MainViewModel()
    val context = LocalContext.current
    val login = remember {vm.getLogin(context)!!}
    val roomsList = remember { mutableStateListOf<Room>() }
    var allRooms = listOf<Room>()
    vm.viewModelScope.launch {
        allRooms = setRoomList(vm, searchData)
        delay(1000)
        roomsList.swapList(allRooms)
    }
    Scaffold() {
        Column(modifier = Modifier.background(BackgroundColor)) {
            if (roomsList.isEmpty()) {
                /*                Row(modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(Alignment.CenterVertically)) {
                Text(
                    text = "No rooms found", fontSize = 40.sp,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .wrapContentHeight(Alignment.CenterVertically)
                )
            }*/
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoadingAnimation()
                }
            } else {
                SearchHotelParametersBar(navController, searchData, roomsList, allRooms)
                Spacer(modifier = Modifier.padding(top = 36.dp))

                Column(
                    modifier = Modifier
                        .background(BackgroundColor)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    for (i in roomsList.indices) {
                        val hotel = rememberSaveable { mutableStateOf(Hotel()) }
                        vm.viewModelScope.launch {
                            hotel.value = setHotelForRoom(vm, roomsList[i].hotelID)
                        }
                        Log.i("Hotel", hotel.toString())

                        HotelCardDescriptionForUser(
                            navController,
                            vm,
                            searchData,
                            roomsList[i],
                            hotel.value,
                            login
                        )

                        Spacer(modifier = Modifier.padding(top = 20.dp))
                    }

                }
            }
        }
    }
}

@Composable
fun SearchHotelParametersBar(
    navController: NavController, searchData: Search,
    roomList: SnapshotStateList<Room>, allRooms: List<Room>
) {
    Card(
        shape = (RoundedCornerShape(24.dp)),
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
            .width(360.dp)
            .height(60.dp)
    ) {
        Card(
            shape = (RoundedCornerShape(24.dp)),
            elevation = 0.dp,
            border = BorderStroke(0.dp, Color.White),
            modifier = Modifier
                .padding(start = 52.dp, end = 238.dp)
                .clickable {
                    val searchJson = Uri.encode(Gson().toJson(searchData))
                    navController.navigate(Screen.SortScreen.route + "/" + searchJson.toString() + "/" + "true")
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_tune),
                contentDescription = "Filters",
                modifier = Modifier
                    .wrapContentWidth(Alignment.Start)
                    .wrapContentHeight(Alignment.CenterVertically)
            )
            Text(
            text = "Filters", fontSize = 14.sp,
            modifier = Modifier
                .wrapContentWidth(Alignment.End)
                .wrapContentHeight(Alignment.CenterVertically)
            )
        }

        val openSortDialog = remember { mutableStateOf(false) }
        Card(
            shape = (RoundedCornerShape(24.dp)),
            elevation = 0.dp,
            border = BorderStroke(0.dp, Color.White),
            modifier = Modifier
                .padding(start = 250.dp, end = 52.dp)
                .clickable {
                    openSortDialog.value = true
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_sort),
                contentDescription = "Sort",
                modifier = Modifier
                    .wrapContentWidth(Alignment.Start)
                    .wrapContentHeight(Alignment.CenterVertically)
            )
            Text(
            text = "Sort", fontSize = 14.sp,
            modifier = Modifier
                .wrapContentWidth(Alignment.End)
                .wrapContentHeight(Alignment.CenterVertically)
            )
        }
        if(openSortDialog.value) {
            AlertDialog(
                onDismissRequest = { openSortDialog.value = false },
                title = { Text(text = "Sort by") },
                shape = RoundedCornerShape(24.dp),
                backgroundColor = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.width(180.dp),
                buttons = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    ) {
                        SortDialogButton(openSortDialog, "Price min", roomList, allRooms)
                        SortDialogButton(openSortDialog, "Price max", roomList, allRooms)
                        SortDialogButton(openSortDialog, "Square max", roomList, allRooms)
                        SortDialogButton(openSortDialog, "Square min", roomList, allRooms)
                        //SortDialogButton(openSortDialog, "Amenities")
                    }
                }
            )
        }
    }
}

@Composable
fun SortDialogButton(openSortDialog: MutableState<Boolean>, text: String, roomList: SnapshotStateList<Room>, allRooms: List<Room>) {
    val vm = MainViewModel()
    Button(
        onClick = {
            vm.sortRooms(roomList, text)
            roomList.swapList(allRooms)
            roomList.swapList(vm.sortRooms(roomList.toMutableList(), text))
            Log.i("roomList", roomList.toMutableList().toString())
            openSortDialog.value = false
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text, fontSize = 16.sp)
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HotelCardDescriptionForUser(
    navController: NavController, vm: MainViewModel, searchData: Search, room: Room, hotel: Hotel, login: String
) {
    val nights = abs(searchData.checkOutDate.time - searchData.checkInDate.time) / (1000 * 60 * 60 * 24)

    Card(
        shape = (RoundedCornerShape(24.dp)),
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .height(160.dp)
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
            val isFavorite = remember { mutableStateOf(false) }
            vm.viewModelScope.launch {
                isFavorite.value = isRoomInFavorites(vm, room.roomId, login)
            }
            FavoriteButton(vm, login, room.roomId/*, isFavorite.value*/)
        }
        Column(
            modifier = Modifier
                .padding(top = 8.dp, start = 152.dp, bottom = 8.dp, end = 18.dp)
        ) {
            Text(text = hotel.name, fontSize = 20.sp)

            Spacer(modifier = Modifier.padding(top = 8.dp))

            Row {
                Icon(
                    painter = painterResource(id = R.drawable.ic_location_on),
                    contentDescription = "location"
                )

                Text(text = "${hotel.street} ${hotel.building}", fontSize = 14.sp)

                Spacer(modifier = Modifier.padding(8.dp))
            }

            Spacer(modifier = Modifier.padding(top = 8.dp))

            Card(
                shape = (RoundedCornerShape(24.dp)),
                backgroundColor = ButtonColor,
                modifier = Modifier
                    .height(70.dp)
                    .width(192.dp)
                    .clickable {
                        val roomJson = Uri.encode(Gson().toJson(room))
                        val hotelJson = Uri.encode(Gson().toJson(hotel))
                        val searchJson = Uri.encode(Gson().toJson(searchData))
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
                        Spacer(modifier = Modifier.padding(start = 1.dp))
                        Text(
                            text = "${room.price * nights}",
                            fontSize = 14.sp,
                            color = Color.White,
                        )
                    }
                    Spacer(modifier = Modifier.padding(top = 2.dp))

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
                        text = "For $nights nights\n$beds",
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



suspend fun setRoomList(vm: MainViewModel, searchData: Search): List<Room> {
    val result: Deferred<List<Room>> = vm.viewModelScope.async { vm.getRooms(searchData) }
    return result.await()
}

suspend fun setHotelForRoom(vm: MainViewModel, hotelId: String): Hotel {
    /*val result: Deferred<Hotel>
    runBlocking {
        result = async { vm.getHotelDataForRoom(hotelId) }
    }*/
    val result: Deferred<Hotel> =vm.viewModelScope.async { vm.getHotelDataForRoom(hotelId) }
    return result.await()
}