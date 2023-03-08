package by.yaugesha.hotelbooking

import android.util.Log
import by.yaugesha.hotelbooking.DataClasses.Booking
import by.yaugesha.hotelbooking.DataClasses.Hotel
import by.yaugesha.hotelbooking.DataClasses.Room
import by.yaugesha.hotelbooking.DataClasses.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.*

class Model {
    var user = User()
    var hotel = Hotel()
    var room = Room()
    var booking = Booking()

    private val dataBase: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val storageRef: StorageReference = FirebaseStorage.getInstance().reference


    fun writeNewUser() {
        dataBase.child("Users").child(user.login).setValue(user)
    }

    fun writeNewHotel(byteArray: ByteArray) {
        dataBase.child("Hotels").child(hotel.hotelId).setValue(hotel)
        var url: String
        val hotelImagesReference = storageRef.child("Images").child("Hotels").child(hotel.hotelId)
            .child("hotel_" + hotel.hotelId)
        hotelImagesReference.putBytes(byteArray).addOnCompleteListener { task1->
            if (task1.isSuccessful) {
                Log.d("Firebase", "file loaded")
                hotelImagesReference.downloadUrl.addOnCompleteListener{task2->
                    if (task2.isSuccessful) {
                        url = task2.result.toString()
                        dataBase.child("Hotels").child(hotel.hotelId).child("photoURI")
                            .setValue(url)
                    }
                }
            }
        }

    }

    fun writeNewRoom(byteArray: ByteArray) {
        dataBase.child("Rooms").child(room.roomId).setValue(room)
        var url: String
        val hotelImagesReference = storageRef.child("Images").child("Hotels").child(room.hotelID)
            .child("room_" + room.roomId)
        hotelImagesReference.putBytes(byteArray).addOnCompleteListener { task1->
            if (task1.isSuccessful) {
                Log.d("Firebase", "file loaded")
                hotelImagesReference.downloadUrl.addOnCompleteListener{task2->
                    if (task2.isSuccessful) {
                        url = task2.result.toString()
                        dataBase.child("Rooms").child( room.roomId).child("photoURI")
                            .setValue(url)
                    }
                }
            }
        }
        dataBase.child("Hotels").child(room.hotelID).child("status").setValue("active")
    }

    fun writeNewBooking() {
        dataBase.child("Bookings").child(booking.bookingId).setValue(booking)
    }

    fun writeNewFavorite(roomId: String, login: String) {
        dataBase.child("Favorites").child(login).child(roomId).setValue(UUID.randomUUID().toString())
    }

    suspend fun findBookingsOFRoom(roomId: String): HashMap<String, Booking>? {
        return dataBase.child("Bookings").orderByChild("room").equalTo(roomId).get().await().getValue<HashMap<String, Booking>>()
    }

    suspend fun findUserBookings(login: String): HashMap<String, Booking>? {
        var bookingsMap = dataBase.child("Bookings").orderByChild("user").equalTo(login).get().await().getValue<HashMap<String, Booking>>()
        if (bookingsMap != null) {
            if(bookingsMap.isNotEmpty())
                return bookingsMap
        }
            return null
    }

    suspend fun loadListOfHotels(): HashMap<String, Hotel> {
        return dataBase.child("Hotels").get().await().getValue<HashMap<String, Hotel>>()!!
    }

    suspend fun findRoomsByHotelId(hotelID: String): HashMap<String, Room>? {
        //Log.i("search pre_res2", "hotels:  ${dataBase.child("Rooms").orderByChild("hotelID").equalTo(hotelID).get().await().getValue<HashMap<String, Room>>()}")
        return dataBase.child("Rooms").orderByChild("hotelID").equalTo(hotelID).get().await().getValue<HashMap<String, Room>>()
    }

    suspend fun getHotel(hotelId: String): Hotel {
        val hotelData = hotel
        runBlocking {
            dataBase.child("Hotels").child(hotelId).get().addOnSuccessListener{
                Log.i("firebase", "Got value ${it.value}")
                hotelData.name = it.child("name").value.toString()
                hotelData.hotelId = it.child("hotelId").value.toString()
                hotelData.building = it.child("building").value.toString()
                hotelData.street = (it.child("street")).value.toString()
                hotelData.city = (it.child("city")).value.toString()
                hotelData.country = (it.child("country")).value.toString()
                hotelData.checkOut = (it.child("checkOut")).value.toString()
                hotelData.checkIn = (it.child("checkIn")).value.toString()
                hotelData.phone = (it.child("phone")).value.toString()
                hotelData.postCode = (it.child("postCode")).value .toString()
                hotelData.status = it.child("status").value.toString()
                hotelData.photoURI = it.child("photoURI").value.toString()
                hotelData.amenities = it.child("amenities").value as HashMap<String, Boolean>
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }.await() }
        return hotelData
    }

    suspend fun getRoom(roomId: String): HashMap<String, Any>/*Room*/ {
        return dataBase.child("Rooms").child(roomId).get().await().getValue<HashMap<String, Any>>()  as HashMap<String, Any>
    }

