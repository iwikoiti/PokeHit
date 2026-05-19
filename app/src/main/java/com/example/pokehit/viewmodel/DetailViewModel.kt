package com.example.pokehit.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokehit.mvi.DetailIntent
import com.example.pokehit.mvi.DetailState
import com.example.pokehit.repository.PokemonRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    private val _intent = MutableSharedFlow<DetailIntent>()

    init {
        collectIntents()
    }

    fun sendIntent(intent: DetailIntent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }

    private fun collectIntents() {
        viewModelScope.launch {
            _intent.collect { intent ->
                when (intent) {
                    is DetailIntent.LoadPokemon -> loadPokemon(intent.pokemonId)
                    is DetailIntent.ToggleFavorite -> toggleFavorite(intent.pokemonId)
                }
            }
        }
    }

    private fun loadPokemon(pokemonId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val pokemon = repository.loadPokemonDetails(pokemonId)
                val isFavorite = repository.isFavorite(pokemonId)

                _state.update {
                    it.copy(
                        pokemon = pokemon,
                        isFavorite = isFavorite,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
                Log.e("DetailViewModel", "Error loading pokemon", e)
            }
        }
    }

    private fun toggleFavorite(pokemonId: Int) {
        viewModelScope.launch {
            val currentFavorite = _state.value.isFavorite

            if (currentFavorite) {
                repository.removeFromFavorites(pokemonId)
            } else {
                repository.addToFavorites(pokemonId)
            }

            _state.update { it.copy(isFavorite = !currentFavorite) }
        }
    }
}