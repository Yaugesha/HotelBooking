package by.yaugesha.hotelbooking.Main

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.BarItem
import by.yaugesha.hotelbooking.DataClasses.BottomBar
import by.yaugesha.hotelbooking.DataClasses.Screen
import by.yaugesha.hotelbooking.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FavoritesScreen(navController: NavController) {
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
            for (i in 1..3) {
                Card(
                    shape = (RoundedCornerShape(24.dp)),
                    backgroundColor = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .height(180.dp)
                        .width(360.dp)
                ) {
                    Box{
                        Image(
                            painter = painterResource(id = R.drawable.mariott),
                            contentDescription = "example of hotel img 396x513",
                            modifier = Modifier
                                .wrapContentWidth(Alignment.Start)
                        )
                        FavoriteButton()
                    }

                    FavouriteHotelCardDescription(navController)
                }
                Spacer(modifier = Modifier.padding(top = 20.dp))
            }
        }
    }
}

@Composable
fun FavouriteHotelCardDescription(navController: NavController) {
    Column(
        modifier = Modifier
            .padding(top = 8.dp, start = 152.dp, bottom = 8.dp, end = 18.dp)
    ) {
        Text(text = "Marriott Hotel", fontSize = 20.sp)

        Spacer(modifier = Modifier.padding(top = 12.dp))

        //Here will be street and building and add changing icon
        Row {
            Row {

                Icon(
                    painter = painterResource(id = R.drawable.ic_location_on),
                    contentDescription = "location"
                )

                Text(text = "Pobediteley 9, Minsk Belarus", fontSize = 14.sp)
            }
           /* Row {
                val favouriteVisible = rememberSaveable { mutableStateOf(false) }

                if (favouriteVisible.value) {
                    IconButton(
                        onClick = { favouriteVisible.value = !favouriteVisible.value },
                        modifier = Modifier
                            .height(24.dp)
                            .width(24.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_heart_red),
                            contentDescription = "Favorite",
                            tint = Color.Red
                        )
                    }
                } else
                    IconButton(
                        onClick = { favouriteVisible.value = !favouriteVisible.value },
                        modifier = Modifier
                            .height(24.dp)
                            .width(24.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_favorite),
                            contentDescription = "Favorite"
                        )

                    }
            }*/
        }
        Spacer(modifier = Modifier.padding(top = 8.dp))

        Card(
            shape = (RoundedCornerShape(24.dp)),
            backgroundColor = ButtonColor,
            modifier = Modifier
                .height(70.dp)
                .width(192.dp)
                .clickable { navController.navigate(Screen.RoomScreen.route) }
        ) {
            Column(modifier = Modifier
                .padding(start = 8.dp)
                .wrapContentHeight(Alignment.CenterVertically)) {
                Row {
                    Text(
                        text = "$",
                        fontSize = 10.sp,
                        color = Color.White,
                        modifier = Modifier.padding(start = 6.dp, top = 3.dp)
                    )
                    Text(text = "60", fontSize = 14.sp, color = Color.White,)
                    Text(
                        text = "cost per night",
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(start = 8.dp)
                    )
                }
                Text(
                    text = "For 2 double + 1 single beds",
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun FavoriteButton() {
    Row(modifier = Modifier
        .wrapContentHeight(Alignment.Top)
        .padding(top = 8.dp, start = 96.dp)
    ) {
        val favouriteVisible = rememberSaveable { mutableStateOf(false) }

        if (favouriteVisible.value) {
            IconButton(
                onClick = { favouriteVisible.value = !favouriteVisible.value },
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