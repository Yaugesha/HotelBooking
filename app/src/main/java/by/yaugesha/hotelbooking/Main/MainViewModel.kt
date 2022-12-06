package by.yaugesha.hotelbooking.Main

import android.util.Log
import androidx.lifecycle.ViewModel
import by.yaugesha.hotelbooking.DataClasses.*
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

    suspend fun getRooms(searchData: Search): List<Room> {
        var city = ""
        var country = ""
        val hotelList = mutableListOf<Hotel>()
        val roomList = mutableListOf<Room>()
        for(i in searchData.location.indices){
            if(searchData.location[i] == ',') {
                city = searchData.location.take(i)
                country = searchData.location.removeRange(0..i+1)
            }
        }
        Log.i("dividing", "location:  $city $country")
        model.searchHotelByLocation(city)?.values!!.forEach {
            if(it.country == country) {
                hotelList.add(it)
            }
        }
        Log.i("search res1", "hotels:  $hotelList")
        hotelList.forEach { roomList.addAll(model.findRoomsByHotelId(it.hotelId)?.values!!.toList())}
        Log.i("search res2", "rooms:  $roomList")
        roomList.removeIf{ it.peopleCapacity < searchData.guests  }
        Log.i("search res3", "rooms:  $roomList")
        return roomList
    }

    suspend fun getHotelDataForRoom(hotelId: String): Hotel {
        val hotel: Hotel = Hotel()
        val result: Deferred< HashMap<String, Any>>
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
        hotel.amenities = hotelMap["amenities"] as HashMap<String, Boolean>
        /*val result: Deferred<JsonObject>
        val gson = Gson()
        runBlocking {
            result = async { model.getHotelDataForUserSearc(hotelId)!!}
        }
        hotel = gson.fromJson(result.await(), Hotel::class.java)*/
        return hotel
    }

    fun setBooking(booking: Booking) {
        model.booking = booking
        model.writeNewBooking()
    }
}