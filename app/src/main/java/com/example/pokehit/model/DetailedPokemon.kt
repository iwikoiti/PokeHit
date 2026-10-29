package com.example.pokehit.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailedPokemon(
    val basicInfo: BasicPokemon,
    val height: Int,
    val weight: Int,
    val baseExperience: Int,
    val stats: List<PokemonStat>,
    val abilities: List<String>,
    val description: String?,
    val homeImageUrl: String?
) : Parcelable

@Parcelize
data class PokemonStat(
    val name: String,
    val baseStat: Int,
    val effort: Int
) : Parcelable