    fun getUserData(login: String): User {
        val userData = user
        runBlocking {
            dataBase.child("Users").child(login).get().addOnSuccessListener {
                Log.i("firebase", "Got value ${it.value}")
                runBlocking {
                    userData.password = it.child("password").value.toString()
                    userData.login = login
                    userData.role = it.child("role").value.toString()
                }
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }
        }
        return userData
    }

    suspend fun getUser(login: String): User? {
        return dataBase.child("Users").child(login).get().await().getValue<User>()
    }

    suspend fun getUserPassword(login: String): String {
        return dataBase.child("Users").child(login).child("password").get().await().value.toString()
    }

    suspend fun loadListOfUsers(): HashMap<String, User> {
        return dataBase.child("Users").get().await().getValue<HashMap<String, User>>()!!
    }

    suspend fun loadListOfBookings(): HashMap<String, Booking> {
        return dataBase.child("Bookings").get().await().getValue<HashMap<String, Booking>>()!!
    }

    fun updateRoom(room: Room, image: ByteArray? = null, roomMap: Map<String, Any>) {
        dataBase.child("Rooms").child(room.roomId).updateChildren(roomMap)
        dataBase.child("Rooms").child(room.roomId).child("amenities").updateChildren(room.amenities as Map<String, Any>)
        var url: String
        val hotelImagesReference = storageRef.child("Images").child("Hotels").child(room.hotelID)
            .child("room_" + room.roomId)
        if (image != null) {
            hotelImagesReference.putBytes(image).addOnCompleteListener { task1->
                if (task1.isSuccessful) {
                    Log.d("Firebase", "file loaded")
                    hotelImagesReference.downloadUrl.addOnCompleteListener{task2->
                        if (task2.isSuccessful) {
                            url = task2.result.toString()
                            dataBase.child("Rooms").child( room.roomId).child("photoURI")
                                .setValue(url)
                        }
                    }
                }
            }
        }
        dataBase.child("Hotels").child(room.hotelID).child("status").setValue("active")
    }

    fun updateUser(login: String, userMap: Map<String, String>) {
        dataBase.child("Users").child(login).updateChildren(userMap)
    }

    fun updatePassword(userId: String, password: String) {
        dataBase.child("Users").child(userId).child("password").setValue(password)
    }

    fun updateHotel(hotel: Hotel, image: ByteArray? = null, hotelMap: Map<String, Any>) {
        dataBase.child("Hotels").child(hotel.hotelId).updateChildren(hotelMap)
        dataBase.child("Hotels").child(hotel.hotelId).child("amenities").updateChildren(hotel.amenities as Map<String, Any>)
        var url: String
        val hotelImagesReference = storageRef.child("Images").child("Hotels").child(hotel.hotelId)
            .child("hotel_" + hotel.hotelId)
        if (image != null) {
            hotelImagesReference.putBytes(image).addOnCompleteListener { task1->
                if (task1.isSuccessful) {
                    Log.d("Firebase", "file loaded")
                    hotelImagesReference.downloadUrl.addOnCompleteListener{task2->
                        if (task2.isSuccessful) {
                            url = task2.result.toString()
                            dataBase.child("Hotels").child(hotel.hotelId).child("photoURI")
                                .setValue(url)
                        }
                    }
                }
            }
        }
    }

    fun deleteRoom(roomId: String) {
        dataBase.child("Rooms").child(roomId).removeValue()
    }

    suspend fun loadListOfRooms(): List<Room> {
        return dataBase.child("Rooms").get().await().getValue<List<Room>>()!!
    }

    suspend fun getHotelDataForUserSearch(hotelId: String): HashMap<String, Any> {
        var hotelData = HashMap<String, Any>()
        hotelData = dataBase.child("Hotels").child(hotelId).get().await().getValue<Map<String, Any>>() as HashMap<String, Any>
        return hotelData
    }


    suspend fun searchHotelByLocation(city: String): HashMap<String, Hotel>? {
        return dataBase.child("Hotels").orderByChild("city").equalTo(city).get().await().getValue<HashMap<String, Hotel>>()
    }

    suspend fun searchHotelByItName(hotelName: String): HashMap<String, Hotel>? {
        return dataBase.child("Hotels").orderByChild("name").equalTo(hotelName).get().await().getValue<HashMap<String, Hotel>>()
}

    fun cancelBooking(bookingId: String) {
        dataBase.child("Bookings").child(bookingId).child("status").setValue("canceled")
    }

    fun deleteBooking(bookingId: String) {
        dataBase.child("Bookings").child(bookingId).removeValue()
    }

    fun deleteFavorite(roomId: String, login: String) {
        dataBase.child("Favorites").child(login).child(roomId).removeValue()
    }

    suspend fun getListOfFavorites(login: String): HashMap<String, String>? {
        return dataBase.child("Favorites").child(login).get().await().getValue<HashMap<String, String>>()
    }

}