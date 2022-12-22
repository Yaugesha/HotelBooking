package by.yaugesha.hotelbooking.Main.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Admin.Hotel.Add.TextField
import by.yaugesha.hotelbooking.Athorisation.Screens.PasswordField
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.DataClasses.User
import by.yaugesha.hotelbooking.Main.MainViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun EditProfileScreen(navController: NavController) {
    val vm = MainViewModel()
    val context = LocalContext.current
    val login = remember {vm.getLogin(context)!!}
    var user = User()
    vm.viewModelScope.launch { user = getUserData(vm, login) }

    Column(
        modifier = Modifier
            .padding(start = 45.dp, end = 45.dp, top = 34.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val email = rememberSaveable { mutableStateOf (user.email) }
        val name = rememberSaveable { mutableStateOf (user.name) }
        val surname = rememberSaveable { mutableStateOf (user.surname) }

        TextField(string = email, field = "Email")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        TextField(string = name, field = "Name")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        TextField(string = surname, field = "Surname")
        Spacer(modifier = Modifier.padding(top = 20.dp))
        Button(
            onClick =
            {
                user.login = login
                user.name = name.value
                user.email = email.value
                user.surname = surname.value
                vm.updateUser(user)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
            shape = (RoundedCornerShape(16.dp)),
            enabled = name.value.isNotEmpty() && email.value.isNotEmpty() && surname.value.isNotEmpty(),
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
                if(vm.isPasswordCorrect(user.login, currentPassword.value, user.password))
                {
                    if (vm.isPasswordSuitable(newPassword1.value))
                        if (newPassword1.value == newPassword2.value)
                            vm.updateUserPassword(user.login, newPassword1.value)
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
            shape = (RoundedCornerShape(16.dp)),
            enabled = currentPassword.value.isNotEmpty() && newPassword1.value.isNotEmpty() &&
                    newPassword2.value.isNotEmpty() && newPassword1.value != newPassword2.value &&
                    !vm.isPasswordSuitable(newPassword1.value),
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

suspend fun getUserData(vm: MainViewModel, login: String): User {
    val result: Deferred<User>
    runBlocking {
        result = async { vm.getUser(login)}
    }
    return result.await()
}