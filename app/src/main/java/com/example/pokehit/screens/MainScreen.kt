package com.example.pokehit.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokehit.R
import com.example.pokehit.mvi.MainIntent
import com.example.pokehit.mvi.MainState
import com.example.pokehit.screens.component.PokemonItem
import com.example.pokehit.screens.component.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    state: MainState,
    onIntent: (MainIntent) -> Unit,
    onPokemonClick: (Int) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Определяем, достигли ли конца списка (для пагинации)
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index == state.filteredPokemons.size - 1 &&
                    state.filteredPokemons.isNotEmpty() &&
                    !state.isLoadingMore &&
                    state.hasMorePages
        }
    }

    // Подгружаем следующую страницу при достижении конца
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onIntent(MainIntent.LoadNextPage)
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Верхняя панель с заголовком и кнопкой фильтра
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PokeHit",
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onFilterClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_filter_alt_24),
                    contentDescription = "Фильтр",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Поисковая строка
        SearchBar(
            query = state.searchQuery,
            onQueryChange = { query ->
                onIntent(MainIntent.UpdateSearchQuery(query))
            },
            onClear = {
                onIntent(MainIntent.UpdateSearchQuery(""))
            }

        )

        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            when {
                // Показываем прогресс при первой загрузке
                state.showProgress -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Показываем сообщение об ошибке
                state.error != null -> {
                    Text(
                        text = "Ошибка: ${state.error}",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                state.filteredPokemons.isEmpty() -> {
                    Text(
                        text = if (state.searchQuery.isNotBlank() || state.selectedTypes.isNotEmpty()) {
                            "Ничего не найдено по вашему запросу"
                        } else {
                            "Нет покемонов"
                        },
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Показываем список
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = state.filteredPokemons,
                            key = { it.id }
                        ) { pokemon ->
                            PokemonItem(
                                pokemon = pokemon,
                                isFavorite = state.favorites.contains(pokemon.id),
                                onToggleFavorite = {
                                    onIntent(MainIntent.ToggleFavorite(pokemon.id))
                                },
                                onClick = {
                                    onPokemonClick(pokemon.id)
                                }
                            )
                        }

                        if (state.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}