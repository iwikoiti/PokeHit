package com.example.pokehit.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.pokehit.R
import com.example.pokehit.model.DetailedPokemon
import com.example.pokehit.model.PokemonStat
import com.example.pokehit.mvi.DetailIntent
import com.example.pokehit.mvi.DetailState
import com.example.pokehit.screens.component.TypeChip

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
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0),
        topBar = {
            TopAppBar(
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
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
                            contentDescription = "Назад"
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
                                contentDescription = "Избранное"
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
    pokemon: DetailedPokemon,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = pokemon.basicInfo.imageUrl,
            contentDescription = pokemon.basicInfo.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            pokemon.basicInfo.types.forEach { type ->
                TypeChip(type = type)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Рост")
                    Text(text = "${pokemon.height / 10.0} м")
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Вес")
                    Text(text = "${pokemon.weight / 10.0} кг")
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Базовый опыт")
                    Text(text = "${pokemon.baseExperience}")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Базовые характеристики",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                pokemon.stats.forEach { stat ->
                    StatItem(stat = stat)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(text = "• ")
                        Text(
                            text = formatStatName(ability),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Галерея",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        val galleryImages = mutableListOf<String>()
        pokemon.homeImageUrl?.let { galleryImages.add(it) }
        pokemon.homeFrontShinyUrl?.let { if (!galleryImages.contains(it)) galleryImages.add(it) }
        pokemon.frontShinyUrl?.let { if (!galleryImages.contains(it)) galleryImages.add(it) }
        pokemon.officialArtworkShinyUrl?.let { if (!galleryImages.contains(it)) galleryImages.add(it) }
        pokemon.basicInfo.imageUrl?.let { if (!galleryImages.contains(it)) galleryImages.add(it) }

        if (galleryImages.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(galleryImages) { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = pokemon.basicInfo.name,
                        modifier = Modifier
                            .width(200.dp)
                            .height(200.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (pokemon.description != null && pokemon.description.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}



@Composable
private fun StatItem(stat: PokemonStat) {
    val statName = formatStatName(stat.name)
    val maxStat = 255f
    val progress = (stat.baseStat.toFloat() / maxStat).coerceIn(0f, 1f)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = statName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = stat.baseStat.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
        )
    }
}

private fun formatStatName(name: String): String {
    return when (name.lowercase()) {
        "hp" -> "HP"
        "attack" -> "Attack"
        "defense" -> "Defense"
        "special-attack" -> "Special Attack"
        "special-defense" -> "Special Defense"
        "speed" -> "Speed"
        else -> name.replace("-", " ").replaceFirstChar { it.uppercase() }
    }
}
