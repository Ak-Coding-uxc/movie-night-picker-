package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.AppDatabase
import com.example.data.MovieRepository
import com.example.ui.MovieViewModel
import com.example.ui.MovieViewModelFactory
import com.example.ui.navigation.MovieNightNavGraph
import com.example.ui.navigation.Screen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.MidnightTheater
import com.example.ui.theme.VelvetSurface
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.PureWhite
import androidx.compose.ui.text.font.FontWeight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            
            // Instantiate AppDatabase, Repository, and ViewModel
            val database = remember { AppDatabase.getDatabase(context) }
            val repository = remember { MovieRepository(database.movieDao()) }
            val viewModel: MovieViewModel by viewModels { MovieViewModelFactory(repository) }

            MyApplicationTheme {
                val navController = rememberNavController()
                val screens = listOf(
                    Screen.Movies,
                    Screen.Collections,
                    Screen.Picker,
                    Screen.Favorites,
                    Screen.Stats
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MidnightTheater,
                    bottomBar = {
                        NavigationBar(
                            containerColor = MidnightTheater,
                            tonalElevation = 0.dp, // Flat pristine dark design
                            windowInsets = WindowInsets.navigationBars
                        ) {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = navBackStackEntry?.destination?.route

                            screens.forEach { screen ->
                                val isSelected = currentRoute == screen.route || 
                                        (screen == Screen.Collections && currentRoute == Screen.CollectionDetails.route)
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = screen.title,
                                            tint = if (isSelected) CinemaRed else Color.White.copy(alpha = 0.45f)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = screen.title,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                            color = if (isSelected) PureWhite else Color.White.copy(alpha = 0.45f)
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent // Clean minimalist tab layout
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    MovieNightNavGraph(
                        navController = navController,
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}
