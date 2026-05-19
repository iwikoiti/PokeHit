package com.example.pokehit.repository

import android.util.Log
import com.example.pokehit.api.ApiService
import com.example.pokehit.model.BasicPokemon
import java.io.IOException
import com.example.pokehit.api.PokemonResponse
import com.example.pokehit.api.PokemonSpeciesResponse
import com.example.pokehit.model.DetailedPokemon
import com.example.pokehit.model.PokemonStat

class PokemonRepository(
    private val apiService: ApiService
) {

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
        return try {
            Log.d("PokeRepo", "Loading details for pokemon $pokemonId from network")

            // основная информация
            val pokemonResponse = apiService.getPokemon(pokemonId)

            // описание
            val speciesResponse = try {
                apiService.getPokemonSpecies(pokemonId)
            } catch (e: Exception) {
                Log.e("PokeRepo", "Failed to load species for $pokemonId: ${e.message}")
                null
            }
            convertToDetailed(pokemonResponse, speciesResponse)

        } catch (e: IOException) {
            Log.e("PokeRepo", "Network error loading details: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("PokeRepo", "Error loading details: ${e.message}")
            null
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
}