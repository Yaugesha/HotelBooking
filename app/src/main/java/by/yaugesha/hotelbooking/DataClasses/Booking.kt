package by.yaugesha.hotelbooking.DataClasses

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime

@Parcelize
data class Booking(
    val bookingId: String = "",
    val user: String = "",            //login
    val room: String = "",            //roomId
    var amountOfRooms: Int = 0,
    var checkInDate: String = "",
    var checkOutDate: String = "",
    var guests: Int = 0,
    var status: String = "active",
    var cost: Int = 0,
    val date: String = ""
): Parcelable

class BookingType: NavType<Booking>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): Booking? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): Booking {
        return Gson().fromJson(value, Booking::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: Booking) {
        bundle.putParcelable(key, value)
    }

}