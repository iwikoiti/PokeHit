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
import com.example.pokehit.mvi.MainIntent
import com.example.pokehit.repository.PokemonRepository
import com.example.pokehit.ui.theme.PokeHitTheme
import com.example.pokehit.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
        lifecycleScope.launch {
            val repository = PokemonRepository(
                ApiClient.apiService,
                AppDatabase.getDatabase(this@MainActivity)
            )
            val viewModel = MainViewModel(repository)

            // Подписываемся на изменения состояния
            lifecycleScope.launch {
                viewModel.state.collect { state ->
                    Log.d("MVI_Test", "Состояние: isLoading=${state.isLoading}, " +
                            "pokemons=${state.filteredPokemons.size}, " +
                            "favorites=${state.favorites.size}, " +
                            "query='${state.searchQuery}'")
                }
            }

            // Тестируем разные интенты
            delay(2000)
            Log.d("MVI_Test", "Тест поиска")
            viewModel.sendIntent(MainIntent.UpdateSearchQuery("pika"))

            delay(1000)
            Log.d("MVI_Test", "Тест сброса")
            viewModel.sendIntent(MainIntent.ResetFilters)

            delay(1000)
            Log.d("MVI_Test", "Тест добавления в избранное")
            viewModel.sendIntent(MainIntent.ToggleFavorite(25))  // Пикачу

            delay(1000)
            viewModel.sendIntent(MainIntent.ToggleFavorite(25))  // Убираем

            delay(1000)
            Log.d("MVI_Test", "Тест загрузки следующей страницы")
            viewModel.sendIntent(MainIntent.LoadNextPage)
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