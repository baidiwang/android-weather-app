package com.example.weather

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather.data.SuggestedActivitiesRepository
import com.example.weather.ui.theme.WeatherTheme
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException


class MainActivity: ComponentActivity() {
    data class Condition(val code: Int, val icon: String, val text: String)
    data class Current(
        val cloud: Float,
        val condition: Condition,
        val feelslike_c: Float,
        val feelslike_f: Float,
        val gust_kph: Float,
        val gust_mph: Float,
        val humidity: Float,
        val is_day: Float,
        val last_updated: String,
        val last_updated_epoch: Float,
        val precip_in: Float,
        val precip_mm: Float,
        val pressure_in: Float,
        val pressure_mb: Float,
        val temp_c: Float,
        val temp_f: Float,
        val uv: Float,
        val vis_km: Float,
        val vis_miles: Float,
        val wind_degree: Float,
        val wind_dir: String,
        val wind_kph: Float,
        val wind_mph: Float,
        val time: String,
    )
    data class Location(val country: String, val lat: Double, val localtime: String, val localtime_epoch: Int, val lon: Double, val name: String, val region: String, val tz_id: String)
    data class Forecastday(val date: String, val date_epoch: Int, val hour: List<Current>)
    data class Forecast(val forecastday: List<Forecastday>)
    data class Weather(val current: Current?, val location: Location?, val forecast: Forecast?)

    private val weather = Weather(null, null, null);
    private val forecast = Weather(null, null, null);
    private val favorite = Location("", 0.0, "", 0, 0.0, "", "", "");
    private val mutableWeather = mutableStateOf(weather)
    private val mutableForecast = mutableStateOf(forecast)
    private val mutableFavorite = mutableStateOf(favorite)
    private val mutableDarkMode = mutableStateOf(false)
    private val suggestedActivitiesRepository = SuggestedActivitiesRepository()

    private val city = "New York"
    private val mutableCity = mutableStateOf(city)

    private val HOST = "http://192.168.1.6:8080"

