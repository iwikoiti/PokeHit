package com.example.pokehit.repository

import android.util.Log
import com.example.pokehit.api.ApiService
import com.example.pokehit.model.BasicPokemon
import java.io.IOException

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
}