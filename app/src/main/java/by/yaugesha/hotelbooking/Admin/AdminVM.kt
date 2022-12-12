package by.yaugesha.hotelbooking.Admin

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import by.yaugesha.hotelbooking.DataClasses.Hotel
import by.yaugesha.hotelbooking.DataClasses.Room
import by.yaugesha.hotelbooking.DataClasses.User
import by.yaugesha.hotelbooking.Model
import java.io.ByteArrayOutputStream
import java.util.*

class AdminViewModel: ViewModel() {

    val model = Model()
    var userList: List<User> = mutableListOf()
    var hotelList: List<Hotel> = mutableListOf()
    var roomList: List<Room> = mutableListOf()

    suspend fun getUsers(): List<User> {
        val userMap: HashMap<String, User> = model.loadListOfUsers()
        userList = userMap.values.toList()
        Log.i("user list", "Got size ${userList}")
        return userList
    }


    /*      Hotels          */

    fun setHotel(hotelName: String, country: String, city: String, street: String, building: String,
        postCode: String, phoneNumber: String, checkIn: String, checkOut: String, bitmap: Bitmap,
                 mapOfHotelAmenities: HashMap<String, Boolean>)
    {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val byteArray = baos.toByteArray()
        model.hotel = Hotel(name = hotelName, country = country, city = city, street = street,
            building = building, postCode = postCode, phone = phoneNumber, checkIn = checkIn,
            checkOut = checkOut, hotelId = UUID.randomUUID().toString(), amenities = mapOfHotelAmenities)
        model.writeNewHotel(byteArray)
    }
    fun getHotelId(): String {
        return model.hotel.hotelId
    }

    suspend fun getHotels(): List<Hotel> {
        val hotelMap: HashMap<String, Hotel> = model.loadListOfHotels()
        Log.i("hotel list", "Got:  $hotelMap")
        hotelList = hotelMap.values.toList()
        return hotelList
    }

    suspend fun getHotelById(hotelId: String): Hotel {
        val hotel = model.getHotel(hotelId)
        Log.i("edit room", "Got1:  $hotel")
        return hotel
    }

    fun updateHotel(hotel: Hotel, image: Bitmap?) {
        val hotelMap = mapOf<String, Any>("hotelId" to hotel.hotelId, "name" to hotel.name, "country" to hotel.country,
            "street" to hotel.street, "building" to hotel.building, "phone" to hotel.phone, "postCode" to hotel.postCode,
            "checkIn" to hotel.checkIn, "checkOut" to hotel.checkOut, "city" to hotel.city, "photoURI" to hotel.photoURI,
            "status" to hotel.status,)
        if(image != null) {
            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val byteArray = baos.toByteArray()
            model.updateHotel(hotel, byteArray, hotelMap)
        }
        model.updateHotel(hotel, hotelMap = hotelMap)
    }

    suspend fun searchHotelsByLocation(location: String): List<Hotel> {
        var city = ""
        var country = ""
        val hotelList = mutableListOf<Hotel>()
        for(i in location.indices){
            if(location[i] == ',') {
                city = location.take(i)
                country = location.removeRange(0..i+1)
            }
        }
        model.searchHotelByLocation(city)?.values!!.forEach {
            if(it.country == country) {
                hotelList.add(it)
            }
        }
        return hotelList
    }

    suspend fun searchHotelByItName(hotelName: String): List<Hotel>? {
        return model.searchHotelByItName(hotelName)?.values?.toList()
    }

    suspend fun searchHotelsByLocationAndName(searchParameter: String): List<Hotel> {
        var location = ""
        var hotelName = ""
        Log.i("name+location", "location:  $searchParameter")
        for(i in searchParameter.indices){
            if(searchParameter[i] == ',') {
                hotelName = searchParameter.take(i)
                Log.i("dividing res", "name: $hotelName location: $searchParameter ")
                location = searchParameter.removeRange(0..i+1)
                break
            }
        }
        Log.i("dividing", "location:  $location $hotelName")
        var city = ""
        var country = ""
        val hotelList = mutableListOf<Hotel>()
        for(i in location.indices){
            if(location[i] == ',') {
                city = location.take(i)
                country = location.removeRange(0..i+1)
            }
        }
        model.searchHotelByLocation(city)?.values!!.forEach {
            if(it.country == country && it.name == hotelName) {
                hotelList.add(it)
            }
        }
        return hotelList
    }


    /*      Rooms          */

    fun setRoom(hotelId: String, peopleCapacity: Int, numberOfRooms: Int,  square: Int, price: Int, amountOfRooms: Int,
                numberOfDoubleBeds: Int, numberOfSingleBeds: Int, bitmap: Bitmap, mapOfRoomAmenities: HashMap<String, Boolean>)
    {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val byteArray = baos.toByteArray()
        model.room = Room(hotelID = hotelId, peopleCapacity = peopleCapacity, numberOfRooms = numberOfRooms, square = square, price = price,
            amountOfRooms = amountOfRooms, numberOfDoubleBeds = numberOfDoubleBeds, numberOfSingleBeds = numberOfSingleBeds,
            roomId = UUID.randomUUID().toString(), amenities = mapOfRoomAmenities)
        model.writeNewRoom(byteArray)
    }

    suspend fun getRooms(hotelId: String): List<Room> {
        val roomMap: HashMap<String, Room>? = model.findRoomsByHotelId(hotelId)
        if (roomMap != null) {
            roomList = roomMap.values.toList()
        }
        Log.i("room list", "Got:  ${roomList}")
        return roomList
    }

    fun updateRoom(room: Room, image: Bitmap?) {
        val roomMap = mapOf<String, Any>("roomId" to room.roomId, "hotelID" to room.hotelID, "peopleCapacity" to room.peopleCapacity,
        "price" to room.price, "numberOfRooms" to room.numberOfRooms, "square" to room.square, "amountOfRooms" to room.amountOfRooms,
        "numberOfDoubleBeds" to room.numberOfDoubleBeds, "numberOfSingleBeds" to room.numberOfSingleBeds)
        if(image != null) {
            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val byteArray = baos.toByteArray()
            model.updateRoom(room, byteArray, roomMap)
        }
        model.updateRoom(room, roomMap = roomMap)
    }

    fun deleteRoom(roomId: String) {
        model.deleteRoom(roomId)
    }

    suspend fun getListOfCities(): List<String> {
        val mapOfHotels = model.loadListOfHotels()
        val listOfCities = mutableListOf<String>()
        mapOfHotels.forEach { listOfCities.add(it.value.city + ", " + it.value.country)}
        return listOfCities.distinct()
    }

    suspend fun getListOfHotelNames(): List<String> {
        val mapOfHotels = model.loadListOfHotels()
        val listHotelNames = mutableListOf<String>()
        mapOfHotels.forEach { listHotelNames.add(it.value.name)}
        return listHotelNames.distinct()
    }

}

class AdminVMFactory(context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminViewModel() as T
    }
}