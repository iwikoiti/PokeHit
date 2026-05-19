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
        // Тест базы данных
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@MainActivity)
            val dao = db.favoritePokemonDao()

            // 1. Добавляем в избранное
            dao.addToFavorites(FavoritePokemonEntity(132))  // Ditto
            dao.addToFavorites(FavoritePokemonEntity(25))   // Pikachu
            Log.d("PokeDB", "Добавили покемонов в избранное")

            // 2. Проверяем, избранный ли Ditto
            val isDittoFavorite = dao.isFavorite(132) > 0
            Log.d("PokeDB", "Ditto в избранном: $isDittoFavorite")  // true

            // 3. Получаем все ID избранных
            val favorites = dao.getAllFavorites()
            Log.d("PokeDB", "Все избранные ID: ${favorites.map { it.pokemonId }}")  // [132, 25]

            // 4. Удаляем Ditto
            dao.removeFromFavorites(132)
            val updatedFavorites = dao.getAllFavorites()
            Log.d("PokeDB", "После удаления: ${updatedFavorites.map { it.pokemonId }}")  // [25]

            // 5. Чистим всё
            dao.clearAllFavorites()
            val emptyFavorites = dao.getAllFavorites()
            Log.d("PokeDB", "После очистки: ${emptyFavorites.size}")  // 0
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