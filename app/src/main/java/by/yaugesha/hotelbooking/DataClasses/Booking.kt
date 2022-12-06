package by.yaugesha.hotelbooking.DataClasses

import java.time.LocalDate
import java.time.LocalDateTime

data class Booking(
    val id: String = "",
    val user: String = "",            //login
    val room: String = "",            //roomId
    var amountOfRooms: Int = 0,
    var checkInDate: String = "",
    var checkOutDate: String = "",
    //var amenities: MutableList<String>,
    var cost: Int = 0,
    val date: LocalDate = LocalDate.now()
)