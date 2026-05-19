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
            val repository = PokemonRepository(
                ApiClient.apiService,
                AppDatabase.getDatabase(this@MainActivity)
            )

            //Поиск по имени
            Log.d("PokeRepoTest", "Поиск pika")
            val pikaResults = repository.searchPokemon("pika")
            pikaResults.take(5).forEach { pokemon ->
                Log.d("PokeRepoTest", "  ${pokemon.name}")
            }

            //Фильтрация по типам
            Log.d("PokeRepoTest", " Фильтр fire")
            val fireTypes = repository.filterPokemonByTypes(setOf("fire"))
            fireTypes.take(5).forEach { pokemon ->
                // Для проверки типов нужно загрузить детали
                val details = repository.loadPokemonDetails(pokemon.id)
                Log.d("PokeRepoTest", "  ${pokemon.name} (${details?.basicInfo?.types})")
            }

            //Комбинированный поиск
            Log.d("PokeRepoTest", "char + fire")
            val combined = repository.searchAndFilter("char", setOf("fire"))
            combined.forEach { pokemon ->
                val details = repository.loadPokemonDetails(pokemon.id)
                Log.d("PokeRepoTest", "  ${pokemon.name} (${details?.basicInfo?.types})")
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