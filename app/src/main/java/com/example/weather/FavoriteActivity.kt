package com.example.weather

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.weather.data.Favorite
import com.example.weather.model.FavoriteViewModel
import com.example.weather.ui.theme.WeatherTheme
import kotlinx.coroutines.launch

class FavoriteActivity: ComponentActivity() {

    private val favorites: List<Favorite> = emptyList()
    private val mutableFavorite = mutableStateOf(favorites)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val city = intent.getStringExtra("city")

        // we retrieve the 'darkMode' value passed from MainActivity. The default value is 'false' if 'darkMode' was not found in the intent extras.
        val darkMode = intent.getBooleanExtra("darkMode", false)

        // An instance of FavoriteViewModel
        val viewModel: FavoriteViewModel by viewModels()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {it ->
                    // Update UI elements
                    if (it.allFavorites.isNotEmpty()) {
                        mutableFavorite.value = it.allFavorites
                    }
                }
            }
        }

        setContent {
            WeatherTheme (
                darkTheme = darkMode
            ) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Favorite(city!!, onRemove = {
                        viewModel.deleteProj(it)
                    })
                }
            }
        }
    }

    @Composable
    fun Favorite(name: String, onRemove: (name: String) -> Unit, modifier: Modifier = Modifier) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            SmallTopAppBarExample(name, onRemove)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SmallTopAppBarExample(
        name: String,
        onRemove: (name: String) -> Unit
    ) {
        val favorites by mutableFavorite

        Scaffold(
            topBar = {
                TopAppBar(
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text("My Favorites")
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            finish()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            LazyColumn (
                modifier = Modifier
                    .padding(innerPadding),
            ) {
                favorites.size?.let {
                    items(count = it) { index ->
                        val item = favorites.get(index)
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    val intent = Intent()
                                    intent.putExtra("city", item.name)
                                    setResult(RESULT_OK, intent)
                                    finish()
                                })
                        ) {
                            Text(text = "${item.name}", modifier = Modifier.padding(10.dp), fontSize = 14.sp)
                            Text(text = "${item.country}", modifier = Modifier.padding(10.dp).width(120.dp), fontSize = 14.sp)
                            Button(onClick = { onRemove(item.name) }, modifier = Modifier.padding(8.dp)) {
                                Text(text = "Remove")
                            }
                        }
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun FavoritePreview() {
        WeatherTheme {
            Favorite("Favorite city", onRemove = {})
        }
    }
}