package by.yaugesha.hotelbooking.DataClasses

data class Booking(
    val user: String,            //login
    val room: String,            //roomId
    var checkInDate: String,
    var checkOutDate: String,
    var amenities: MutableList<String>,
    var cost: Int,
)