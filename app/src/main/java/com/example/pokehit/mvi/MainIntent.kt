package com.example.pokehit.mvi

sealed class MainIntent {
    object LoadPokemons : MainIntent()

    // пагинация при скролле
    object LoadNextPage : MainIntent()

    // Добавить/удалить из избранного
    data class ToggleFavorite(val pokemonId: Int) : MainIntent()

    // Обновить поисковый запрос
    data class UpdateSearchQuery(val query: String) : MainIntent()

    // Обновить выбранные типы для фильтрации
    data class UpdateSelectedTypes(val types: Set<String>) : MainIntent()

    // Сбросить все фильтры
    object ResetFilters : MainIntent()

    // Ручное обновление
    object Refresh : MainIntent()
}