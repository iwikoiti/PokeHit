package com.example.pokehit

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.pokehit.api.ApiClient
import com.example.pokehit.database.AppDatabase
import com.example.pokehit.database.FavoritePokemonEntity
import com.example.pokehit.model.BasicPokemon
import com.example.pokehit.model.DetailedPokemon
import com.example.pokehit.model.PokemonStat
import com.example.pokehit.repository.PokemonRepository
import com.example.pokehit.ui.theme.PokeHitTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeHitTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            Log.d("PokeRepoTest", "=== ТЕСТ 4.4: Избранное ===")

            val repository = PokemonRepository(
                ApiClient.apiService,
                AppDatabase.getDatabase(this@MainActivity)
            )

            // 1. Добавляем в избранное
            repository.addToFavorites(25)  // Пикачу
            repository.addToFavorites(132) // Дитто
            Log.d("PokeRepoTest", "Добавили Пикачу и Дитто")

            // 2. Проверяем статус
            val isPikachuFavorite = repository.isFavorite(25)
            val isDittoFavorite = repository.isFavorite(132)
            val isBulbasaurFavorite = repository.isFavorite(1)
            Log.d("PokeRepoTest", "Пикачу в избранном: $isPikachuFavorite")
            Log.d("PokeRepoTest", "Дитто в избранном: $isDittoFavorite")
            Log.d("PokeRepoTest", "Бульбазавр в избранном: $isBulbasaurFavorite")

            // 3. Получаем список ID
            val favoriteIds = repository.getFavoritePokemonIds()
            Log.d("PokeRepoTest", "Избранные ID: $favoriteIds")

            // 4. Удаляем Ditto
            repository.removeFromFavorites(132)
            val updatedIds = repository.getFavoritePokemonIds()
            Log.d("PokeRepoTest", "После удаления Ditto: $updatedIds")

            // 5. Загружаем детали избранных
            val favoritePokemons = repository.loadFavoritePokemons()
            Log.d("PokeRepoTest", "Детали избранных: ${favoritePokemons.size} покемонов")
            favoritePokemons.forEach { pokemon ->
                Log.d("PokeRepoTest", "  - ${pokemon.basicInfo.name} (${pokemon.basicInfo.types})")
            }
        }

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PokeHitTheme {
        Greeting("Android")
    }
}