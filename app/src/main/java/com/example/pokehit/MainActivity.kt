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
import com.example.pokehit.ui.theme.PokeHitTheme
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
            try {
                // Тест 1: Получить список
                val listResponse = ApiClient.apiService.getPokemonList(limit = 5)
                Log.d("PokeAPI", "Список: ${listResponse.results.map { it.name }}")

                // Тест 2: Получить Ditto
                val ditto = ApiClient.apiService.getPokemon(132)
                Log.d("PokeAPI", "Ditto: ${ditto.name}, рост: ${ditto.height}, вес: ${ditto.weight}")
                Log.d("PokeAPI", "Типы: ${ditto.types.map { it.type.name }}")
                Log.d("PokeAPI", "Картинка: ${ditto.sprites.other?.officialArtwork?.frontDefault}")

                // Тест 3: Получить описание Ditto
                val species = ApiClient.apiService.getPokemonSpecies(132)
                val description = species.flavorTextEntries
                    .firstOrNull { it.language.name == "en" }
                    ?.flavorText
                    ?.replace("\n", " ")
                    ?.replace("\u000c", " ")
                Log.d("PokeAPI", "Описание: $description")

            } catch (e: Exception) {
                Log.e("PokeAPI", "Ошибка: ${e.message}")
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