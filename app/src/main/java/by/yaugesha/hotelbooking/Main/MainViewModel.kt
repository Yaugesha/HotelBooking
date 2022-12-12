package by.yaugesha.hotelbooking.Main

import android.util.Log
import androidx.lifecycle.ViewModel
import by.yaugesha.hotelbooking.DataClasses.Booking
import by.yaugesha.hotelbooking.DataClasses.Hotel
import by.yaugesha.hotelbooking.DataClasses.Room
import by.yaugesha.hotelbooking.DataClasses.Search
import by.yaugesha.hotelbooking.Model
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat

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
        var roomList = mutableListOf<Room>()
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
        val formatter = SimpleDateFormat("dd.MM.yyyy")

        /*if(searchData.sorts.mapOfAmenities.all{ it.value != false})
            hotelList.removeIf{
                        it.amenities.all { (amenity, value) -> searchData.sorts.mapOfAmenities[amenity] == value }
                    }*/
        hotelList.forEach {
            model.findRoomsByHotelId(it.hotelId)?.values!!.toList().forEach {
                room ->
                    room.amenities += it.amenities
                    roomList.add(room)
            }
        }

        roomList.removeIf{ it.peopleCapacity < searchData.guests }

        if(searchData.sorts.maxPrice != 0 && searchData.sorts.minPrice != 0)
            roomList.removeIf { it.price < searchData.sorts.minPrice || it.price > searchData.sorts.maxPrice }

        if(searchData.sorts.numberOfDoubleBeds != 0 || searchData.sorts.numberOfSingleBeds != 0)
            roomList.removeIf {
                it.numberOfDoubleBeds < searchData.sorts.numberOfDoubleBeds ||
                it.numberOfSingleBeds < searchData.sorts.numberOfSingleBeds
            }

        Log.i("rooms:",  "$roomList")
        Log.i("rooms:",  "${searchData.sorts.mapOfAmenities}")
        if(searchData.sorts.mapOfAmenities.any{ it.value == true }) {
            Log.i("rooms:", "$roomList")
            roomList.removeIf {
                searchData.sorts.mapOfAmenities.any { (amenity, value) -> it.amenities[amenity] != value }
            }
        }

        if (roomList.isEmpty()) {
            Log.i("rooms:", "$roomList")
            return roomList
        }

        roomList = roomList.sortedBy{ it.peopleCapacity } as MutableList<Room>
        var listOfBookings: List<Booking>
        var resultRoomList = mutableListOf<Room>()
        roomList.forEach{ it ->
            val mapOfRoomBookings = model.findBookingsOFRoom(it.roomId)?.values
            if(mapOfRoomBookings != null) {
                listOfBookings = model.findBookingsOFRoom(it.roomId)?.values?.toList()!!// bookings for one room type in hotel
                var countOfBookings = 0 // count of bookings which date cross search date
                listOfBookings.forEach{ booking ->
                    if(!(formatter.parse(booking.checkInDate)?.time!! < searchData.checkInDate.time
                        && formatter.parse(booking.checkOutDate)?.time!! < searchData.checkInDate.time)) {
                        if (!(formatter.parse(booking.checkInDate)?.time!! > searchData.checkOutDate.time
                                    && formatter.parse(booking.checkOutDate)?.time!! > searchData.checkOutDate.time)
                        )
                            countOfBookings += booking.amountOfRooms
                    }
//                roomList.removeIf { countOfBookings > (it.amountOfRooms - searchData.rooms) }
                    if(countOfBookings < (it.amountOfRooms - searchData.rooms) && it.amountOfRooms >= searchData.rooms)
                        resultRoomList.add(it)
                }
            }
            else
                if(it.amountOfRooms >= searchData.rooms)
                    resultRoomList.add(it)
        }
        return resultRoomList
    }

    suspend fun checkIfRoomsFree(room: Room, searchData: Search): Boolean {
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        Log.i("got value:",  "${room.amountOfRooms < searchData.rooms}")
        if(room.amountOfRooms < searchData.rooms)
            return false
        val listOfBookings = model.findBookingsOFRoom(room.roomId)?.values?.toList()
        var countOfBookings = 0
        if(listOfBookings != null) {
            listOfBookings.forEach{ booking ->
            if(!(formatter.parse(booking.checkInDate)?.time!! < searchData.checkInDate.time
                        && formatter.parse(booking.checkOutDate)?.time!! < searchData.checkInDate.time)) {
                    if (!(formatter.parse(booking.checkInDate)?.time!! > searchData.checkOutDate.time
                                && formatter.parse(booking.checkOutDate)?.time!! > searchData.checkOutDate.time)
                    )
                        countOfBookings += booking.amountOfRooms
            }
                return countOfBookings < (room.amountOfRooms - searchData.rooms)
            }
        }
        return true
    }

    suspend fun checkBookingEdition(room: Room, newBooking: Booking): Boolean {
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        Log.i("got value:",  "${room.amountOfRooms < newBooking.amountOfRooms}")
        if(room.amountOfRooms < newBooking.amountOfRooms)
            return false
        val listOfBookings = model.findBookingsOFRoom(room.roomId)?.values?.toList()
        var countOfBookings = 0
        if(listOfBookings != null) {
            listOfBookings.forEach{ booking ->
                if(booking.user != newBooking.user) {
                    if (!(formatter.parse(booking.checkInDate)?.time!! < formatter.parse(newBooking.checkInDate)?.time!!
                                && formatter.parse(booking.checkOutDate)?.time!! < formatter.parse(newBooking.checkInDate)?.time!!)
                    ) {
                        if (!(formatter.parse(booking.checkInDate)?.time!! > formatter.parse(newBooking.checkOutDate)?.time!!
                                && formatter.parse(booking.checkOutDate)?.time!! > formatter.parse(newBooking.checkOutDate)?.time!!)
                        )
                            countOfBookings += booking.amountOfRooms
                    }
                    return countOfBookings < (room.amountOfRooms - newBooking.amountOfRooms)
                }
            }
        }
        return true
    }

    fun cancelBooking(bookingId: String){
        model.cancelBooking(bookingId)
    }

    fun deleteBooking(bookingId: String){
        model.deleteBooking(bookingId)
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
        return hotel
    }

    suspend fun getRoomById(roomId: String): Room {
        val result: Deferred<HashMap<String, Any>>
        runBlocking {
            result = async { model.getRoom(roomId) }
        }
        val roomMap = result.await()
        Log.i("find room", "Got1: $roomId $roomMap ")
        val room = Room()
        room.roomId = roomMap["roomId"].toString()
        room.hotelID = roomMap["hotelID"].toString()
        room.peopleCapacity = roomMap["peopleCapacity"].toString().toInt()
        room.price = roomMap["price"].toString().toInt()
        room.numberOfRooms = roomMap["numberOfRooms"].toString().toInt()
        room.square = roomMap["square"].toString().toInt()
        room.amountOfRooms = roomMap["amountOfRooms"].toString().toInt()
        room.numberOfDoubleBeds = roomMap["numberOfDoubleBeds"].toString().toInt()
        room.numberOfSingleBeds = roomMap["numberOfSingleBeds"].toString().toInt()
        room.photoURI = roomMap["photoURI"].toString()
        room.status = roomMap["status"].toString()
        room.amenities = roomMap["amenities"] as HashMap<String, Boolean>
        return room
    }

    suspend fun getUserBookings(login: String): List<Booking> {
        val listOfBookings = model.findUserBookings(login)?.values ?: return listOf<Booking>()
        Log.i("got UserBookings:",  "${listOfBookings.toList()}")
        return listOfBookings.toList()
    }

    fun setBooking(booking: Booking) {
        model.booking = booking
        model.writeNewBooking()
    }

    fun setFavorite(roomId: String, login: String) {
        model.writeNewFavorite(roomId, login)
    }

    fun deleteFavorite(roomId: String, login: String) {
        model.deleteFavorite(roomId, login)
    }

    suspend fun loadListOfFavorites(login: String): MutableList<Room> {
        val mapOfRoomsId = model.getListOfFavorites(login)
        Log.i("Map of favorites Id's", mapOfRoomsId.toString())
        val listOfRooms = mutableListOf<Room>()
        mapOfRoomsId?.forEach { (roomId, favoriteId) ->
            listOfRooms.add(getRoomById(roomId))
        }
        Log.i("Map of favorites", listOfRooms.toString())
        return listOfRooms
    }

    suspend fun checkIsRoomInFavorites(roomId: String, login: String): Boolean {
        //val listOfFavorites = loadListOfFavorites(login)
        model.getListOfFavorites(login)?.forEach { (favRoomId, favoriteId) ->
            Log.i(favRoomId, roomId)
            if(favRoomId == roomId)
                return true
        }
        return false
    }
}