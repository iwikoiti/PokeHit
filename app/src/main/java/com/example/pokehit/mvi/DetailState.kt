package com.example.pokehit.mvi

import com.example.pokehit.model.DetailedPokemon

data class DetailState(
    val pokemon: DetailedPokemon? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)