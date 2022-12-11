package by.yaugesha.hotelbooking

import android.util.Log
import by.yaugesha.hotelbooking.DataClasses.Booking
import by.yaugesha.hotelbooking.DataClasses.Hotel
import by.yaugesha.hotelbooking.DataClasses.Room
import by.yaugesha.hotelbooking.DataClasses.User
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.HashMap

class Model {
    var user = User()
    var hotel = Hotel()
    var room = Room()
    var booking = Booking()

    private val dataBase: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val storageRef: StorageReference = FirebaseStorage.getInstance().reference


    fun writeNewUser() {
        dataBase.child("Users").child(user.login).child("email").setValue(user.email)
        dataBase.child("Users").child(user.login).child("login").setValue(user.login)
        dataBase.child("Users").child(user.login).child("name").setValue(user.name)
        dataBase.child("Users").child(user.login).child("password").setValue(user.password)
        dataBase.child("Users").child(user.login).child("status").setValue(user.status)
        dataBase.child("Users").child(user.login).child("role").setValue(user.role)
        dataBase.child("Users").child(user.login).child("surname").setValue(user.surname)
    }

    fun writeNewHotel(byteArray: ByteArray) {
        dataBase.child("Hotels").child(hotel.hotelId).child("hotelId").setValue(hotel.hotelId)
        dataBase.child("Hotels").child(hotel.hotelId).child("name").setValue(hotel.name)
        dataBase.child("Hotels").child(hotel.hotelId).child("country").setValue(hotel.country)
        dataBase.child("Hotels").child(hotel.hotelId).child("city").setValue(hotel.city)
        dataBase.child("Hotels").child(hotel.hotelId).child("street").setValue(hotel.street)
        dataBase.child("Hotels").child(hotel.hotelId).child("building").setValue(hotel.building)
        dataBase.child("Hotels").child(hotel.hotelId).child("postCode").setValue(hotel.postCode)
        dataBase.child("Hotels").child(hotel.hotelId).child("phone").setValue(hotel.phone)
        dataBase.child("Hotels").child(hotel.hotelId).child("checkIn").setValue(hotel.checkIn)
        dataBase.child("Hotels").child(hotel.hotelId).child("checkOut").setValue(hotel.checkOut)
        dataBase.child("Hotels").child(hotel.hotelId).child("status").setValue(hotel.status)
        dataBase.child("Hotels").child(hotel.hotelId).child("amenities").setValue(hotel.amenities)
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
        dataBase.child("Rooms").child(room.roomId).child("roomId").setValue(room.roomId)
        dataBase.child("Rooms").child(room.roomId).child("hotelID").setValue(room.hotelID)
        dataBase.child("Rooms").child(room.roomId).child("peopleCapacity").setValue(room.peopleCapacity)
        dataBase.child("Rooms").child(room.roomId).child("price").setValue(room.price)
        dataBase.child("Rooms").child(room.roomId).child("numberOfRooms").setValue(room.numberOfRooms)
        dataBase.child("Rooms").child(room.roomId).child("square").setValue(room.square)
        dataBase.child("Rooms").child(room.roomId).child("amountOfRooms").setValue(room.amountOfRooms)
        dataBase.child("Rooms").child(room.roomId).child("numberOfDoubleBeds").setValue(room.numberOfDoubleBeds)
        dataBase.child("Rooms").child(room.roomId).child("numberOfSingleBeds").setValue(room.numberOfSingleBeds)
        dataBase.child("Rooms").child(room.roomId).child("status").setValue(room.status)
        dataBase.child("Rooms").child(room.roomId).child("amenities").setValue(room.amenities)
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
        dataBase.child("Bookings").child(booking.bookingId).child("bookingId").setValue(booking.bookingId)
        dataBase.child("Bookings").child(booking.bookingId).child("user").setValue(booking.user)
        dataBase.child("Bookings").child(booking.bookingId).child("room").setValue(booking.room)
        dataBase.child("Bookings").child(booking.bookingId).child("guests").setValue(booking.guests)
        dataBase.child("Bookings").child(booking.bookingId).child("checkInDate").setValue(booking.checkInDate)
        dataBase.child("Bookings").child(booking.bookingId).child("checkOutDate").setValue(booking.checkOutDate)
        dataBase.child("Bookings").child(booking.bookingId).child("amountOfRooms").setValue(booking.amountOfRooms)
        dataBase.child("Bookings").child(booking.bookingId).child("cost").setValue(booking.cost)
        dataBase.child("Bookings").child(booking.bookingId).child("status").setValue(booking.status)
        dataBase.child("Bookings").child(booking.bookingId).child("date").setValue(booking.date/*.format(DateTimeFormatter.ofLocalizedDate(
            FormatStyle.SHORT))*/)
        //dataBase.child("Rooms").child(booking.room).child("bookings").child(booking.id).setValue(LocalDateTime.now())
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
       /* val roomData = room
        runBlocking {
            dataBase.child("Rooms").child(roomId).get().addOnSuccessListener{
                Log.i("firebase", "Got value ${it.value}")
                roomData.roomId = it.child("roomId").value.toString()
                roomData.hotelID = it.child("hotelID").value.toString()
                roomData.peopleCapacity = (it.child("peopleCapacity").value as Long).toInt()
                roomData.price = (it.child("price").value as Long).toInt()
                roomData.numberOfRooms = (it.child("numberOfRooms").value as Long).toInt()
                roomData.square = (it.child("square").value as Long).toInt()
                roomData.amountOfRooms = (it.child("amountOfRooms").value as Long).toInt()
                roomData.numberOfDoubleBeds = (it.child("numberOfDoubleBeds").value as Long).toInt()
                roomData.numberOfSingleBeds = (it.child("numberOfSingleBeds").value as Long).toInt()
                roomData.status = it.child("status").value.toString()
                roomData.photoURI = it.child("photoURI").value.toString()
                roomData.amenities = it.child("amenities").value as HashMap<String, Boolean>
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }.await() }
        return roomData*/
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

    suspend fun getUserPassword(login: String): String {
        return dataBase.child("Users").child(login).child("password").get().await().value.toString()
    }

    suspend fun loadListOfUsers(): HashMap<String, User> {
        return dataBase.child("Users").get().await().getValue<HashMap<String, User>>()!!
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

    suspend fun loadListOfRooms(): HashMap<String, Room> {
        return dataBase.child("Rooms").get().await().getValue<HashMap<String, Room>>()!!
    }

    suspend fun getHotelDataForUserSearch(hotelId: String): HashMap<String, Any> {
        var hotelData = HashMap<String, Any>()
        hotelData = dataBase.child("Hotels").child(hotelId).get().await().getValue<Map<String, Any>>() as HashMap<String, Any>
        return hotelData
    }
    /*suspend fun getHotelDataForUserSearc(hotelId: String): JsonObject? {
        return dataBase.child("Hotels").child(hotelId).get().await().getValue<JsonObject>()
    }*/


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