package com.example.pokehit.mvi

sealed class FavoritesIntent {
    object LoadFavorites : FavoritesIntent()
    data class ToggleFavorite(val pokemonId: Int) : FavoritesIntent()
    data class RemoveFromFavorites(val pokemonId: Int) : FavoritesIntent()
    object Refresh : FavoritesIntent()
}