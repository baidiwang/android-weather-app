package com.example.weather.data

data class Favorite(val _id: String, var country: String, var lat: String, var lon: String, var name: String, var region: String, var tz_id: String) {
    companion object {
        val favorites = mutableListOf<Favorite>(
//            Favorite("111", "222", "333", "444", "555", "666", "777")
        )
    }
}