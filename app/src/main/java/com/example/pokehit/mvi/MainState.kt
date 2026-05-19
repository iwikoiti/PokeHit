package com.example.pokehit.mvi

import com.example.pokehit.model.BasicPokemon

data class MainState(
    // Данные
    val allPokemons: List<BasicPokemon> = emptyList(),
    val filteredPokemons: List<BasicPokemon> = emptyList(),

    // Избранное
    val favorites: Set<Int> = emptySet(),

    // Состояние UI
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,

    // Пагинация
    val currentPage: Int = 0,
    val hasMorePages: Boolean = true,
    val totalLoaded: Int = 0,

    // Поиск и фильтры
    val searchQuery: String = "",
    val selectedTypes: Set<String> = emptySet(),

    // Доступные типы для фильтрации
    val availableTypes: List<String> = listOf(
        "normal", "fire", "water", "electric", "grass", "ice",
        "fighting", "poison", "ground", "flying", "psychic",
        "bug", "rock", "ghost", "dragon", "dark", "steel", "fairy"
    ),
    val showFilterSheet: Boolean = false  // Для управления BottomSheet
) {
    //проверка, пуст ли результат
    val isEmpty: Boolean
        get() = !isLoading && filteredPokemons.isEmpty() && error == null

    // показывает ли прогресс при загрузке
    val showProgress: Boolean
        get() = isLoading && filteredPokemons.isEmpty()
}