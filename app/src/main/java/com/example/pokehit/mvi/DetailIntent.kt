package com.example.pokehit.mvi

sealed class DetailIntent {
    data class LoadPokemon(val pokemonId: Int) : DetailIntent()
    data class ToggleFavorite(val pokemonId: Int) : DetailIntent()
}