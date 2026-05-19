package com.example.pokehit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokehit.api.ApiClient
import com.example.pokehit.database.AppDatabase
import com.example.pokehit.repository.PokemonRepository
import com.example.pokehit.screens.AppNavigation
import com.example.pokehit.ui.theme.PokeHitTheme
import com.example.pokehit.viewmodel.MainViewModel
import com.example.pokehit.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val repository = PokemonRepository(ApiClient.apiService, database)

        setContent {
            PokeHitTheme {
                val mainViewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(repository)
                )

                AppNavigation(
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}