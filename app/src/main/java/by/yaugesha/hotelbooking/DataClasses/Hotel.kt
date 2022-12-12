package by.yaugesha.hotelbooking.DataClasses

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Hotel(
    var building: String = "",
    var checkIn: String = "",
    var checkOut: String = "",
    var city: String = "",
    var country: String = "",
    var hotelId: String = "",
    var name: String = "",
    var phone: String = "",
    var postCode: String = "",
    var status: String = "blocked",
    var street: String = "",
    var photoURI: String = "",
    var amenities: HashMap<String, Boolean> = hashMapOf()
): Parcelable

class HotelType: NavType<Hotel>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): Hotel? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): Hotel {
        return Gson().fromJson(value, Hotel::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: Hotel) {
        bundle.putParcelable(key, value)
    }

}