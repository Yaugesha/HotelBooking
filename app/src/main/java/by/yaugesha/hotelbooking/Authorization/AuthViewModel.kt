package by.yaugesha.hotelbooking.Authorization

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.yaugesha.hotelbooking.DataClasses.User
import by.yaugesha.hotelbooking.Model
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.MessageDigest


class AuthViewModel: ViewModel() {

    val model = Model()
    //Login
    private fun isPasswordCorrect(user: User, password: String): Boolean {
        val md = MessageDigest.getInstance("MD5")
        val bigInt = BigInteger(1, md.digest(password.toByteArray(Charsets.UTF_8)))
        val md5Pass = String.format("%032x", bigInt)
        return md5Pass == user.password
    }

    suspend fun authorities(login: String, password: String): User {
        val result: Deferred<User>
        runBlocking {
            result = async {  model.getUserData(login) }
        }
        val user: User = result.await()
        delay(1000L)
        Log.i("role", user.role)
        return user/*if (isPasswordCorrect(user, password)) {
            if(user.role == "admin") {
                "admin"
            }
            else
                "user"
        } else
            "error"*/
    }

    //Registration
    fun setUserPassword(password: String) {
        val md = MessageDigest.getInstance("MD5")
        val bigInt = BigInteger(1, md.digest(password.toByteArray(Charsets.UTF_8)))
        model.user.password =  String.format("%032x", bigInt)
    }
    fun setUserEmail(email: String) { // check if it's unique
        model.user.email = email
    }
    fun setUserLogin(login: String) { // check if it's unique
        model.user.login = login
    }
    fun setUserName(name: String) { // check if it's unique
        model.user.name = name
    }
    fun setUserSurname(surname: String) { // check if it's unique
        model.user.surname = surname
    }

    fun isPasswordSuitable(password: String): Boolean {
        return (password.any { it.isDigit() } && password.any { it.isLetter()} && password.length >= 8 )
    }

    fun registerUser(): Boolean {
        model.writeNewUser()
        return true
    }

    fun writeLogin(login: String, context: Context) {
        var fos: FileOutputStream? = null
        fos = context.openFileOutput("USER_LOGIN", MODE_PRIVATE);
        fos?.write(login.toByteArray());
        fos?.close();
    }

    fun getLogin(context: Context): String? {
        var fin: FileInputStream? = null
        fin = context.openFileInput ("USER_LOGIN")
        val bytes = fin?.let { ByteArray(it.available()) }
        fin?.read(bytes)
        val text = bytes?.let { String(it) }
        fin?.close();
        return text
    }
}

class AuthFactory(context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel() as T
    }
}

