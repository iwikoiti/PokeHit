package com.example.pokehit.api

import com.google.gson.annotations.SerializedName

data class PokemonListResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("previous") val previous: String?,
    @SerializedName("results") val results: List<PokemonListItem>
)

data class PokemonListItem(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
) {
    // Извлекаем ID из URL (например: "https://pokeapi.co/api/v2/pokemon/25/")
    val id: Int
        get() = url.trimEnd('/').split("/").last().toInt()
}