package by.yaugesha.hotelbooking.Main

import android.util.Log
import androidx.lifecycle.ViewModel
import by.yaugesha.hotelbooking.DataClasses.Hotel
import by.yaugesha.hotelbooking.DataClasses.Room
import by.yaugesha.hotelbooking.DataClasses.User
import by.yaugesha.hotelbooking.Model
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.math.BigInteger
import java.security.MessageDigest

class MainViewModel: ViewModel() {

    val model = Model()

    suspend fun isPasswordCorrect(userId: String, newPassword: String): Boolean {
        val md = MessageDigest.getInstance("MD5")
        val bigInt = BigInteger(1, md.digest(newPassword.toByteArray(Charsets.UTF_8)))
        val md5Pass = String.format("%032x", bigInt)
        val currentPassword = model.getUserPassword(userId)
        return md5Pass == currentPassword
    }

    fun setUserPassword(password: String) {
        val md = MessageDigest.getInstance("MD5")
        val bigInt = BigInteger(1, md.digest(password.toByteArray(Charsets.UTF_8)))
        model.user.password =  String.format("%032x", bigInt)
    }

    fun isPasswordSuitable(password: String): Boolean {
        return (password.any { it.isDigit() } && password.any { it.isLetter()} && password.length >= 8 )
    }

    suspend fun getRooms(): List<Room> {
        val roomMap: HashMap<String, Room> = model.loadListOfRooms()
        Log.i("hotel list", "Got:  $roomMap")
        return roomMap.values.toList()
    }
    suspend fun getHotelDataForRoom(hotelId: String): Hotel{
        val hotel = Hotel()
        val result: Deferred< HashMap<String, String>>
        runBlocking {
            result = async { model.getHotelDataForUserSearch(hotelId) }
        }
        val hotelMap = result.await()
        hotel.hotelId = hotelMap["hotelId"].toString()
        hotel.name = hotelMap["name"].toString()
        hotel.country = hotelMap["country"].toString()
        hotel.street = hotelMap["street"].toString()
        hotel.building = hotelMap["building"].toString()
        hotel.phone = hotelMap["phone"].toString()
        hotel.postCode = hotelMap["postCode"].toString()
        hotel.checkIn = hotelMap["checkIn"].toString()
        hotel.checkOut = hotelMap["checkOut"].toString()
        hotel.city = hotelMap["city"].toString()
        hotel.photoURI = hotelMap["photoURI"].toString()
        hotel.status = hotelMap["status"].toString()
        /*val result: Deferred<JsonObject>
        val gson = Gson()
        runBlocking {
            result = async { model.getHotelDataForUserSearc(hotelId)!!}
        }
        hotel = gson.fromJson(result.await(), Hotel::class.java)*/
        return hotel
    }
}