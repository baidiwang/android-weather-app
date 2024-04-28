package com.example.weather.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.Favorite
import com.example.weather.data.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val allFavorites: List<Favorite> = emptyList(),
    val loading: Boolean = false
)
class FavoriteViewModel: ViewModel() {
    // Holds the UI state for favorites, wrapped in a StateFlow.
    private val _uiState: MutableStateFlow<FavoritesUiState>
            = MutableStateFlow(FavoritesUiState())
    private val favoriteRepository = FavoriteRepository()

    val uiState: StateFlow<FavoritesUiState>
        get() = _uiState

    init{
        viewModelScope.launch {
            favoriteRepository.getFavoritesFlow().collect{favorites ->
                _uiState.update{
                    it.copy(allFavorites = favorites)
                }
            }
        }
    }

    // This function calls the repository to delete a favorite and updates the UI state accordingly.
    fun deleteProj(name: String){
        favoriteRepository.deleteFavorite(name, callback = {
            viewModelScope.launch {
                favoriteRepository.getFavoritesFlow().collect{favorites ->
                    _uiState.update{
                        it.copy(allFavorites = favorites)
                    }
                }
            }
        })
    }
}