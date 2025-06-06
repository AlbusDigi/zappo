package com.faharix.zappo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.faharix.zappo.data.Note
import com.faharix.zappo.viewmodel.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    notesViewModel: NotesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val deletedNotes by notesViewModel.deletedNotes.collectAsState()
    var showEmptyTrashDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trash") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (deletedNotes.isNotEmpty()) {
                        IconButton(onClick = { showEmptyTrashDialog = true }) {
                            Icon(Icons.Filled.DeleteForever, contentDescription = "Empty Trash")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (deletedNotes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Trash is empty")
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(deletedNotes) { note ->
                        TrashNoteItem(
                            note = note,
                            onRestore = { notesViewModel.restoreNote(note) },
                            onDeletePermanently = { notesViewModel.permanentlyDeleteNote(note) }
                        )
                        Divider()
                    }
                }
            }
        }
    }

    if (showEmptyTrashDialog) {
        AlertDialog(
            onDismissRequest = { showEmptyTrashDialog = false },
            title = { Text("Empty Trash?") },
            text = { Text("Are you sure you want to permanently delete all notes in the trash? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        deletedNotes.forEach { notesViewModel.permanentlyDeleteNote(it) }
                        showEmptyTrashDialog = false
                    }
                ) { Text("Empty Trash") }
            },
            dismissButton = {
                TextButton(onClick = { showEmptyTrashDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun TrashNoteItem(
    note: Note,
    onRestore: () -> Unit,
    onDeletePermanently: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(note.title, maxLines = 1) },
        supportingContent = { Text(note.content, maxLines = 2) },
        trailingContent = {
            Row {
                IconButton(onClick = onRestore) {
                    Icon(Icons.Filled.RestoreFromTrash, contentDescription = "Restore")
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Filled.DeleteForever, contentDescription = "Delete Permanently")
                }
            }
        }
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Permanently?") },
            text = { Text("Are you sure you want to permanently delete this note? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeletePermanently()
                        showDeleteDialog = false
                    }
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}
