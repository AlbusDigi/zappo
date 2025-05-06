package com.faharix.zappo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.faharix.zappo.data.Note

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    note: Note?,
    contentType: ContentType = ContentType.NOTES,
    onSaveNote: (String, String, String?, Boolean, Boolean) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    var folder by remember { mutableStateOf(note?.folder ?: "") }
    var isTask by remember { mutableStateOf(note?.isTask ?: (contentType == ContentType.TASKS)) }
    var isCompleted by remember { mutableStateOf(note?.isCompleted ?: false) }
    var showFolderDialog by remember { mutableStateOf(false) }

    val isNewNote = note == null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isNewNote) {
                            if (isTask) "Nouvelle tâche" else "Nouvelle note"
                        } else {
                            if (isTask) "Modifier la tâche" else "Modifier la note"
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                actions = {
                    // Bouton pour changer entre note et tâche
                    IconButton(onClick = { isTask = !isTask }) {
                        Icon(
                            imageVector = if (isTask) Icons.Default.Note else Icons.Default.CheckCircle,
                            contentDescription = if (isTask) "Convertir en note" else "Convertir en tâche"
                        )
                    }

                    // Bouton pour sélectionner un dossier
                    IconButton(onClick = { showFolderDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = "Sélectionner un dossier"
                        )
                    }

                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Annuler"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onSaveNote(title, content, if (folder.isEmpty()) null else folder, isTask, isCompleted) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Sauvegarder",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Afficher le dossier sélectionné s'il y en a un
            if (folder.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Dossier: $folder",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Option de complétion pour les tâches
            if (isTask) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { isCompleted = it }
                    )
                    Text(
                        text = if (isCompleted) "Tâche complétée" else "Tâche à faire",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titre") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Contenu") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = MaterialTheme.typography.bodyLarge
            )
        }

        // Dialogue pour sélectionner ou créer un dossier
        if (showFolderDialog) {
            FolderSelectionDialog(
                currentFolder = folder,
                onFolderSelected = { selectedFolder ->
                    folder = selectedFolder
                    showFolderDialog = false
                },
                onDismiss = { showFolderDialog = false }
            )
        }
    }
}

@Composable
fun FolderSelectionDialog(
    currentFolder: String,
    onFolderSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newFolderName by remember { mutableStateOf("") }
    var isCreatingNewFolder by remember { mutableStateOf(false) }

    // Liste de dossiers existants (à remplacer par une vraie liste de votre base de données)
    val existingFolders = listOf("Personnel", "Travail", "Projets", "Idées")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sélectionner un dossier") },
        text = {
            Column {
                if (isCreatingNewFolder) {
                    OutlinedTextField(
                        value = newFolderName,
                        onValueChange = { newFolderName = it },
                        label = { Text("Nom du nouveau dossier") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                } else {
                    // Option pour aucun dossier
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFolderSelected("") }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentFolder.isEmpty(),
                            onClick = { onFolderSelected("") }
                        )
                        Text("Aucun dossier")
                    }

                    // Liste des dossiers existants
                    existingFolders.forEach { folderName ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onFolderSelected(folderName) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentFolder == folderName,
                                onClick = { onFolderSelected(folderName) }
                            )
                            Text(folderName)
                        }
                    }

                    // Option pour créer un nouveau dossier
                    TextButton(
                        onClick = { isCreatingNewFolder = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CreateNewFolder, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Créer un nouveau dossier")
                    }
                }
            }
        },
        confirmButton = {
            if (isCreatingNewFolder) {
                TextButton(
                    onClick = {
                        if (newFolderName.isNotEmpty()) {
                            onFolderSelected(newFolderName)
                        }
                    },
                    enabled = newFolderName.isNotEmpty()
                ) {
                    Text("Créer")
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Fermer")
                }
            }
        },
        dismissButton = {
            if (isCreatingNewFolder) {
                TextButton(onClick = { isCreatingNewFolder = false }) {
                    Text("Annuler")
                }
            }
        }
    )
}