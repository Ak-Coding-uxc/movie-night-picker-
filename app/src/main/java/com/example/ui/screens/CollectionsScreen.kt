package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.MovieCollection
import com.example.ui.MovieViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(
    viewModel: MovieViewModel,
    onNavigateToDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val collectionsWithCount by viewModel.collectionsWithCount.collectAsState()
    val isFamilySafeMode by viewModel.isFamilySafeMode.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var collectionToRename by remember { mutableStateOf<MovieCollection?>(null) }
    var collectionToDelete by remember { mutableStateOf<MovieCollection?>(null) }

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
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Screen Header Panel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Collections",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = PureWhite,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Themed playlists & movie catalogs",
                        fontSize = 11.sp,
                        color = MutedText
                    )
                }

                // Family safe badge
                AnimatedVisibility(isFamilySafeMode) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(FamilyGreenBg)
                            .border(1.dp, FamilyGreen.copy(alpha = 0.5f), RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChildCare,
                                contentDescription = "Family active",
                                tint = FamilyGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Family",
                                color = FamilyGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Collections Grid Board
            if (collectionsWithCount.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(50))
                                .background(CinemaGold.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = "Folder",
                                tint = CinemaGold.copy(alpha = 0.7f),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Text(
                            text = "No playlists catalogued",
                            color = PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Set up your own custom collections (like Anime, Family Movies, Marvel, Horror) to classify your movie favorites elegantly.",
                            color = MutedText,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                        
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CinemaRed, contentColor = PureWhite),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Create Collection Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(collectionsWithCount, key = { it.first.id }) { (col, count) ->
                        CollectionGridCard(
                            collection = col,
                            movieCount = count,
                            onClick = {
                                viewModel.selectCollection(col.id)
                                onNavigateToDetails()
                            },
                            onRename = { collectionToRename = col },
                            onDelete = { collectionToDelete = col }
                        )
                    }
                }
            }
        }

        // Float Add Button on bottom right
        if (collectionsWithCount.isNotEmpty()) {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 20.dp, end = 20.dp)
                    .testTag("create_collection_fab"),
                containerColor = CinemaRed,
                contentColor = PureWhite,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.CreateNewFolder, contentDescription = "Add Collection", modifier = Modifier.size(20.dp))
                    Text("New Playlist", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }

    // Modal dialog for creation
    if (showAddDialog) {
        CreateCollectionDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, iconName ->
                viewModel.addCollection(name, iconName)
                showAddDialog = false
            }
        )
    }

    // Modal dialog for rename
    collectionToRename?.let { col ->
        RenameCollectionDialog(
            collection = col,
            onDismiss = { collectionToRename = null },
            onSave = { newName ->
                viewModel.renameCollection(col, newName)
                collectionToRename = null
            }
        )
    }

    // Modal dialog for delete confirmation
    collectionToDelete?.let { col ->
        DeleteCollectionConfirmDialog(
            collection = col,
            onDismiss = { collectionToDelete = null },
            onConfirm = {
                viewModel.deleteCollection(col)
                collectionToDelete = null
            }
        )
    }
}

@Composable
fun CollectionGridCard(
    collection: MovieCollection,
    movieCount: Int,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    // Beautiful dynamic icons matched from name descriptors
    val icon = remember(collection.iconName) {
        getIconForDescriptor(collection.iconName)
    }

    val themeGradient = remember(collection.iconName) {
        getGradientForDescriptor(collection.iconName)
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF141318),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        modifier = Modifier
            .fillMaxWidth()
            .height(145.dp)
            .testTag("collection_card_${collection.id}")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Gradient decorative highlight
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.radialGradient(colors = themeGradient, radius = 240f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Icon and Actions row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = collection.name,
                            tint = themeGradient.first(),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Simple Actions dropdown triggers
                    Row {
                        IconButton(onClick = onRename, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Rename", tint = MutedText.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CinemaRed.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                        }
                    }
                }

                // Name and details
                Column {
                    Text(
                        text = collection.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PureWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$movieCount ${if (movieCount == 1) "Movie" else "Movies"}",
                        fontSize = 11.sp,
                        color = MutedText
                    )
                }
            }
        }
    }
}

