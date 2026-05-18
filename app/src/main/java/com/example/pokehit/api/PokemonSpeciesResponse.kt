package com.example.pokehit.api

import com.google.gson.annotations.SerializedName
//Получаем описание покемонов
data class PokemonSpeciesResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry>
)

data class FlavorTextEntry(
    @SerializedName("flavor_text") val flavorText: String,
    @SerializedName("language") val language: NamedApiResource
) {
    fun getDescription(): String? {
        return if (language.name == "en") {
            flavorText
                .replace("\n", " ")      // заменяем переносы строк на пробелы
                .replace("\u000c", " ")  // заменяем символы формы страницы
                .trim()
        } else null
    }
}