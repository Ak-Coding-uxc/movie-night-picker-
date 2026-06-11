package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Movie
import com.example.ui.MovieViewModel
import com.example.ui.theme.*

@Composable
fun PickerScreen(viewModel: MovieViewModel, modifier: Modifier = Modifier) {
    val pickedMovie by viewModel.pickedMovie.collectAsState()
    val isPicking by viewModel.isPicking.collectAsState()
    
    // Read collection-specific candidate pools
    val movies by viewModel.pickerEligibleMovies.collectAsState()
    val collections by viewModel.collections.collectAsState()
    val rouletteCollectionId by viewModel.rouletteCollectionId.collectAsState()
    val isFamilySafeMode by viewModel.isFamilySafeMode.collectAsState()

    // Pulsing glow animation background
    val infiniteTransition = rememberInfiniteTransition(label = "PulseGlow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowIntensity"
    )

    // Bouncing animation for pick visual reveal
    val bounceScale by animateFloatAsState(
        targetValue = if (isPicking) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "Reveal Bounce Scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF08070A),
                        Color(0xFF0E0D11),
                        Color(0xFF040405)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Screen Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CinemaRed.copy(alpha = 0.15f))
                        .border(1.dp, CinemaRed.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = "Random Pick",
                        tint = CinemaRed,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Cinema Roulette",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Let fate curate tonight's popcorn screening",
                    fontSize = 11.sp,
                    color = MutedText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Premium Family Night Toggle bar & Playlist constraint row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = { viewModel.toggleFamilySafeMode() },
                        shape = RoundedCornerShape(50),
                        color = if (isFamilySafeMode) FamilyGreenBg else Color(0x14FFFFFF),
                        border = BorderStroke(
                            1.dp,
                            if (isFamilySafeMode) FamilyGreen.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.05f)
                        ),
                        modifier = Modifier
                            .testTag("family_night_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isFamilySafeMode) Icons.Default.ChildCare else Icons.Default.People,
                                contentDescription = "Family Night",
                                tint = if (isFamilySafeMode) FamilyGreen else PureWhite.copy(alpha = 0.6f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isFamilySafeMode) "Family Active" else "General Catalogue",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = if (isFamilySafeMode) FamilyGreen else PureWhite.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Collection Selection Wheels Row
                Text(
                    text = "Pick Source Playlist",
                    color = MutedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isAllSelected = rouletteCollectionId == null
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (isAllSelected) CinemaRed else Color(0x11FFFFFF))
                            .border(
                                1.dp,
                                if (isAllSelected) CinemaRed else Color.White.copy(alpha = 0.05f),
                                RoundedCornerShape(50)
                            )
                            .clickable { viewModel.setRouletteCollectionId(null) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "All Movies",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }

                    collections.forEach { col ->
                        val isSelected = rouletteCollectionId == col.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (isSelected) CinemaRed else Color(0x11FFFFFF))
                                .border(
                                    1.dp,
                                    if (isSelected) CinemaRed else Color.White.copy(alpha = 0.05f),
                                    RoundedCornerShape(50)
                                )
                                .clickable { viewModel.setRouletteCollectionId(col.id) }
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
            }

            // Screen Center: The Magic Spinning Wheel Box with glowing ambient backlights
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (movies.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MovieFilter,
                            contentDescription = "Empty Database",
                            tint = MutedText.copy(alpha = 0.2f),
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "No movies in target pool!",
                            color = PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (rouletteCollectionId != null) {
                                "The selected collection doesn't have matching movies${if (isFamilySafeMode) " that are Family Safe" else ""}. Add titles to it to spin!"
                            } else {
                                "Add matching titles to the Movies list first to enable roulette selection."
                            },
                            color = MutedText,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 15.sp
                        )
                    }
                } else {
                    // Pulsing Glow Glow behind the ticket
                    val activeColor = if (isPicking) CinemaRed else CinemaGold
                    Box(
                        modifier = Modifier
                            .size(width = 240.dp, height = 300.dp)
                            .graphicsLayer {
                                scaleX = glowPulse * 1.08f
                                scaleY = glowPulse * 1.08f
                                alpha = glowPulse * 0.25f
                            }
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(activeColor, Color.Transparent)
                                )
                            )
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .scale(bounceScale)
                            .testTag("picked_movie_reveal_card")
                    ) {
                        AnimatedContent(
                            targetState = pickedMovie,
                            transitionSpec = {
                                if (isPicking) {
                                    (slideInVertically { height -> height } + fadeIn() togetherWith
                                            slideOutVertically { height -> -height } + fadeOut())
                                        .using(SizeTransform(clip = false))
                                } else {
                                    (scaleIn(initialScale = 0.82f) + fadeIn() togetherWith
                                            fadeOut() + scaleOut(targetScale = 0.82f))
                                }
                            },
                            label = "Cinema Slot Rolling"
                        ) { targetMovie ->
                            if (targetMovie != null) {
                                RouletteTicket(movie = targetMovie, isFinal = !isPicking)
                            } else {
                                RoulettePlaceholder()
                            }
                        }
                    }
                }
            }

            // Bottom Section: Big Cinema Trigger Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 90.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { viewModel.pickRandomMovie() },
                    enabled = movies.isNotEmpty() && !isPicking,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("random_pick_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFamilySafeMode) FamilyGreen else CinemaRed,
                        contentColor = PureWhite,
                        disabledContainerColor = Color.White.copy(alpha = 0.05f),
                        disabledContentColor = MutedText.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isPicking) Icons.Default.Refresh else Icons.Default.Casino,
                            contentDescription = "Pick Random Button Icon",
                            tint = if (movies.isNotEmpty() && !isPicking) PureWhite else MutedText.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                        val targetLabel = remember(rouletteCollectionId, collections) {
                            val targetCol = collections.find { it.id == rouletteCollectionId }
                            if (targetCol == null) "All Movies" else targetCol.name
                        }
                        Text(
                            text = if (isPicking) "SCANNING CATALOGUE..." else "SPIN WHEEL ($targetLabel)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (pickedMovie != null && !isPicking) {
                    TextButton(
                        onClick = { viewModel.clearPickedMovie() },
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            "Clear Selection", 
                            color = CinemaRed.copy(alpha = 0.8f), 
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(36.dp))
                }
            }
        }
    }
}

