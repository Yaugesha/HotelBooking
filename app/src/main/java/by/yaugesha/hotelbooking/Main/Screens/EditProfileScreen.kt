package by.yaugesha.hotelbooking.Main.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Admin.Hotel.Add.TextField
import by.yaugesha.hotelbooking.Athorisation.Screens.PasswordField
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.Main.MainViewModel

@Composable
fun EditProfileScreen(navController: NavController) {
    val vm = MainViewModel()
    Column(
        modifier = Modifier
            .padding(start = 45.dp, end = 45.dp, top = 34.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val login = rememberSaveable { mutableStateOf ("Login") }
        val email = rememberSaveable { mutableStateOf ("Email") }
        val name = rememberSaveable { mutableStateOf ("Name") }
        val surname = rememberSaveable { mutableStateOf ("Surname") }

        TextField(string = login, field = "Login")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        TextField(string = email, field = "Email")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        TextField(string = name, field = "Name")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        TextField(string = surname, field = "Surname")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        Button(
            onClick =
            {
               /* vm.setHotel(
                    hotelName.value, country.value, city.value, street.value, building.value,
                    postCode.value, phoneNumber.value, checkIn.value, checkOut.value, bitmap.value!!
                )
                navController.navigate(Screen.AddRoomScreen.route + "/" + vm.getHotelId())*/
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
            shape = (RoundedCornerShape(16.dp)),
            /*enabled = hotelName.value.isNotEmpty() && country.value.isNotEmpty() &&
                    city.value.isNotEmpty() && street.value.isNotEmpty() &&
                    building.value.isNotEmpty() && postCode.value.isNotEmpty() &&
                    phoneNumber.value.isNotEmpty() && checkIn.value.isNotEmpty() &&
                    checkOut.value.isNotEmpty() && bitmap.value != null,*/
            modifier = Modifier.fillMaxWidth().width(44.dp)
        ) {
            Text(
                text = "Edit",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.padding(top = 68.dp))

        val currentPassword = rememberSaveable { mutableStateOf ("") }
        val newPassword1 = rememberSaveable { mutableStateOf ("") }
        val newPassword2 = rememberSaveable { mutableStateOf ("") }

        currentPassword.value = PasswordField("Current password")

        newPassword1.value = PasswordField("New password")
        if (!vm.isPasswordSuitable(newPassword1.value))
            Text(text = "Password must contains at least 1 digit and letter and min length: 8",
                color = Color.Gray,
                fontSize = 12.sp)

        newPassword2.value = PasswordField("Repeat password")
        if (newPassword1.value != newPassword2.value)
            Text(text = "Passwords mismatch", color = Color.Red)
        else
            if(vm.isPasswordSuitable(newPassword1.value))
                vm.setUserPassword(newPassword1.value)

        Spacer(modifier = Modifier.padding(20.dp))
        Button(
            onClick =
            {
                /* vm.setHotel(
                     hotelName.value, country.value, city.value, street.value, building.value,
                     postCode.value, phoneNumber.value, checkIn.value, checkOut.value, bitmap.value!!
                 )
                 navController.navigate(Screen.AddRoomScreen.route + "/" + vm.getHotelId())*/
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
            shape = (RoundedCornerShape(16.dp)),
            /*enabled = hotelName.value.isNotEmpty() && country.value.isNotEmpty() &&
                    city.value.isNotEmpty() && street.value.isNotEmpty() &&
                    building.value.isNotEmpty() && postCode.value.isNotEmpty() &&
                    phoneNumber.value.isNotEmpty() && checkIn.value.isNotEmpty() &&
                    checkOut.value.isNotEmpty() && bitmap.value != null,*/
            modifier = Modifier.fillMaxWidth().width(44.dp)
        ) {
            Text(
                text = "Change password",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.padding(32.dp))
        Text(text = "Forgot password", Modifier.wrapContentWidth(Alignment.CenterHorizontally))
    }
}