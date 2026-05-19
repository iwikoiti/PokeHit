package com.example.pokehit.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailedPokemon(
    val basicInfo: BasicPokemon,
    val height: Int, // в дециметрах
    val weight: Int, // в гектограммах
    val baseExperience: Int,
    val stats: List<PokemonStat>,
    val abilities: List<String>,
    val description: String?,
    val backImageUrl: String?,
    val frontShinyUrl: String?,
    val backShinyUrl: String?,
    val homeImageUrl: String?,
    val dreamWorldImageUrl: String?
) : Parcelable

@Parcelize
data class PokemonStat(
    val name: String, // "hp", "attack", "defense"
    val baseStat: Int,
    val effort: Int
) : Parcelable