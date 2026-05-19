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
            val repository = PokemonRepository(ApiClient.apiService)

            // Первый вызов - из сети
            val startTime1 = System.currentTimeMillis()
            val pikachu1 = repository.loadPokemonDetails(25)
            val duration1 = System.currentTimeMillis() - startTime1
            Log.d("PokeRepoTest", "Первый вызов (сеть): ${duration1}ms")

            // Второй вызов - из кэша
            val startTime2 = System.currentTimeMillis()
            val pikachu2 = repository.loadPokemonDetails(25)
            val duration2 = System.currentTimeMillis() - startTime2
            Log.d("PokeRepoTest", "Второй вызов (кэш): ${duration2}ms")

            // Проверяем, что объекты одинаковые
            Log.d("PokeRepoTest", "одинаково? ${pikachu1 === pikachu2}")

            // Проверяем размер кэша
            //val charmander = repository.loadPokemonDetails(4)
            val mewtwo = repository.loadPokemonDetails(150)
            if (mewtwo != null) {
                Log.d("PokeRepoTest", "Mewtwo загружен")
            } else {
                Log.d("PokeRepoTest", "Mewtwo пропущен (ошибка загрузки)")
            }
            Log.d("PokeRepoTest", "Размер кэша после загрузки 3 покемонов: ожидается 3")
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