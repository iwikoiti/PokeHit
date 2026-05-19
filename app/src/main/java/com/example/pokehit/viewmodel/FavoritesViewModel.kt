package com.example.pokehit.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokehit.mvi.FavoritesIntent
import com.example.pokehit.mvi.FavoritesState
import com.example.pokehit.repository.PokemonRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesState())
    val state = _state.asStateFlow()

    private val _intent = MutableSharedFlow<FavoritesIntent>()

    init {
        collectIntents()
        loadFavorites()
        observeFavorites()
    }

    fun sendIntent(intent: FavoritesIntent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }

    private fun collectIntents() {
        viewModelScope.launch {
            _intent.collect { intent ->
                when (intent) {
                    is FavoritesIntent.LoadFavorites -> loadFavorites()
                    is FavoritesIntent.ToggleFavorite -> toggleFavorite(intent.pokemonId)
                    is FavoritesIntent.RemoveFromFavorites -> removeFromFavorites(intent.pokemonId)
                    is FavoritesIntent.Refresh -> refresh()
                }
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val favoritePokemons = repository.loadFavoritePokemons()
                val favoriteIds = favoritePokemons.map { it.basicInfo.id }.toSet()

                _state.update {
                    it.copy(
                        favorites = favoritePokemons,
                        favoriteIds = favoriteIds,
                        isLoading = false
                    )
                }

                Log.d("FavoritesVM", "Loaded ${favoritePokemons.size} favorites")
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
                Log.e("FavoritesVM", "Error loading favorites", e)
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getFavoritesFlow().collect { favoriteIds ->
                // Обновляем только ID, полные данные перезагружаем отдельно
                _state.update { it.copy(favoriteIds = favoriteIds) }

                // Если список избранного изменился, перезагружаем
                loadFavorites()
            }
        }
    }

    private fun toggleFavorite(pokemonId: Int) {
        viewModelScope.launch {
            if (_state.value.favoriteIds.contains(pokemonId)) {
                repository.removeFromFavorites(pokemonId)
            } else {
                repository.addToFavorites(pokemonId)
            }
        }
    }

    private fun removeFromFavorites(pokemonId: Int) {
        viewModelScope.launch {
            repository.removeFromFavorites(pokemonId)
        }
    }

    private fun refresh() {
        loadFavorites()
    }
}