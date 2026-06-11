package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Movie
import com.example.ui.MovieViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(viewModel: MovieViewModel, modifier: Modifier = Modifier) {
    val movies by viewModel.movies.collectAsState()
    val isFamilySafeMode by viewModel.isFamilySafeMode.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

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
            // Immersive Header Container (Netflix brand identity)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Premium Crimson Red Logo Mark
                    Text(
                        text = "Cinema",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = CinemaRed,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = "Roulette",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = PureWhite,
                        letterSpacing = (-1).sp
                    )
                }

                // Family night badge indicator
                AnimatedVisibility(
                    visible = isFamilySafeMode,
                    enter = fadeIn() + scaleIn(initialScale = 0.9f),
                    exit = fadeOut() + scaleOut(targetScale = 0.9f)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(FamilyGreenBg)
                            .border(1.dp, FamilyGreen.copy(alpha = 0.6f), RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChildCare,
                                contentDescription = "Family Night",
                                tint = FamilyGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Family Night Active",
                                color = FamilyGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Cinematic Minimalist Search & Filter Panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Search Input with Glassmorphic visual outline
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input"),
                    placeholder = { Text("Search title, genre, year...", color = MutedText.copy(alpha = 0.7f), fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = CinemaRed,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = PureWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CinemaRed.copy(alpha = 0.8f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                        focusedContainerColor = Color(0x33FFFFFF),
                        unfocusedContainerColor = Color(0x19FFFFFF),
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    )
                )

                // Family Safe Mode quick glass bar
                Surface(
                    onClick = { viewModel.toggleFamilySafeMode() },
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x1AFFFFFF),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("family_safe_toggle")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isFamilySafeMode) FamilyGreenBg else Color(0x11FFFFFF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isFamilySafeMode) Icons.Default.ChildCare else Icons.Outlined.ChildCare,
                                    contentDescription = "Family Safe Switch icon",
                                    tint = if (isFamilySafeMode) FamilyGreen else MutedText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Family Safe Filter",
                                    fontWeight = FontWeight.SemiBold,
                                    color = PureWhite,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Only show family friendly screenings",
                                    color = MutedText,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Switch(
                            checked = isFamilySafeMode,
                            onCheckedChange = { viewModel.setFamilySafeMode(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = FamilyGreen,
                                checkedTrackColor = FamilyGreenBg,
                                uncheckedThumbColor = MutedText,
                                uncheckedTrackColor = Color.Transparent
                            ),
                            modifier = Modifier.scale(0.81f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Collection Content
            if (movies.isEmpty()) {
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
                            contentDescription = "No movies registered",
                            tint = MutedText.copy(alpha = 0.25f),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = if (searchQuery.isNotEmpty() || isFamilySafeMode) {
                                "No matches found"
                            } else {
                                "Curate Your Database"
                            },
                            color = PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (searchQuery.isNotEmpty() || isFamilySafeMode) {
                                "Try clearing your text filters or toggling Family Safe Mode."
                            } else {
                                "Begin your database by tapping 'Add Movie' below. Custom deterministic posters will load instantly!"
                            },
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
                    // Netflix-inspired feature Spotlight header
                    val spotlightMovie = movies.find { it.isFavorite } ?: movies.firstOrNull()
                    if (spotlightMovie != null && searchQuery.isEmpty()) {
                        item {
                            SpotlightBanner(
                                movie = spotlightMovie,
                                onWatchNow = { viewModel.toggleWatched(spotlightMovie) }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Screening Catalog",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = PureWhite,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }

                    items(movies, key = { it.id }) { movie ->
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

        // Float Premium Add Movie Action Button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 20.dp, end = 20.dp)
                .testTag("add_movie_button"),
            containerColor = CinemaRed,
            contentColor = PureWhite,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Movie", modifier = Modifier.size(20.dp))
                Text("Add Movie", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    // Modern Modal dialogue for adding a movie
    if (showAddDialog) {
        val collections by viewModel.collections.collectAsState()
        AddMovieDialog(
            defaultFamilySafe = isFamilySafeMode,
            collections = collections,
            onDismiss = { showAddDialog = false },
            onSave = { title, genre, year, rating, familySafe, favorite, collectionId ->
                viewModel.addMovie(title, genre, year, rating, familySafe, favorite, collectionId)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun MoviePosterThumbnail(movie: Movie, modifier: Modifier = Modifier) {
    val genreColors = remember(movie.genre) {
        val g = movie.genre.lowercase()
        when {
            g.contains("action") || g.contains("horror") || g.contains("thriller") -> 
                listOf(Color(0xFFB71C1C), Color(0xFF0F080A))
            g.contains("comedy") || g.contains("animation") || g.contains("family") || g.contains("kids") -> 
                listOf(Color(0xFFFF6F00), Color(0xFF510926))
            g.contains("sci-fi") || g.contains("fantasy") || g.contains("mystery") -> 
                listOf(Color(0xFF00B8D4), Color(0xFF0A0F2B))
            g.contains("drama") || g.contains("documentary") || g.contains("romance") || g.contains("history") -> 
                listOf(Color(0xFF6A1B9A), Color(0xFF1D062E))
            else -> 
                listOf(Color(0xFF455A64), Color(0xFF0B0A0C))
        }
    }
    
    val initials = remember(movie.title) {
        movie.title.split(" ")
            .filter { it.isNotEmpty() }
            .take(2)
            .map { it.first().uppercase() }
            .joinToString("")
    }

    Box(
        modifier = modifier
            .width(82.dp)
            .height(115.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .background(Brush.verticalGradient(genreColors))
    ) {
        // High fidelity procedural layout overlays
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
        ) {
            // Large blurred initials rendering as background artwork
            Text(
                text = initials,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier.align(Alignment.Center)
            )

            // Top decorative "N/C" logo ribbon & label
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "N",
                    fontWeight = FontWeight.Black,
                    color = CinemaRed,
                    fontSize = 11.sp,
                    letterSpacing = (-1).sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 3.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "HDR",
                        color = CinemaGold,
                        fontSize = 6.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Bottom fading Title Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                        )
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = movie.title,
                    color = PureWhite,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 10.sp
                )
            }
        }
    }
}

@Composable
fun SpotlightBanner(movie: Movie, onWatchNow: () -> Unit) {
    val genreColors = remember(movie.genre) {
        val g = movie.genre.lowercase()
        when {
            g.contains("action") || g.contains("horror") || g.contains("thriller") -> 
                listOf(Color(0xFF8E040E).copy(alpha = 0.8f), Color(0xFF140204))
            g.contains("comedy") || g.contains("animation") || g.contains("family") -> 
                listOf(Color(0xFFFF5722).copy(alpha = 0.8f), Color(0xFF2E0513))
            g.contains("sci-fi") || g.contains("fantasy") -> 
                listOf(Color(0xFF00ACC1).copy(alpha = 0.8f), Color(0xFF060920))
            else -> 
                listOf(Color(0xFF5E35B1).copy(alpha = 0.8f), Color(0xFF0C021E))
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        color = Color.Black
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(genreColors))
        ) {
            // Elegant background pattern
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MoviePosterThumbnail(movie = movie, modifier = Modifier.scale(1.05f))

                // Cinematic Description Group
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "★ SPOTLIGHT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CinemaGold,
                                letterSpacing = 1.sp
                            )
                            if (movie.isFamilySafe) {
                                Text(
                                    text = "FAMILY SAFE",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = FamilyGreen,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(FamilyGreenBg)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = movie.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = PureWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${movie.genre}  •  ${movie.releaseYear}",
                            fontSize = 11.sp,
                            color = MutedText,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onWatchNow,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PureWhite,
                                contentColor = Color.Black
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (movie.isWatched) Icons.Default.Check else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (movie.isWatched) "Screened" else "Screen Choice",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Premium dynamic rating label
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = CinemaGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${movie.personalRating}",
                                color = PureWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieTicketCard(
    movie: Movie,
    onToggleFavorite: () -> Unit,
    onToggleWatched: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0x2B18171C),
        border = BorderStroke(
            1.dp,
            if (movie.isFavorite) CinemaRed.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f)
        ),
        shadowElevation = if (movie.isFavorite) 4.dp else 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Elegant deterministic mock poster representation
            MoviePosterThumbnail(movie = movie)

            // Right content details column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = movie.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PureWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = movie.genre,
                                fontSize = 12.sp,
                                color = MutedText,
                                fontWeight = FontWeight.Normal
                            )
                            Text(text = "•", color = MutedText, fontSize = 12.sp)
                            Text(
                                text = "${movie.releaseYear}",
                                fontSize = 12.sp,
                                color = CinemaGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Trash button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_movie_button_${movie.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = Color.White.copy(alpha = 0.4e-1f + 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Double inline badges / Star rating and controls row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Star rating visuals
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        val fullStars = movie.personalRating.toInt()
                        val hasHalf = movie.personalRating % 1 >= 0.5f

                        for (i in 1..5) {
                            Icon(
                                imageVector = when {
                                    i <= fullStars -> Icons.Default.Star
                                    i == fullStars + 1 && hasHalf -> Icons.Default.StarHalf
                                    else -> Icons.Outlined.Star
                                },
                                contentDescription = null,
                                tint = if (i <= fullStars + (if (hasHalf) 1 else 0)) CinemaGold else MutedText.copy(alpha = 0.3f),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        if (movie.isFamilySafe) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ChildCare,
                                contentDescription = "Family safe item icon",
                                tint = FamilyGreen,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    // Action controls row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Eye Watched Badge
                        IconButton(
                            onClick = onToggleWatched,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("toggle_watched_button_${movie.id}")
                        ) {
                            Icon(
                                imageVector = if (movie.isWatched) Icons.Filled.Visibility else Icons.Outlined.Visibility,
                                contentDescription = "Toggle screening watch",
                                tint = if (movie.isWatched) CinemaGold else MutedText.copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Favorite Heart Badge
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("toggle_favorite_button_${movie.id}")
                        ) {
                            Icon(
                                imageVector = if (movie.isFavorite) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                                contentDescription = "Toggle favorite status",
                                tint = if (movie.isFavorite) CinemaRed else MutedText.copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddMovieDialog(
    defaultFamilySafe: Boolean = false,
    collections: List<com.example.data.MovieCollection> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (title: String, genre: String, year: Int, rating: Float, familySafe: Boolean, favorite: Boolean, collectionId: Long?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var releaseYear by remember { mutableStateOf("2026") }
    var rating by remember { mutableFloatStateOf(4f) }
    var isFamilySafe by remember(defaultFamilySafe) { mutableStateOf(defaultFamilySafe) }
    var isFavorite by remember { mutableStateOf(false) }
    var selectedCollectionId by remember { mutableStateOf<Long?>(null) }

    val presetGenres = listOf("Action", "Comedy", "Drama", "Sci-Fi", "Thriller", "Horror", "Documentary", "Animation", "Family")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 8.dp,
            color = Color(0xFF141318),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Add Title to Database",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Movie Title", color = MutedText, fontSize = 13.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("movie_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CinemaRed,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    )
                )

                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre", color = MutedText, fontSize = 13.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("movie_genre_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CinemaRed,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    )
                )

                // Horizontally scrollable genre chips tag suggestions (highly responsive & safe)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    presetGenres.forEach { preset ->
                        val isSelected = genre.equals(preset, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CinemaRed else Color(0x19FFFFFF))
                                .border(
                                    1.dp, 
                                    if (isSelected) CinemaRed else Color.White.copy(alpha = 0.05f), 
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { genre = preset }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = preset,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = releaseYear,
                    onValueChange = { releaseYear = it.filter { char -> char.isDigit() } },
                    label = { Text("Release Year", color = MutedText, fontSize = 13.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("movie_year_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CinemaRed,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    )
                )

                // Playlist / Collection Selector
                Text("Playlist / Collection Target", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isNoneSelected = selectedCollectionId == null
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isNoneSelected) CinemaGold else Color(0x19FFFFFF))
                            .border(
                                1.dp, 
                                if (isNoneSelected) CinemaGold else Color.White.copy(alpha = 0.05f), 
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedCollectionId = null }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "None (General)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isNoneSelected) Color.Black else PureWhite
                        )
                    }

                    collections.forEach { col ->
                        val isSelected = selectedCollectionId == col.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CinemaRed else Color(0x19FFFFFF))
                                .border(
                                    1.dp, 
                                    if (isSelected) CinemaRed else Color.White.copy(alpha = 0.05f), 
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedCollectionId = col.id }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = col.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }
                    }
                }

                // Slider Star Rating
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Rating", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${rating.toInt()} Stars", color = CinemaGold, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                    Slider(
                        value = rating,
                        onValueChange = { rating = it },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(
                            thumbColor = CinemaGold,
                            activeTrackColor = CinemaGold,
                            inactiveTrackColor = Color(0x19FFFFFF)
                        ),
                        modifier = Modifier.testTag("movie_rating_slider")
                    )
                }

                // Checkbox options
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isFamilySafe = !isFamilySafe }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.ChildCare, contentDescription = null, tint = FamilyGreen, modifier = Modifier.size(18.dp))
                            Text("Family Friendly", color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                        Checkbox(
                            checked = isFamilySafe,
                            onCheckedChange = { isFamilySafe = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = FamilyGreen,
                                checkmarkColor = Color.Black
                            ),
                            modifier = Modifier.testTag("movie_family_safe_checkbox")
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isFavorite = !isFavorite }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = CinemaRed, modifier = Modifier.size(18.dp))
                            Text("Mark as Favorite", color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                        Checkbox(
                            checked = isFavorite,
                            onCheckedChange = { isFavorite = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = CinemaRed,
                                checkmarkColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            if (title.trim().isNotEmpty()) {
                                val finalYear = releaseYear.toIntOrNull() ?: 2026
                                onSave(title, genre.ifBlank { "Uncategorized" }, finalYear, rating, isFamilySafe, isFavorite, selectedCollectionId)
                            }
                        },
                        enabled = title.trim().isNotEmpty(),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_movie_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CinemaRed,
                            contentColor = PureWhite,
                            disabledContainerColor = CinemaRed.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Add Choice", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
