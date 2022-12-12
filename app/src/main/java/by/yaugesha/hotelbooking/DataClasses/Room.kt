package by.yaugesha.hotelbooking.DataClasses

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Room(
    var roomId : String = "",
    var hotelID: String = "",
    var peopleCapacity:Int = 0,
    var price: Int = 0,
    var numberOfRooms: Int = 0,//in this hotel room
    var square: Int = 0,
    var amountOfRooms: Int = 0,//in hotel
    var numberOfDoubleBeds: Int = 0,
    var numberOfSingleBeds: Int = 0,
    var photoURI: String = "",
    var status: String = "active",
    var amenities: HashMap<String, Boolean> = hashMapOf()
): Parcelable

class RoomType: NavType<Room>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): Room? {
           return bundle.getParcelable(key)
         }

    override fun parseValue(value: String): Room {
           return Gson().fromJson(value, Room::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: Room) {
        bundle.putParcelable(key, value)
    }

}
