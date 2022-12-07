package by.yaugesha.hotelbooking.DataClasses

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class Search (
    var location: String = "",
    var guests: Int = 0,
    var checkInDate: Date = Date(),
    var checkOutDate: Date = Date(),
    var rooms: Int = 0
 ): Parcelable

class SearchType: NavType<Search>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): Search? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): Search {
        return Gson().fromJson(value, Search::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: Search) {
        bundle.putParcelable(key, value)
    }

}