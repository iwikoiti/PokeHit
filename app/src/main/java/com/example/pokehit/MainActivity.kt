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
import com.example.pokehit.database.AppDatabase
import com.example.pokehit.database.FavoritePokemonEntity
import com.example.pokehit.model.BasicPokemon
import com.example.pokehit.model.DetailedPokemon
import com.example.pokehit.model.PokemonStat
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
        // Тест модели
        val testBasic = BasicPokemon(
            id = 132,
            name = "ditto",
            imageUrl = "https://example.com/image.png",
            types = listOf("normal")
        )
        Log.d("PokeModel", "BasicPokemon: ${testBasic.name}, types: ${testBasic.types}")

        val testStat = PokemonStat(name = "hp", baseStat = 48, effort = 1)
        val testDetailed = DetailedPokemon(
            basicInfo = testBasic,
            height = 3,
            weight = 40,
            baseExperience = 101,
            stats = listOf(testStat),
            abilities = listOf("limber", "imposter"),
            description = "Test description",
            backImageUrl = null,
            frontShinyUrl = null,
            backShinyUrl = null,
            homeImageUrl = null,
            dreamWorldImageUrl = null
        )
        Log.d("PokeModel", "DetailedPokemon HP: ${testDetailed.stats[0].baseStat}")

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