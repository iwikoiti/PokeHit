package com.example.pokehit.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokehit.viewmodel.DetailViewModel
import com.example.pokehit.viewmodel.DetailViewModelFactory
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
                    navController.navigate("detail/$pokemonId")
                }
            )
        }

        composable(
            route = "detail/{pokemonId}",
            arguments = listOf(
                navArgument("pokemonId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val pokemonId = backStackEntry.arguments?.getInt("pokemonId") ?: return@composable

            val detailViewModel: DetailViewModel = viewModel(
                factory = DetailViewModelFactory(mainViewModel.repository)
            )
            val detailState by detailViewModel.state.collectAsState()

            DetailScreen(
                pokemonId = pokemonId,
                state = detailState,
                onIntent = { intent ->
                    detailViewModel.sendIntent(intent)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}