@Composable
fun CreateCollectionDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, iconName: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedIconName by remember { mutableStateOf("Movie") }

    val iconPresets = listOf(
        Pair("Anime", "Animation"),
        Pair("Marvel", "Superhero"),
        Pair("DC Comics", "Gavel"),
        Pair("Horror", "Ghost"),
        Pair("Comedy", "Laugh"),
        Pair("Family", "ChildCare"),
        Pair("Movies", "Movie")
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF141318),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "New Playlist Collection",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Collection Title", color = MutedText, fontSize = 13.sp) },
                    singleLine = true,
                    placeholder = { Text("E.g. Anime, Marvel, DC", color = MutedText.copy(alpha = 0.5f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("collection_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CinemaRed,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    )
                )

                Text("Visual Theme", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                // Select preset shortcuts mapping colors & icons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    iconPresets.forEach { (label, presetType) ->
                        val isSelected = selectedIconName == presetType
                        val itemGradient = getGradientForDescriptor(presetType)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) itemGradient.first().copy(alpha = 0.2f) else Color(0x19FFFFFF))
                                .border(
                                    1.dp,
                                    if (isSelected) itemGradient.first() else Color.White.copy(alpha = 0.05f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedIconName = presetType
                                    if (name.isBlank() || name == "New Collection") {
                                        name = label
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

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
                            if (name.trim().isNotEmpty()) {
                                onSave(name, selectedIconName)
                            }
                        },
                        enabled = name.trim().isNotEmpty(),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("submit_collection_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CinemaRed,
                            contentColor = PureWhite,
                            disabledContainerColor = CinemaRed.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Create", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun RenameCollectionDialog(
    collection: MovieCollection,
    onDismiss: () -> Unit,
    onSave: (newName: String) -> Unit
) {
    var name by remember { mutableStateOf(collection.name) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF141318),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Rename Playlist Title",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("New Playlist Name", color = MutedText, fontSize = 13.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CinemaRed,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    )
                )

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
                            if (name.trim().isNotEmpty()) {
                                onSave(name)
                            }
                        },
                        enabled = name.trim().isNotEmpty(),
                        modifier = Modifier
                            .weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CinemaRed,
                            contentColor = PureWhite,
                            disabledContainerColor = CinemaRed.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Rename", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteCollectionConfirmDialog(
    collection: MovieCollection,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete \"${collection.name}\"?",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
        },
        text = {
            Text(
                "Are you sure you want to remove this playlist? The movies inside will NOT be deleted, they are kept safely in your General Movies index.",
                color = MutedText,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PureWhite)
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = CinemaRed, contentColor = PureWhite),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Delete", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xFF141318),
        shape = RoundedCornerShape(16.dp)
    )
}

// Map descriptor to vectors
private fun getIconForDescriptor(desc: String): ImageVector {
    return when (desc.lowercase()) {
        "animation" -> Icons.Default.Animation
        "superhero" -> Icons.Default.Shield
        "gavel" -> Icons.Default.Gavel
        "ghost" -> Icons.Default.MoodBad
        "laugh" -> Icons.Default.SentimentSatisfiedAlt
        "childcare" -> Icons.Default.ChildCare
        else -> Icons.Default.Movie
    }
}

// Map descriptor to gorgeous dark glowing gradient highlights
private fun getGradientForDescriptor(desc: String): List<Color> {
    return when (desc.lowercase()) {
        "animation" -> listOf(Color(0xFFE040FB), Color(0x00E040FB))
        "superhero" -> listOf(Color(0xFFE50914), Color(0x00E50914))
        "gavel" -> listOf(Color(0xFF00E5FF), Color(0x0000E5FF))
        "ghost" -> listOf(Color(0xFF9E9E9E), Color(0x009E9E9E))
        "laugh" -> listOf(Color(0xFFFFD600), Color(0x00FFD600))
        "childcare" -> listOf(Color(0xFF00E676), Color(0x0000E676))
        else -> listOf(Color(0xFFFF3D00), Color(0x00FF3D00))
    }
}
