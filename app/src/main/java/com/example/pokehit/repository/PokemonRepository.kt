package com.example.pokehit.repository

import android.util.Log
import com.example.pokehit.api.ApiService
import com.example.pokehit.model.BasicPokemon
import java.io.IOException
import com.example.pokehit.api.PokemonResponse
import com.example.pokehit.api.PokemonSpeciesResponse
import com.example.pokehit.database.AppDatabase
import com.example.pokehit.model.DetailedPokemon
import com.example.pokehit.model.PokemonStat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PokemonRepository(
    private val apiService: ApiService,
    private val database: AppDatabase
) {
    private val cache = mutableMapOf<Int, DetailedPokemon>()
    private val cacheMutex = Mutex()
    private companion object {
        private const val MAX_CACHE_SIZE = 100
    }
    private var allPokemonListCache: List<BasicPokemon>? = null

    //Список покемонов с пагинацией
    suspend fun loadPokemonList(limit: Int = 50, offset: Int = 0): List<BasicPokemon> {
        return try {
            Log.d("PokeRepo", "Loading pokemon list: limit=$limit, offset=$offset")

            val response = apiService.getPokemonList(limit, offset)

            val basicPokemons = response.results.map { item ->
                BasicPokemon(
                    id = item.id,
                    name = item.name,
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${item.id}.png",
                    types = emptyList()  // типы пока пустые
                )
            }

            Log.d("PokeRepo", "Loaded ${basicPokemons.size} pokemons")
            basicPokemons

        } catch (e: IOException) {
            Log.e("PokeRepo", "Network error: ${e.message}")
            emptyList()
        } catch (e: Exception) {
            Log.e("PokeRepo", "Unknown error: ${e.message}")
            emptyList()
        }
    }

    //Детальная информация о покемоне
    suspend fun loadPokemonDetails(pokemonId: Int): DetailedPokemon? {
        //Проверяем кэш
        cacheMutex.withLock {
            cache[pokemonId]?.let {
                Log.d("PokeRepo", "Returning cached details for $pokemonId")
                return it
            }
        }

        //Загружаем из сети
        return try {
            Log.d("PokeRepo", "Loading details for pokemon $pokemonId from network")

            val pokemonResponse = apiService.getPokemon(pokemonId)
            val speciesResponse = try {
                apiService.getPokemonSpecies(pokemonId)
            } catch (e: Exception) {
                Log.e("PokeRepo", "Failed to load species for $pokemonId: ${e.message}")
                null
            }
            val detailedPokemon = convertToDetailed(pokemonResponse, speciesResponse)

            //Сохраняем в кэш
            cacheMutex.withLock {
                if (cache.size >= MAX_CACHE_SIZE) {
                    // Удаляем самый старый элемент
                    val oldestKey = cache.keys.firstOrNull()
                    if (oldestKey != null) {
                        cache.remove(oldestKey)
                        Log.d("PokeRepo", "Cache full, removed oldest: $oldestKey")
                    }
                }
                cache[pokemonId] = detailedPokemon
                Log.d("PokeRepo", "Cached details for $pokemonId, cache size: ${cache.size}")
            }

            detailedPokemon
        } catch (e: IOException) {
            Log.e("PokeRepo", "Network error loading details: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("PokeRepo", "Error loading details: ${e.message}")
            null
        }
    }
    suspend fun clearCache() {
        cacheMutex.withLock {
            cache.clear()
            Log.d("PokeRepo", "Cache cleared")
        }
    }

    //Конвертируем API-ответ в модель DetailedPokemon
    private fun convertToDetailed(
        response: PokemonResponse,
        speciesResponse: PokemonSpeciesResponse?
    ): DetailedPokemon {
        // основное изображение
        val imageUrl = response.sprites.other?.officialArtwork?.frontDefault
            ?: response.sprites.frontDefault
            ?: ""

        // базовая информация
        val basicInfo = BasicPokemon(
            id = response.id,
            name = response.name,
            imageUrl = imageUrl,
            types = response.types.map { it.type.name }
        )

        // характеристики
        val stats = response.stats.map { stat ->
            PokemonStat(
                name = stat.stat.name,
                baseStat = stat.baseStat,
                effort = stat.effort
            )
        }

        // Описание
        val description = speciesResponse?.flavorTextEntries
            ?.firstOrNull { it.language.name == "en" }
            ?.getDescription()

        return DetailedPokemon(
            basicInfo = basicInfo,
            height = response.height,
            weight = response.weight,
            baseExperience = response.baseExperience,
            stats = stats,
            abilities = response.abilities.map { it.ability.name },
            description = description,
            backImageUrl = response.sprites.backDefault,
            frontShinyUrl = response.sprites.frontShiny,
            backShinyUrl = response.sprites.backShiny,
            homeImageUrl = response.sprites.other?.home?.frontDefault,
            dreamWorldImageUrl = response.sprites.other?.dreamWorld?.frontDefault
        )
    }

    //Добавить покемона в избранное
    suspend fun addToFavorites(pokemonId: Int) {
        try {
            database.favoritePokemonDao().addToFavorites(
                com.example.pokehit.database.FavoritePokemonEntity(pokemonId)
            )
            Log.d("PokeRepo", "Added $pokemonId to favorites")
        } catch (e: Exception) {
            Log.e("PokeRepo", "Error adding to favorites: ${e.message}")
        }
    }

    //Удалить покемона из избранного
    suspend fun removeFromFavorites(pokemonId: Int) {
        try {
            database.favoritePokemonDao().removeFromFavorites(pokemonId)
            Log.d("PokeRepo", "Removed $pokemonId from favorites")
        } catch (e: Exception) {
            Log.e("PokeRepo", "Error removing from favorites: ${e.message}")
        }
    }

    //Проверить, находится ли покемон в избранном
    suspend fun isFavorite(pokemonId: Int): Boolean {
        return try {
            val count = database.favoritePokemonDao().isFavorite(pokemonId)
            count > 0
        } catch (e: Exception) {
            Log.e("PokeRepo", "Error checking favorite: ${e.message}")
            false
        }
    }

    //Получить Flow со списком избранных ID
    fun getFavoritesFlow(): Flow<Set<Int>> {
        return database.favoritePokemonDao().getFavoritesFlow()
            .map { entities -> entities.map { it.pokemonId }.toSet() }
    }

    //Получить список избранных покемонов
    suspend fun getFavoritePokemonIds(): List<Int> {
        return try {
            database.favoritePokemonDao().getAllFavorites().map { it.pokemonId }
        } catch (e: Exception) {
            Log.e("PokeRepo", "Error getting favorites: ${e.message}")
            emptyList()
        }
    }

    //Загрузить всех избранных покемонов (с деталями)
    suspend fun loadFavoritePokemons(): List<DetailedPokemon> {
        val favoriteIds = getFavoritePokemonIds()
        Log.d("PokeRepo", "Loading ${favoriteIds.size} favorite pokemons")

        return favoriteIds.mapNotNull { id ->
            loadPokemonDetails(id)  // Использует кэш
        }
    }

    //Загрузка и кэш покемонов для поиска/фильтрации
    suspend fun getAllPokemonBasic(): List<BasicPokemon> {
        // Если уже загружены, возвращаем из кэша
        if (allPokemonListCache != null) {
            return allPokemonListCache!!
        }

        Log.d("PokeRepo", "Loading all basic pokemon (this may take a while)")

        val limit = 1000
        val response = apiService.getPokemonList(limit = limit, offset = 0)

        val basicPokemons = response.results.map { item ->
            BasicPokemon(
                id = item.id,
                name = item.name,
                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${item.id}.png",
                types = emptyList()  // Типы пока пустые
            )
        }

        allPokemonListCache = basicPokemons
        Log.d("PokeRepo", "Cached ${basicPokemons.size} basic pokemons")

        return basicPokemons
    }

    //Поиск покемонов по имени
    suspend fun searchPokemon(query: String): List<BasicPokemon> {
        if (query.isBlank()) {
            return getAllPokemonBasic()
        }

        val allPokemon = getAllPokemonBasic()
        val lowerQuery = query.lowercase()

        val results = allPokemon.filter { pokemon ->
            pokemon.name.contains(lowerQuery)
        }

        Log.d("PokeRepo", "Search '$query': found ${results.size} results")
        return results
    }

    //Фильтрация покемонов по типам
    suspend fun filterPokemonByTypes(selectedTypes: Set<String>): List<BasicPokemon> {
        if (selectedTypes.isEmpty()) {
            return getAllPokemonBasic()
        }

        Log.d("PokeRepo", "Filtering by types: $selectedTypes")

        val allPokemon = getAllPokemonBasic()
        val filtered = mutableListOf<BasicPokemon>()

        for (pokemon in allPokemon) {
            // Загружаем детали (использует кэш, если уже загружены)
            val details = loadPokemonDetails(pokemon.id)
            if (details != null) {
                val hasMatchingType = details.basicInfo.types.any { type ->
                    selectedTypes.contains(type)
                }
                if (hasMatchingType) {
                    filtered.add(pokemon)
                }
            }
        }

        Log.d("PokeRepo", "Filter result: ${filtered.size} pokemons")
        return filtered
    }

    //Поиск + фильтрация
    suspend fun searchAndFilter(
        query: String,
        selectedTypes: Set<String>
    ): List<BasicPokemon> {
        // фильтруем по типам
        val typeFiltered = if (selectedTypes.isNotEmpty()) {
            filterPokemonByTypes(selectedTypes)
        } else {
            getAllPokemonBasic()
        }

        // фильтруем по имени
        if (query.isBlank()) {
            return typeFiltered
        }

        val lowerQuery = query.lowercase()
        return typeFiltered.filter { it.name.contains(lowerQuery) }
    }
}