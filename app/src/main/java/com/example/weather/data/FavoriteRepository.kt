package com.example.weather.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class FavoriteRepository {
    private val HOST = "http://192.168.1.6:8080"
    fun getFavoritesFlow(): Flow<List<Favorite>> = flow {
        try {
            val response = getFavorites()

            val responseBody = response.body?.string()
            responseBody?.let { body ->
                val itemType = object : TypeToken<ArrayList<Favorite>>() {}.type
                val favorites = Gson().fromJson<List<Favorite>>(body, itemType)
                emit(favorites)
            } ?: run {
                println("Empty response body.")
                emit(emptyList())
            }
        } catch (e: IOException) {
            println("Failed to fetch favorites: ${e.message}")
            emit(emptyList())
        }
    }

    suspend fun getFavorites(): Response = withContext(Dispatchers.IO) {
        val url = "$HOST/favorite"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IOException("Unexpected code $response")
        }

        response
    }

    fun deleteFavorite(name: String, callback: () -> Unit) {
        val url2 = HOST + "/favorite/" + name
        val request2 = Request.Builder()
            .url(url2)
            .method("DELETE", null)
            .build()

        OkHttpClient().newCall(request2)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println(e);
                }
                override fun onResponse(call: Call, response: Response) {
                    callback()
                }
            })
    }
}