package com.example.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather.ui.theme.WeatherTheme
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class MainActivity : ComponentActivity() {
    data class Condition(val code: Int, val icon: String, val text: String)
    data class Current(
        val cloud: Int,
        val condition: Condition,
        val feelslike_c: Float,
        val feelslike_f: Float,
        val gust_kph: Float,
        val gust_mph: Float,
        val humidity: Int,
        val is_day: Int,
        val last_updated: String,
        val last_updated_epoch: Int,
        val precip_in: Int,
        val precip_mm: Int,
        val pressure_in: Float,
        val pressure_mb: Int,
        val temp_c: Float,
        val temp_f: Float,
        val uv: Int,
        val vis_km: Int,
        val vis_miles: Int,
        val wind_degree: Int,
        val wind_dir: String,
        val wind_kph: Float,
        val wind_mph: Float,
        val time: String,
    )
    data class Location(val country: String, val lat: Float, val localtime: String, val localtime_epoch: Int, val lon: Float, val name: String, val region: String, val tz_id: String)
    data class Forecastday(val date: String, val date_epoch: Int, val hour: List<Current>)
    data class Forecast(val forecastday: List<Forecastday>)
    data class Weather(val current: Current?, val location: Location?, val forecast: Forecast?)

    private val weather = Weather(null, null, null);
    private val forecast = Weather(null, null, null);
    private val mutableWeather = mutableStateOf(weather)
    private val mutableForecast = mutableStateOf(forecast)

    private val city = "New York"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android222")
                }
            }
        }

        val url = "http://192.168.0.104:8080/weather/current-weather?q=${city}"
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
                    responseBody?.let {
                        try {
                            val weatherResp = Gson().fromJson(it, Weather::class.java)
                            mutableWeather.value = weatherResp
                        } catch (e: Exception) {
                            println(e)
                        }


                    } ?: run {
                        println("Empty response body.")
                    }
                }
            })


        val url1 = "http://192.168.0.104:8080/weather/forecast-weather?q=${city}"
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

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        val weather by mutableWeather
        val forecast by mutableForecast

        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            )
            {
                Text(text = "New York")
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                )
                {
                    Button(onClick = { /*TODO*/ }, modifier = modifier.padding(8.dp)) {
                        Text(text = "Search")
                    }
                    Button(onClick = { /*TODO*/ }, modifier = modifier.padding(8.dp)) {
                        Text(text = "My Favorite")
                    }
                }
            }
            Text(text = "My Location", modifier = modifier.padding(10.dp), fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "${weather.location?.name}", modifier = modifier.padding(5.dp), fontSize = 16.sp)
                Icon(
                    Icons.Rounded.FavoriteBorder,
                    contentDescription = null
                )
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
                        Text(text = "Precipitation amount", modifier = modifier.padding(10.dp), fontSize = 25.sp)
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
            Text(
                text = "Today is ${forecast.current?.condition?.text}",
                modifier = modifier.padding(10.dp),
                fontSize = 25.sp
            )
            LazyColumn {
                forecast.forecast?.forecastday?.get(0)?.hour?.size?.let {
                    items(count = it) { index ->
                        val item = forecast.forecast?.forecastday?.get(0)?.hour!!.get(index)
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
                                modifier = Modifier.width(40.dp).height(40.dp),
                            )
                        }
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        WeatherTheme {
            Greeting("Android")
        }
    }
}