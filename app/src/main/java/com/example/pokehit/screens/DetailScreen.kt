package com.example.pokehit.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.pokehit.R
import com.example.pokehit.mvi.DetailIntent
import com.example.pokehit.mvi.DetailState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    pokemonId: Int,
    state: DetailState,
    onIntent: (DetailIntent) -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(pokemonId) {
        onIntent(DetailIntent.LoadPokemon(pokemonId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.pokemon?.basicInfo?.name?.replaceFirstChar { it.uppercase() }
                            ?: "Загрузка..."
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (state.pokemon != null) {
                        IconButton(
                            onClick = {
                                onIntent(DetailIntent.ToggleFavorite(state.pokemon.basicInfo.id))
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (state.isFavorite) R.drawable.baseline_favorite_24
                                    else R.drawable.outline_favorite_24
                                ),
                                contentDescription = "Favorite"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.error != null -> {
                    Text(
                        text = "Ошибка: ${state.error}",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                state.pokemon != null -> {
                    DetailContent(
                        pokemon = state.pokemon,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailContent(
    pokemon: com.example.pokehit.model.DetailedPokemon,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Главное изображение
        AsyncImage(
            model = pokemon.basicInfo.imageUrl,
            contentDescription = pokemon.basicInfo.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Типы
        Text(
            text = "Типы:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        // TODO: Добавить TypeChip с цветами
        Text(
            text = pokemon.basicInfo.types.joinToString(", "),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Вес и рост
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Физические характеристики",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Рост: ${pokemon.height / 10.0} м")
                Text(text = "Вес: ${pokemon.weight / 10.0} кг")
                Text(text = "Базовый опыт: ${pokemon.baseExperience}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Характеристики (статы)
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Базовые характеристики",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                pokemon.stats.forEach { stat ->
                    Text(
                        text = "${stat.name.replaceFirstChar { it.uppercase() }}: ${stat.baseStat}",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Способности
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Способности",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                pokemon.abilities.forEach { ability ->
                    Text(
                        text = ability.replaceFirstChar { it.uppercase() },
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Описание
        if (pokemon.description != null) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Описание",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = pokemon.description,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Галерея изображений
        Text(
            text = "Галерея",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Обычная форма
        if (pokemon.homeImageUrl != null) {
            AsyncImage(
                model = pokemon.homeImageUrl,
                contentDescription = "${pokemon.basicInfo.name} home",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Fit
            )
        }

        // Блестящая форма
        if (pokemon.frontShinyUrl != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Блестящий (Shiny):")
            AsyncImage(
                model = pokemon.frontShinyUrl,
                contentDescription = "${pokemon.basicInfo.name} shiny",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}