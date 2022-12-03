package by.yaugesha.hotelbooking.DataClasses

data class User(
    var email: String = "",
    var login: String = "",
    var name: String = "",
    var password: String = "",
    var role: String = "user",
    var status: String = "active",
    var surname: String =""
)
