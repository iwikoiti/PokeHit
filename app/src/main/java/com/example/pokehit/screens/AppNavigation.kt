package com.example.pokehit.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pokehit.viewmodel.MainViewModel

@Composable
fun AppNavigation(
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val mainState by mainViewModel.state.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "main",
        modifier = modifier
    ) {
        composable("main") {
            MainScreen(
                state = mainState,
                onIntent = { intent ->
                    mainViewModel.sendIntent(intent)
                },
                onPokemonClick = { pokemonId ->
                    // navController.navigate("detail/$pokemonId")
                    println("Clicked on pokemon: $pokemonId")
                }
            )
        }

    }
}