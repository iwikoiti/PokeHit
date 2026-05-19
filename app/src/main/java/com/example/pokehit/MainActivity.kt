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
            // детали Ditto
            val dittoDetails = repository.loadPokemonDetails(132)
            if (dittoDetails != null) {
                Log.d("PokeRepoTest", "Ditto загружен успешно")
                Log.d("PokeRepoTest", "имя: ${dittoDetails.basicInfo.name}")
                Log.d("PokeRepoTest", "типы: ${dittoDetails.basicInfo.types}")
                Log.d("PokeRepoTest", "рост: ${dittoDetails.height}")
                Log.d("PokeRepoTest", "вес: ${dittoDetails.weight}")
                Log.d("PokeRepoTest", "способности: ${dittoDetails.abilities}")
                Log.d("PokeRepoTest", "описание: ${dittoDetails.description?.take(100)}...")
                // статы
                dittoDetails.stats.forEach { stat ->
                    Log.d("PokeRepoTest", "   ${stat.name}: ${stat.baseStat} (effort: ${stat.effort})")
                }
            } else {
                Log.e("PokeRepoTest", "Не удалось загрузить Ditto")
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