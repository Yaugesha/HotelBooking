package by.yaugesha.hotelbooking.Athorisation.Screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import by.yaugesha.hotelbooking.Authorization.AuthViewModel
import by.yaugesha.hotelbooking.Authorization.ui.theme.BackgroundColor
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.Authorization.ui.theme.TextFieldColor
import by.yaugesha.hotelbooking.DataClasses.Screen
import by.yaugesha.hotelbooking.R

@Composable
fun RegistrationScreen(navController: NavController) {
    val pass1 = remember { mutableStateOf ("") }
    val pass2 = remember { mutableStateOf ("") }
    val email = remember { mutableStateOf("") }
    val login = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val vm = AuthViewModel()
    val context = LocalContext.current
    vm.setUserEmail(email.value)
    vm.setUserLogin(login.value)
    vm.setUserName(name.value)
    vm.setUserSurname(surname.value)

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(color = BackgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Get Started!", fontSize = 32.sp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(top = 100.dp)
        )

        Text(
            text = "Create your account", fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 28.dp, end = 28.dp)
        )
        Column(
            modifier = Modifier
                .padding(top = 20.dp, start = 28.dp, end = 28.dp)
        ) {
            OutlinedTextField(
                login.value, onValueChange = { newText -> login.value = newText },
                shape = (RoundedCornerShape(24.dp)),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        Icons.Filled.AccountCircle,
                        tint = Color.White,
                        contentDescription = "Login"
                    )
                },
                placeholder = { Text(text = "Login:", color = Color.White, fontSize = 14.sp) },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    backgroundColor = TextFieldColor
                ),
                modifier = Modifier
                    .height(52.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(top = 20.dp))

            OutlinedTextField(
                email.value, onValueChange = { newText -> email.value = newText },
                shape = (RoundedCornerShape(24.dp)),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.email),
                        tint = Color.White,
                        contentDescription = "email"
                    )
                },
                placeholder = { Text(text = "Email:", color = Color.White, fontSize = 14.sp) },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    backgroundColor = TextFieldColor
                ),
                modifier = Modifier
                    .height(52.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(top = 20.dp))

            OutlinedTextField(
                name.value, onValueChange = { newText -> name.value = newText },
                shape = (RoundedCornerShape(24.dp)),
                singleLine = true,
                //label = { Text("Name", color = Color.White) },
                placeholder = { Text(text = "Name:", color = Color.White, fontSize = 14.sp) },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    backgroundColor = TextFieldColor
                ),
                modifier = Modifier
                    .height(52.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(top = 20.dp))

            OutlinedTextField(
                surname.value, onValueChange = { newText -> surname.value = newText },
                shape = (RoundedCornerShape(24.dp)),
                singleLine = true,
                //label = { Text("Surname", color = Color.White) },
                placeholder = { Text(text = "Surname:", color = Color.White, fontSize = 14.sp) },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    backgroundColor = TextFieldColor
                ),
                modifier = Modifier
                    .height(52.dp)
                    .fillMaxWidth()
            )
            //Spacer(modifier = Modifier.padding(top = 20.dp))

            pass1.value = PasswordField("Enter password")
            if (!vm.isPasswordSuitable(pass1.value))
                Text(text = "Password must contains at least 1 digit and letter and min length: 8",
                    color = Color.Gray,
                    fontSize = 12.sp)

            pass2.value = PasswordField("Repeat password")

            if (pass1.value != pass2.value)
                Text(text = "Passwords mismatch", color = Color.Red)
            else
                if(vm.isPasswordSuitable(pass1.value))
                    vm.setUserPassword(pass1.value)
        }

        Button(
            onClick =
            {
                if(vm.registerUser()) {
                    Toast.makeText( context,"You've benn registered", Toast.LENGTH_SHORT ).show()
                    navController.navigate(Screen.LoginScreen.route)
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
            enabled = pass1.value == pass2.value,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(top = 70.dp)
                .height(44.dp)
                .width(182.dp),
            shape = (RoundedCornerShape(24.dp)),
        )
        {
            Text(
                text = "Sign Up",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun PasswordField(contentDescription: String): String {
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisible = rememberSaveable { mutableStateOf(false) }

    val icon = if(passwordVisible.value)
        painterResource(R.drawable.ic_visibility)
    else
        painterResource(R.drawable.ic_visibility_off)

    OutlinedTextField(
        password.value, {  password.value = it },
        leadingIcon = { Icon(
            Icons.Filled.Lock,
            tint = Color.White,
            contentDescription = "password"
        )
        },
        shape = (RoundedCornerShape(24.dp)),
        singleLine = true,
        placeholder = { Text(text = contentDescription, color = Color.White, fontSize = 14.sp) },
        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                Icon(painter = icon, tint = Color.White, contentDescription = "password")
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            backgroundColor = TextFieldColor
        ),
        modifier = Modifier
            .padding(top = 20.dp)
            .height(52.dp)
            .fillMaxWidth()
    )
    return password.value
}