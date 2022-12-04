package by.yaugesha.hotelbooking.Authorization.Screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.runtime.rememberCoroutineScope
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
import by.yaugesha.hotelbooking.Admin.AdminActivity
import by.yaugesha.hotelbooking.Authorization.AuthViewModel
import by.yaugesha.hotelbooking.Authorization.ui.theme.BackgroundColor
import by.yaugesha.hotelbooking.Authorization.ui.theme.ButtonColor
import by.yaugesha.hotelbooking.Authorization.ui.theme.TextFieldColor
import by.yaugesha.hotelbooking.DataClasses.Screen
import by.yaugesha.hotelbooking.Main.MainActivity
import by.yaugesha.hotelbooking.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun LoginScreen(navController: NavController) {
    val login = rememberSaveable { mutableStateOf("admin") }//"admin"user
    val password = rememberSaveable { mutableStateOf("admin01") }//"admin01"Useruser01
    val context = LocalContext.current
    val vm = AuthViewModel()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(color = BackgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Welcome!", fontSize = 32.sp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(top = 118.dp)
        )

        Text(
            text = "Enter in your account", fontSize = 20.sp,
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
                leadingIcon = {
                    Icon(
                        Icons.Filled.AccountCircle,
                        tint = Color.White,
                        contentDescription = "Login"
                    )
                },
                placeholder = { Text(text = "Login:", color = Color.White, fontSize = 14.sp) },
                shape = (RoundedCornerShape(24.dp)),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    backgroundColor = TextFieldColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
            val passwordVisible = rememberSaveable { mutableStateOf(false) }

            val icon = if (passwordVisible.value)
                painterResource(R.drawable.ic_visibility)
            else
                painterResource(R.drawable.ic_visibility_off)

            OutlinedTextField(
                password.value, { password.value = it },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Lock,
                        tint = Color.White,
                        contentDescription = "password"
                    )
                },
                shape = (RoundedCornerShape(24.dp)),
                singleLine = true,
                placeholder = { Text(text = "Password:", color = Color.White, fontSize = 14.sp) },
                visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                        Icon(
                            painter = icon,
                            tint = Color.White,
                            contentDescription = "password"
                        )
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
        }

        val coroutineScope: CoroutineScope = rememberCoroutineScope()
        Button(
            onClick =
            {
                coroutineScope.launch {
                   Toast.makeText( context, login(context, vm, login.value, password.value), Toast.LENGTH_SHORT ).show()
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
            enabled = login.value.isNotEmpty() && password.value.isNotEmpty(),
            shape = (RoundedCornerShape(24.dp)),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(top = 66.dp)
                .height(44.dp)
                .width(182.dp)
        )
        {
            Text(
                text = "Login",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.padding(top = 60.dp))
        Row(modifier = Modifier.padding(start = 48.dp)) {
            Divider(color = Color.Black, modifier = Modifier.width(132.dp).padding(top = 12.dp))
            Text(text = "Or", fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp, end = 8.dp))
            Divider(color = Color.Black, modifier = Modifier.width(132.dp).padding(top = 12.dp))
        }
        Spacer(modifier = Modifier.padding(top = 18.dp))
        Text(text = "Don't have an account?", modifier = Modifier.padding(start = 106.dp))
        Spacer(modifier = Modifier.padding(top = 24.dp))

        Button(
            onClick = { navController.navigate(Screen.RegistrationScreen.route) },
            colors = ButtonDefaults.buttonColors(backgroundColor = ButtonColor),
            shape = (RoundedCornerShape(24.dp)),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(bottom = 100.dp)
                .height(44.dp)
                .width(182.dp)
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

suspend fun login(context: Context, vm: AuthViewModel, login: String, password: String): String {
    val user = vm.authorities(login, password)
    var string = "Login succeed"
    runBlocking {
        when (user.role) {
            "user" -> {
                val intent = Intent(context,MainActivity::class.java )
                intent.putExtra("login", user.login)
                context.startActivity(intent)
            }
            "admin" -> {
                val intent = Intent(context,AdminActivity::class.java )
                intent.putExtra("login", user.login)
                context.startActivity(intent)
            }
            "error" -> string = "Incorrect input"
        }
    }
    return string

}