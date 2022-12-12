package by.yaugesha.hotelbooking.DataClasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Sort(
    var minPrice: Int = 0,
    var maxPrice: Int = 0,
    var mapOfAmenities: HashMap<String, Boolean> = hashMapOf(
        "Free parking" to false,
        "Hotel bar" to false,
        "Spa" to false,
        "Departure from airport" to false,
        "Casino" to false,
        "Swimming pool" to false,
        "Cribs" to false,
        "Laundry" to false,
        "Business services" to false,
        "Outdoor space" to false,
        "Wi-fi in lobby" to false,
        "Restaurant" to false,
        "Gym" to false,
        "Kitchen" to false,
        "Bathroom" to false,
        "Wi-fi in room" to false,
        "Wheelchair accessible" to false,
        "TV in room" to false,
        "AC unit" to false,
        "Pet friendly" to false,
        "Balcony" to false,
        "No smoking" to false,
        "Breakfast included" to false,
        "Noise isolation" to false
    ),
    var numberOfSingleBeds: Int = 0,
    var numberOfDoubleBeds: Int = 0,
    var status: String = "",
    var isEdited: Boolean = false
): Parcelable