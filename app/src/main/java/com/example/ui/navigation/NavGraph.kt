package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ui.MovieViewModel
import com.example.ui.screens.*

@Composable
fun MovieNightNavGraph(
    navController: NavHostController,
    viewModel: MovieViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Movies.route,
        modifier = modifier
    ) {
        composable(Screen.Movies.route) {
            MoviesScreen(viewModel = viewModel)
        }
        composable(Screen.Collections.route) {
            CollectionsScreen(
                viewModel = viewModel,
                onNavigateToDetails = { navController.navigate(Screen.CollectionDetails.route) }
            )
        }
        composable(Screen.CollectionDetails.route) {
            CollectionDetailsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Picker.route) {
            PickerScreen(viewModel = viewModel)
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(viewModel = viewModel)
        }
        composable(Screen.Stats.route) {
            StatsScreen(viewModel = viewModel)
        }
    }
}