    private fun getFavorite() {
        val city by mutableCity
        val url2 = HOST + "/favorite/${city}"
        val request2 = Request.Builder()
            .url(url2)
            .method("GET", null)
            .build()

        OkHttpClient().newCall(request2)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println(e);
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        try {
                            val favoriteResp = Gson().fromJson(it, Location::class.java)
                            if (favoriteResp != null) {
                                mutableFavorite.value = favoriteResp
                            } else {
                                mutableFavorite.value = Location("", 0.0, "", 0, 0.0, "", "", "")
                            }
                        } catch (e: Exception) {
                            println(e)
                        }
                    } ?: run {
                        println("Empty response body.")
                    }
                }
            })
    }

    private fun getCurrentWeather() {
        // Fetch data
        val city by mutableCity
        val url = HOST + "/weather/current-weather?q=${city}"
        val request = Request.Builder()
            .url(url)
            .method("GET", null)
            .build()

        OkHttpClient().newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println(e);
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()

                    Log.d("WeatherApp", "Current weather response: $responseBody")

                    responseBody?.let {
                        try {
                            val weatherResp = Gson().fromJson(it, Weather::class.java)
                            mutableWeather.value = weatherResp

                            Log.d("WeatherApp", "Weather data parsed successfully")

                        } catch (e: Exception) {
                            println(e)
                        }
                    } ?: run {
                        println("Empty response body.")
                    }
                }
            })
    }

    private fun getForecastWeather() {
        val city by mutableCity
        val url1 = HOST + "/weather/forecast-weather?q=${city}"
        val request1 = Request.Builder()
            .url(url1)
            .method("GET", null)
            .build()

        OkHttpClient().newCall(request1)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println(e);
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        try {
                            val forecastResp = Gson().fromJson(it, Weather::class.java)
                            mutableForecast.value = forecastResp
                        } catch (e: Exception) {
                            println(e)
                        }
                    } ?: run {
                        println("Empty response body.")
                    }
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Used intent to be a dynamic route change
        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val city = data?.getStringExtra("city")
                if (city != null) {
                    mutableCity.value = city
                    getCurrentWeather()
                    getForecastWeather()
                    getFavorite()
                }
            }
        }

        setContent {
            WeatherTheme (
                darkTheme = mutableDarkMode.value
            ) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherApp("WeatherApp", setFavorite = { it, isFavorite ->
                        if (!isFavorite) {
                            val url = HOST + "/favorite/" + mutableCity.value
                            val request = Request.Builder()
                                .url(url)
                                .method("DELETE", null)
                                .build()
                            OkHttpClient().newCall(request)
                                .enqueue(object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        println(e);
                                    }

                                    override fun onResponse(call: Call, response: Response) {
                                        getFavorite()
                                    }
                                })
                        } else {
                            val favoriteBody = Gson().toJson(it)
                            val url = HOST + "/favorite"
                            val mediaType = "application/json; charset=utf-8".toMediaType()
                            val requestBody = favoriteBody.toString().toRequestBody(mediaType)
                            val request = Request.Builder()
                                .url(url)
                                .method("POST", requestBody)
                                .build()
                            OkHttpClient().newCall(request)
                                .enqueue(object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        println(e);
                                    }

                                    override fun onResponse(call: Call, response: Response) {
                                        getFavorite()
                                    }
                                })
                        }

                    }, onSearch = {
                        getCurrentWeather()
                        getForecastWeather()
                        getFavorite()
                    }, result = resultLauncher)
                }
            }
        }

        getCurrentWeather()
        getForecastWeather()
        getFavorite()
    }

    // Main page UI
    @Composable
    fun WeatherApp(
        name: String,
        setFavorite: (Location, isFavorite: Boolean) -> Unit,
        onSearch: () -> Unit,
        result: ActivityResultLauncher<Intent>?,
        modifier: Modifier = Modifier
    ) {
        val weather by mutableWeather
        val forecast by mutableForecast
        val favorite by mutableFavorite
        val city by mutableCity

        // dark model feature, 'checked' is a state that represents the current value of the dark mode switch.
        var checked by remember { mutableDarkMode }

        // suggested activities feature
        var activities: List<String>? = listOf<String>()
        weather.current?.condition?.text?.let {
            activities = suggestedActivitiesRepository.getSuggestedActivitiesByType(
                it
            )
        }

        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        )
        {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = checked,
                        onCheckedChange = {
                            checked = it
                        }
                    )
                    Text(text = "Dark Mode", modifier = Modifier.padding(horizontal = 10.dp))
                }
                Button(onClick = {
                    val intent = Intent(this@MainActivity,FavoriteActivity::class.java)
                    intent.putExtra("city", city)
                    intent.putExtra("darkMode", checked)

                    result?.launch(intent)
                }, modifier = modifier.padding(8.dp)) {
                    Text(text = "My Favorite")
                }
            }
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            )

            // Menu UI
            {
                TextField(
                    value = city,
                    onValueChange = { mutableCity.value = it },
                    label = { Text("City") },
                    modifier = modifier.width(250.dp)
                )
                Button(onClick = { onSearch() }, modifier = modifier.padding(8.dp)) {
                    Text(text = "Search")
                }
            }

            // Real-time weather UI
            Text(text = "My Location", modifier = modifier.padding(10.dp), fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "${weather.location?.name}", modifier = modifier.padding(5.dp), fontSize = 16.sp)
                if ("".equals(favorite.name)) {
                    IconButton(onClick = {
                        setFavorite(weather.location!!, true)
                    }) {
                        Icon(
                            Icons.Rounded.FavoriteBorder,
                            contentDescription = null
                        )
                    }
                } else {
                    IconButton(onClick = {
                        setFavorite(weather.location!!, false)
                    }) {
                        Icon(
                            Icons.Rounded.Favorite,
                            contentDescription = null
                        )
                    }
                }
            }
            Text(text = "${weather.current?.temp_c}°", modifier = modifier.padding(10.dp), fontSize = 60.sp, fontWeight = FontWeight.Bold)
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            )
            {
                Box(
                    modifier = Modifier
                        .shadow(2.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = modifier
                            .width(180.dp)
                            .height(130.dp)
                    )
                    {
                        Text(
                            text = "Wind speed",
                            modifier = modifier.padding(10.dp),
                            fontSize = 25.sp
                        )
                        Text(
                            text = "${weather.current?.wind_mph}",
                            modifier = modifier.padding(10.dp),
                            fontSize = 18.sp
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .shadow(2.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = modifier
                            .width(180.dp)
                            .height(130.dp)
                    )
                    {
                        Text(
                            text = "Pressure",
                            modifier = modifier.padding(10.dp),
                            fontSize = 25.sp
                        )
                        Text(
                            text = "${weather.current?.pressure_mb}",
                            modifier = modifier.padding(10.dp),
                            fontSize = 18.sp
                        )
                    }
                }
            }
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            )
            {
                Box(
                    modifier = Modifier
                        .shadow(2.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = modifier
                            .width(180.dp)
                            .height(130.dp)
                    )
                    {
                        Text(text = "Precipitation amount", modifier = modifier.padding(10.dp), fontSize = 25.sp, textAlign = TextAlign.Center)
                        Text(text = "${weather.current?.precip_mm}", modifier = modifier.padding(10.dp), fontSize = 18.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .shadow(2.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = modifier
                            .width(180.dp)
                            .height(130.dp)
                    )
                    {
                        Text(
                            text = "Humidity",
                            modifier = modifier.padding(10.dp),
                            fontSize = 25.sp
                        )
                        Text(
                            text = "${weather.current?.humidity}",
                            modifier = modifier.padding(10.dp),
                            fontSize = 18.sp
                        )
                    }
                }
            }

            // Suggested Activities UI
            if (activities != null) {
                Text(
                    text = "Suggested Activities",
                    modifier = modifier.padding(10.dp),
                    fontSize = 25.sp
                )
                for (item in activities!!) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = modifier
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = item, modifier = modifier.padding(horizontal = 10.dp, vertical = 2.dp), fontSize = 16.sp)
                    }
                }
            }

            // Forecast UI
            Text(
                text = "Today is ${forecast.current?.condition?.text}",
                modifier = modifier.padding(10.dp),
                fontSize = 25.sp
            )
            if (forecast.forecast !== null && forecast.forecast!!.forecastday != null) {
                for (item in forecast.forecast?.forecastday?.get(0)?.hour!!) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = "${item.time.split(" ")[1]}", modifier = modifier.padding(10.dp), fontSize = 14.sp)
                        Text(text = "${item.temp_c}°", modifier = modifier.padding(10.dp), fontSize = 14.sp)
                        AsyncImage(
                            model = "https:${item.condition.icon}",
                            contentDescription = null,
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp),
                        )
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun WeatherAppPreview() {
        WeatherTheme {
            WeatherApp("WeatherApp", setFavorite = { it, isFavorite -> {}}, onSearch = {}, result = null)
        }
    }
}