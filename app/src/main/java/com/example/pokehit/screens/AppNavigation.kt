package com.example.pokehit.screens

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokehit.R
import com.example.pokehit.viewmodel.DetailViewModel
import com.example.pokehit.viewmodel.DetailViewModelFactory
import com.example.pokehit.viewmodel.FavoritesViewModel
import com.example.pokehit.viewmodel.FavoritesViewModelFactory
import com.example.pokehit.viewmodel.MainViewModel

sealed class Screen(val route: String, val title: String, val icon: Int) {
    object Home : Screen("main", "Главная", R.drawable.baseline_home_24)
    object Favorites : Screen("favorites", "Избранное", R.drawable.outline_favorite_24)
}

@Composable
fun AppNavigation(
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val mainState by mainViewModel.state.collectAsState()

    val favoritesViewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(mainViewModel.repository)
    )
    val favoritesState by favoritesViewModel.state.collectAsState()

    val items = listOf(
        Screen.Home,
        Screen.Favorites
    )

    Scaffold(
        bottomBar = {
            NavigationBar (
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = screen.icon),
                                contentDescription = screen.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text(screen.title) },
                        selected = navController.currentDestination?.route == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
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

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    state = favoritesState,
                    onIntent = { intent ->
                        favoritesViewModel.sendIntent(intent)
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
}