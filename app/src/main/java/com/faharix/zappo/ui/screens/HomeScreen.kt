package com.faharix.zappo.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.faharix.zappo.data.Note
import com.faharix.zappo.ui.components.DeleteConfirmationDialog
import com.faharix.zappo.ui.theme.FahariXGradientColors
import java.text.SimpleDateFormat
import java.util.*

enum class ContentType {
    NOTES, TASKS
}

enum class ViewType {
    GRID, LIST
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    notes: List<Note>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNoteClick: (Int) -> Unit,
    onAddNoteClick: () -> Unit,
    onDeleteNote: (Note) -> Unit,
    onToggleTheme: () -> Unit,
    onNavigateToTrash: () -> Unit
) {
    var selectedContentType by remember { mutableStateOf(ContentType.NOTES) }
    var isSearchActive by remember { mutableStateOf(false) }
    var viewType by remember { mutableStateOf(ViewType.GRID) }
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Top App Bar avec gradient FahariX
        FahariXTopBar(
            isSearchActive = isSearchActive,
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onSearchToggle = { isSearchActive = !isSearchActive },
            onToggleTheme = onToggleTheme,
            onNavigateToTrash = onNavigateToTrash,
            viewType = viewType,
            onViewTypeChange = { viewType = it },
            showMenu = showMenu,
            onMenuToggle = { showMenu = !showMenu }
        )

        // Content Type Selector
        ContentTypeSelector(
            selectedType = selectedContentType,
            onTypeChange = { selectedContentType = it }
        )

        // Notes Content
        Box(modifier = Modifier.weight(1f)) {
            NotesContent(
                notes = notes.filter {
                    (selectedContentType == ContentType.NOTES && !it.isTask) ||
                            (selectedContentType == ContentType.TASKS && it.isTask)
                },
                searchQuery = searchQuery,
                viewType = viewType,
                onNoteClick = onNoteClick,
                onDeleteNote = onDeleteNote
            )

            // FAB avec gradient FahariX
            FloatingActionButton(
                onClick = onAddNoteClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = if (selectedContentType == ContentType.NOTES)
                        Icons.Outlined.Edit else Icons.Outlined.Add,
                    contentDescription = if (selectedContentType == ContentType.NOTES)
                        "Nouvelle note" else "Nouvelle tâche"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FahariXTopBar(
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchToggle: () -> Unit,
    onToggleTheme: () -> Unit,
    onNavigateToTrash: () -> Unit,
    viewType: ViewType,
    onViewTypeChange: (ViewType) -> Unit,
    showMenu: Boolean,
    onMenuToggle: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (isSearchActive) 8.dp else 2.dp
    ) {
        // Gradient background pour la top bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            if (isSearchActive) {
                // Search Bar
                SearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onSearch = { },
                    active = true,
                    onActiveChange = { if (!it) onSearchToggle() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Rechercher dans FahariX Notes") },
                    leadingIcon = {
                        IconButton(onClick = onSearchToggle) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Retour",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Effacer",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                ) {}
            } else {
                // Normal Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Menu button
                    IconButton(onClick = onMenuToggle) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Title avec style FahariX
                    Text(
                        text = "FahariX Notes",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )

                    // Actions
                    IconButton(onClick = onSearchToggle) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Rechercher",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = {
                        onViewTypeChange(
                            if (viewType == ViewType.GRID) ViewType.LIST else ViewType.GRID
                        )
                    }) {
                        Icon(
                            imageVector = if (viewType == ViewType.GRID)
                                Icons.Outlined.ViewAgenda else Icons.Outlined.ViewModule,
                            contentDescription = "Changer la vue",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // More options
                    Box {
                        IconButton(onClick = { /* Show more menu */ }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Plus d'options",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        DropdownMenu(
                            expanded = false,
                            onDismissRequest = { }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Corbeille") },
                                onClick = onNavigateToTrash,
                                leadingIcon = {
                                    Icon(Icons.Outlined.Delete, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Paramètres") },
                                onClick = { },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Settings, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Thème") },
                                onClick = onToggleTheme,
                                leadingIcon = {
                                    Icon(
                                        if (MaterialTheme.colorScheme.isLight())
                                            Icons.Outlined.DarkMode
                                        else
                                            Icons.Outlined.LightMode,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContentTypeSelector(
    selectedType: ContentType,
    onTypeChange: (ContentType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        FilterChip(
            onClick = { onTypeChange(ContentType.NOTES) },
            label = { Text("Notes") },
            selected = selectedType == ContentType.NOTES,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            modifier = Modifier.padding(end = 8.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        FilterChip(
            onClick = { onTypeChange(ContentType.TASKS) },
            label = { Text("Listes") },
            selected = selectedType == ContentType.TASKS,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.CheckBox,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )
    }
}

@Composable
fun NotesContent(
    notes: List<Note>,
    searchQuery: String,
    viewType: ViewType,
    onNoteClick: (Int) -> Unit,
    onDeleteNote: (Note) -> Unit
) {
    val filteredNotes = if (searchQuery.isBlank()) {
        notes
    } else {
        notes.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.content.contains(searchQuery, ignoreCase = true)
        }
    }

    if (filteredNotes.isEmpty()) {
        EmptyState(
            message = if (searchQuery.isEmpty())
                "Vos notes FahariX apparaîtront ici"
            else
                "Aucune note trouvée"
        )
    } else {
        when (viewType) {
            ViewType.GRID -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalItemSpacing = 8.dp
                ) {
                    items(filteredNotes) { note ->
                        FahariXNoteCard(
                            note = note,
                            onClick = { onNoteClick(note.id) },
                            onDelete = { onDeleteNote(note) }
                        )
                    }
                }
            }
            ViewType.LIST -> {
                // Implementation for list view
            }
        }
    }
}

@Composable
fun FahariXNoteCard(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (note.isTask)
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // Accent bar en haut de la carte
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (note.isTask)
                            listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.tertiary)
                        else
                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                    )
                )
        )

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Title
            if (note.title.isNotEmpty()) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Content
            if (note.content.isNotEmpty()) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date
                Text(
                    text = dateFormat.format(note.modifiedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                // Actions
                Row {
                    if (note.isTask) {
                        Icon(
                            imageVector = if (note.isCompleted)
                                Icons.Default.CheckCircle
                            else
                                Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = if (note.isCompleted)
                                MaterialTheme.colorScheme.secondary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Supprimer",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            note = note,
            onConfirm = {
                onDelete()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icône avec gradient FahariX
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            )
                        ),
                        shape = RoundedCornerShape(40.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun ColorScheme.isLight(): Boolean {
    return this.background.luminance() > 0.5
}