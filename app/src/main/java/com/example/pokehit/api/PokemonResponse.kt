package com.example.pokehit.api

import com.google.gson.annotations.SerializedName

data class PokemonResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("height") val height: Int,      // в дециметрах
    @SerializedName("weight") val weight: Int,      // в гектограммах
    @SerializedName("base_experience") val baseExperience: Int,
    @SerializedName("types") val types: List<PokemonType>,
    @SerializedName("stats") val stats: List<PokemonStat>,
    @SerializedName("abilities") val abilities: List<PokemonAbility>,
    @SerializedName("sprites") val sprites: PokemonSprites,
    @SerializedName("species") val species: NamedApiResource
)

// Типы покемона
data class PokemonType(
    @SerializedName("slot") val slot: Int,
    @SerializedName("type") val type: NamedApiResource
)

// Характеристики
data class PokemonStat(
    @SerializedName("base_stat") val baseStat: Int,
    @SerializedName("effort") val effort: Int,
    @SerializedName("stat") val stat: NamedApiResource
)

// Способности
data class PokemonAbility(
    @SerializedName("is_hidden") val isHidden: Boolean,
    @SerializedName("slot") val slot: Int,
    @SerializedName("ability") val ability: NamedApiResource
)

// Все изображения
data class PokemonSprites(
    @SerializedName("front_default") val frontDefault: String?,
    @SerializedName("back_default") val backDefault: String?,
    @SerializedName("front_shiny") val frontShiny: String?,
    @SerializedName("back_shiny") val backShiny: String?,

    @SerializedName("other") val other: OtherSprites?
)

data class OtherSprites(
    @SerializedName("official-artwork") val officialArtwork: OfficialArtwork?,
    @SerializedName("home") val home: HomeSprites?,
    @SerializedName("dream_world") val dreamWorld: DreamWorld?
)

data class OfficialArtwork(
    @SerializedName("front_default") val frontDefault: String?
)

data class HomeSprites(
    @SerializedName("front_default") val frontDefault: String?,
    @SerializedName("front_shiny") val frontShiny: String?
)

data class DreamWorld(
    @SerializedName("front_default") val frontDefault: String?
)

// Общий тип для API-ресурсов (имя + ссылка)
data class NamedApiResource(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)