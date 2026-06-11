package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MovieViewModel
import com.example.ui.theme.*

@Composable
fun StatsScreen(viewModel: MovieViewModel, modifier: Modifier = Modifier) {
    val stats by viewModel.statistics.collectAsState()
    val scrollState = rememberScrollState()

    // Determine cinema milestoners
    val movieMilestone = remember(stats.totalMovies) {
        when {
            stats.totalMovies >= 15 -> "Hollywood Mogul"
            stats.totalMovies >= 8 -> "Popcorn Master"
            stats.totalMovies >= 3 -> "Silver Screen Buff"
            else -> "Cinema Apprentice"
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF070609), 
                        Color(0xFF0E0D11), 
                        Color(0xFF040405)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Upper Title Panel
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Theater Insights",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Aesthetic database metrics and curator achievements",
                    fontSize = 11.sp,
                    color = MutedText
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Milestone Badge Board
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0x19FFFFFF),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CinemaGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Milestone Medal",
                            tint = CinemaGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "CURATOR RANK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = CinemaGold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = movieMilestone,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PureWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Analytical Cards Stack
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("statistics_card"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatBoxCard(
                    title = "Total Database Count",
                    value = "${stats.totalMovies}",
                    icon = Icons.Default.Movie,
                    colorGlow = CinemaRed,
                    subtitle = "Titles currently registered under your screenings"
                )

                StatBoxCard(
                    title = "V.I.P. Favorites",
                    value = "${stats.favoriteMovies}",
                    icon = Icons.Default.Favorite,
                    colorGlow = CinemaAccentRed,
                    subtitle = "Handpicked titles pinned for rapid roulette curation"
                )

                StatBoxCard(
                    title = "Most Screened Genre",
                    value = stats.mostWatchedGenre,
                    icon = Icons.Default.LocalActivity,
                    colorGlow = CinemaGold,
                    subtitle = "Dominant category catalogued from history logs"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Cinematic Director Quotes
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0x0EFFFFFF),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.02f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 100.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = "Quote",
                        tint = CinemaRed.copy(alpha = 0.6f),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "\"Cinema is a matter of what's in the frame and what's out.\"",
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "— Martin Scorsese",
                        color = MutedText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatBoxCard(
    title: String,
    value: String,
    icon: ImageVector,
    colorGlow: Color,
    subtitle: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0x19FFFFFF),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorGlow.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colorGlow,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.uppercase(),
                    fontSize = 10.sp,
                    color = MutedText,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = value,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MutedText,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