@Composable
fun RouletteTicket(movie: Movie, isFinal: Boolean) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF141318),
        border = BorderStroke(
            1.dp,
            if (isFinal) CinemaGold.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.08f)
        ),
        shadowElevation = if (isFinal) 12.dp else 2.dp,
        modifier = Modifier
            .width(260.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ticket Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CINEMA TICKET",
                    color = if (isFinal) CinemaGold else MutedText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                if (movie.isFamilySafe) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(FamilyGreenBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("FAVORABLE", color = FamilyGreen, fontSize = 8.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Beautiful movie poster in center of the ticket!
            MoviePosterThumbnail(movie = movie, modifier = Modifier.scale(1.22f).padding(vertical = 12.dp))

            Spacer(modifier = Modifier.height(12.dp))

            // Main Reveal Title
            Text(
                text = movie.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = PureWhite,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 24.sp,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .testTag("picked_movie_title")
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Genre & Year Badge
            Text(
                text = "${movie.genre}  •  ${movie.releaseYear}",
                fontSize = 12.sp,
                color = MutedText,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Dotted layout visual break line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(Color.Transparent, Color.White.copy(alpha = 0.15f), Color.Transparent)
                        )
                    )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Star score
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating Star",
                    tint = CinemaGold,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${movie.personalRating} / 5 Score",
                    fontSize = 12.sp,
                    color = PureWhite,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isFinal) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "🍿 TONIGHT'S PICK!",
                    color = CinemaGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun RoulettePlaceholder() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x1F22202A),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        modifier = Modifier
            .width(240.dp)
            .height(280.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Casino,
                contentDescription = "Roulette Roller",
                tint = MutedText.copy(alpha = 0.3f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Tap Spin Wheel to select",
                color = MutedText.copy(alpha = 0.7f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Enjoy randomly picking high-rated titles instantly.",
                color = MutedText.copy(alpha = 0.4f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
