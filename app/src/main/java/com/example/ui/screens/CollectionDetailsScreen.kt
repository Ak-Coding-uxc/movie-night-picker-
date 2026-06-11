package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Movie
import com.example.ui.MovieViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailsScreen(
    viewModel: MovieViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val collections by viewModel.collections.collectAsState()
    val selectedColId by viewModel.selectedCollectionId.collectAsState()
    val activeCollection = remember(collections, selectedColId) {
        collections.find { it.id == selectedColId }
    }

    val moviesInCol by viewModel.moviesInSelectedCollection.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isFamilySafeMode by viewModel.isFamilySafeMode.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    // Clear search query specifically when visiting or leaving this details panel to prevent leakage
    DisposableEffect(Unit) {
        onDispose {
            viewModel.setSearchQuery("")
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF09080B), 
                        Color(0xFF0C0B0E), 
                        Color(0xFF050505)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Elegant cinematic header bar with Back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("collection_details_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back",
                        tint = PureWhite
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activeCollection?.name ?: "Playlist Contents",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = PureWhite
                    )
                    Text(
                        text = "${moviesInCol.size} matching titles",
                        fontSize = 11.sp,
                        color = MutedText
                    )
                }

                // Family safe notification indicator
                AnimatedVisibility(isFamilySafeMode) {
                    Icon(
                        imageVector = Icons.Default.ChildCare,
                        contentDescription = "Family active",
                        tint = FamilyGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Simple search filtering inside this collection
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("collection_search_input"),
                placeholder = { Text("Search inside this collection...", color = MutedText.copy(alpha = 0.5f), fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = CinemaRed,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = PureWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CinemaRed.copy(alpha = 0.6f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.05f),
                    focusedContainerColor = Color(0x1AFFFFFF),
                    unfocusedContainerColor = Color(0x0FFFFFFF),
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Movie index list representation
            if (moviesInCol.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MovieFilter,
                            contentDescription = "No items",
                            tint = MutedText.copy(alpha = 0.3f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No filter results" else "No screenings inside this playlist",
                            color = PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Try searching a different title or genre." else "Add movies to database directly and assign them here, or tap 'Add Movie to Playlist' below.",
                            color = MutedText,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp)
                ) {
                    items(moviesInCol, key = { it.id }) { movie ->
                        MovieTicketCard(
                            movie = movie,
                            onToggleFavorite = { viewModel.toggleFavorite(movie) },
                            onToggleWatched = { viewModel.toggleWatched(movie) },
                            onDelete = { viewModel.deleteMovie(movie) }
                        )
                    }
                }
            }
        }

        // Action Floating Button specifically targeting movie additions bound to this collection
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 20.dp, end = 20.dp)
                .testTag("collection_add_movie_fab"),
            containerColor = CinemaRed,
            contentColor = PureWhite,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Movie to Playlist", modifier = Modifier.size(20.dp))
                Text("Add Movie", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    // Reuse elegant Add Movie Dialog, but override selection defaults to this active collection
    if (showAddDialog) {
        AddMovieDialog(
            defaultFamilySafe = isFamilySafeMode,
            collections = collections,
            onDismiss = { showAddDialog = false },
            onSave = { title, genre, year, rating, familySafe, favorite, collectionId ->
                // Auto bind to current active collection if none is selected
                val targetColId = collectionId ?: selectedColId
                viewModel.addMovie(title, genre, year, rating, familySafe, favorite, targetColId)
                showAddDialog = false
            }
        )
    }
}
