package com.example.pokehit.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokehit.model.BasicPokemon
import com.example.pokehit.mvi.MainIntent
import com.example.pokehit.mvi.MainState
import com.example.pokehit.repository.PokemonRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    val repository: PokemonRepository
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 50  //50 покемонов на страницу
        private const val SEARCH_DEBOUNCE = 500L  //Задержка для поиска
    }

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    private val _intent = MutableSharedFlow<MainIntent>()

    private var loadJob: Job? = null
    private var searchJob: Job? = null

    init {
        collectIntents()
        sendIntent(MainIntent.LoadPokemons)
    }

    fun sendIntent(intent: MainIntent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }

    private fun collectIntents() {
        viewModelScope.launch {
            _intent.collect { intent ->
                when (intent) {
                    is MainIntent.LoadPokemons -> loadPokemons()
                    is MainIntent.LoadNextPage -> loadNextPage()
                    is MainIntent.ToggleFavorite -> toggleFavorite(intent.pokemonId)
                    is MainIntent.UpdateSearchQuery -> updateSearchQuery(intent.query)
                    is MainIntent.UpdateSelectedTypes -> updateSelectedTypes(intent.types)
                    is MainIntent.ResetFilters -> resetFilters()
                    is MainIntent.Refresh -> refresh()
                }
            }
        }
    }

    private fun loadPokemons() {
        if (_state.value.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val pokemons = repository.loadPokemonList(
                    limit = PAGE_SIZE,
                    offset = 0
                )

                _state.update {
                    it.copy(
                        allPokemons = pokemons,
                        filteredPokemons = applyFilters(pokemons, it.searchQuery, it.selectedTypes),
                        isLoading = false,
                        currentPage = 1,
                        totalLoaded = pokemons.size,
                        hasMorePages = pokemons.size == PAGE_SIZE
                    )
                }

                loadFavorites()

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error loading pokemons"
                    )
                }
                Log.e("MainViewModel", "Error loading pokemons", e)
            }
        }
    }

    private fun loadNextPage() {
        val currentState = _state.value

        // Проверяем, можно ли загружать следующую страницу
        if (currentState.isLoadingMore || !currentState.hasMorePages || currentState.isLoading) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }

            try {
                val nextPage = currentState.currentPage + 1
                val offset = (nextPage - 1) * PAGE_SIZE

                val newPokemons = repository.loadPokemonList(
                    limit = PAGE_SIZE,
                    offset = offset
                )

                if (newPokemons.isNotEmpty()) {
                    val allPokemons = currentState.allPokemons + newPokemons

                    _state.update {
                        it.copy(
                            allPokemons = allPokemons,
                            filteredPokemons = applyFilters(allPokemons, it.searchQuery, it.selectedTypes),
                            isLoadingMore = false,
                            currentPage = nextPage,
                            totalLoaded = allPokemons.size,
                            hasMorePages = newPokemons.size == PAGE_SIZE
                        )
                    }
                } else {
                    _state.update { it.copy(isLoadingMore = false, hasMorePages = false) }
                }

            } catch (e: Exception) {
                _state.update { it.copy(isLoadingMore = false, error = e.message) }
                Log.e("MainViewModel", "Error loading next page", e)
            }
        }
    }

    //Избранное

    private fun loadFavorites() {
        viewModelScope.launch {
            repository.getFavoritesFlow().collect { favorites ->
                _state.update { it.copy(favorites = favorites) }
            }
        }
    }

    private fun toggleFavorite(pokemonId: Int) {
        viewModelScope.launch {
            val isFavorite = _state.value.favorites.contains(pokemonId)

            if (isFavorite) {
                repository.removeFromFavorites(pokemonId)
                Log.d("MainViewModel", "Removed $pokemonId from favorites")
            } else {
                repository.addToFavorites(pokemonId)
                Log.d("MainViewModel", "Added $pokemonId to favorites")
            }
        }
    }

    //Поиск и фильтрация

    private fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }

        // Debounce для поиска (чтобы не фильтровать при каждом символе)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE)
            applyCurrentFilters()
        }
    }

    private fun updateSelectedTypes(types: Set<String>) {
        _state.update { it.copy(selectedTypes = types) }
        applyCurrentFilters()
    }

    private fun resetFilters() {
        _state.update {
            it.copy(
                searchQuery = "",
                selectedTypes = emptySet()
            )
        }
        applyCurrentFilters()
    }

    private fun refresh() {
        // Очищаем кэш и загружаем заново
        viewModelScope.launch {
            repository.clearCache()
            loadPokemons()
        }
    }

    private fun applyCurrentFilters() {
        val currentState = _state.value
        val filtered = applyFilters(
            currentState.allPokemons,
            currentState.searchQuery,
            currentState.selectedTypes
        )
        _state.update { it.copy(filteredPokemons = filtered) }
    }

    private fun applyFilters(
        pokemons: List<BasicPokemon>,
        searchQuery: String,
        selectedTypes: Set<String>
    ): List<BasicPokemon> {
        var result = pokemons

        // Фильтр по поисковому запросу
        if (searchQuery.isNotBlank()) {
            val lowerQuery = searchQuery.lowercase()
            result = result.filter { it.name.contains(lowerQuery) }
        }

        return result
    }
}