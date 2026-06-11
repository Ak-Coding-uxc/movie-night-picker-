package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Movies : Screen("movies", "Movies", Icons.Default.Movie)
    object Collections : Screen("collections_hub", "Collections", Icons.Default.Folder)
    object CollectionDetails : Screen("collection_details", "Collection Details", Icons.Default.FolderOpen)
    object Picker : Screen("picker", "Picker", Icons.Default.Casino)
    object Favorites : Screen("favorites", "Favorites", Icons.Default.Favorite)
    object Stats : Screen("stats", "Stats", Icons.Default.PieChart)
}
