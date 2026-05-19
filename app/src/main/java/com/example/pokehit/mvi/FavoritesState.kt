package com.example.pokehit.mvi

import com.example.pokehit.model.DetailedPokemon

data class FavoritesState(
    val favorites: List<DetailedPokemon> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)