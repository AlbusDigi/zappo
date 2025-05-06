package com.faharix.zappo.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.faharix.zappo.data.Note
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    note: Note?,
    contentType: ContentType = ContentType.NOTES,
    onSaveNote: (String, String, String?, Boolean, Boolean, Date?) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    var folder by remember { mutableStateOf(note?.folder ?: "") }
    var isTask by remember { mutableStateOf(note?.isTask ?: (contentType == ContentType.TASKS)) }
    var isCompleted by remember { mutableStateOf(note?.isCompleted ?: false) }
    var dueDate by remember { mutableStateOf<Date?>(null) }
    var showFolderDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val isNewNote = note == null
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

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
                    containerColor = if (isTask)
                        MaterialTheme.colorScheme.tertiaryContainer
                    else
                        MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = if (isTask)
                        MaterialTheme.colorScheme.onTertiaryContainer
                    else
                        MaterialTheme.colorScheme.onPrimaryContainer
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
                onClick = {
                    onSaveNote(title, content, if (folder.isEmpty()) null else folder, isTask, isCompleted, dueDate)
                },
                containerColor = if (isTask)
                    MaterialTheme.colorScheme.tertiary
                else
                    MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Sauvegarder",
                    tint = if (isTask)
                        MaterialTheme.colorScheme.onTertiary
                    else
                        MaterialTheme.colorScheme.onPrimary
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
            // En-tête avec choix du type (note ou tâche)
            TypeSelector(
                isTask = isTask,
                onTypeChange = { isTask = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Afficher le dossier sélectionné s'il y en a un
            if (folder.isNotEmpty()) {
                FolderChip(
                    folderName = folder,
                    onClick = { showFolderDialog = true }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Options spécifiques aux tâches
            AnimatedVisibility(visible = isTask) {
                Column {
                    // Option de complétion pour les tâches
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable { isCompleted = !isCompleted }
                            .padding(12.dp)
                    ) {
                        Checkbox(
                            checked = isCompleted,
                            onCheckedChange = { isCompleted = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.tertiary,
                                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            text = if (isCompleted) "Tâche complétée" else "Tâche à faire",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Date d'échéance pour les tâches
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable { showDatePicker = true }
                            .padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = "Date d'échéance",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (dueDate != null)
                                "Échéance: ${dateFormat.format(dueDate!!)}"
                            else
                                "Ajouter une date d'échéance",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Champ de titre
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titre") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.titleLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isTask)
                        MaterialTheme.colorScheme.tertiary
                    else
                        MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Champ de contenu
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text(if (isTask) "Description de la tâche" else "Contenu de la note") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isTask)
                        MaterialTheme.colorScheme.tertiary
                    else
                        MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
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

        // Date Picker pour les tâches
        if (showDatePicker) {
            DatePickerDialog(
                onDismiss = { showDatePicker = false },
                onDateSelected = {
                    dueDate = it
                    showDatePicker = false
                }
            )
        }
    }
}

@Composable
fun TypeSelector(
    isTask: Boolean,
    onTypeChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Option Note
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    if (!isTask) MaterialTheme.colorScheme.primaryContainer
                    else Color.Transparent
                )
                .clickable { onTypeChange(false) },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Note,
                    contentDescription = null,
                    tint = if (!isTask)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Note",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (!isTask) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (!isTask)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Option Tâche
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    if (isTask) MaterialTheme.colorScheme.tertiaryContainer
                    else Color.Transparent
                )
                .clickable { onTypeChange(true) },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (isTask)
                        MaterialTheme.colorScheme.onTertiaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tâche",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isTask) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isTask)
                        MaterialTheme.colorScheme.onTertiaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FolderChip(
    folderName: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .wrapContentWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = folderName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
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
                            .padding(bottom = 16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                } else {
                    // Option pour aucun dossier
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onFolderSelected("") }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentFolder.isEmpty(),
                            onClick = { onFolderSelected("") },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Aucun dossier")
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Liste des dossiers existants
                    existingFolders.forEach { folderName ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onFolderSelected(folderName) }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentFolder == folderName,
                                onClick = { onFolderSelected(folderName) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(folderName)
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Option pour créer un nouveau dossier
                    Button(
                        onClick = { isCreatingNewFolder = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
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
                Button(
                    onClick = {
                        if (newFolderName.isNotEmpty()) {
                            onFolderSelected(newFolderName)
                        }
                    },
                    enabled = newFolderName.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
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

@Composable
fun DatePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance()
    var selectedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sélectionner une date d'échéance") },
        text = {
            Column {
                // Ici, vous pourriez implémenter un sélecteur de date personnalisé
                // Pour simplifier, j'utilise des champs numériques
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Jour
                    OutlinedTextField(
                        value = selectedDay.toString(),
                        onValueChange = {
                            val day = it.toIntOrNull() ?: 1
                            if (day in 1..31) selectedDay = day
                        },
                        label = { Text("Jour") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Mois
                    OutlinedTextField(
                        value = (selectedMonth + 1).toString(),
                        onValueChange = {
                            val month = it.toIntOrNull() ?: 1
                            if (month in 1..12) selectedMonth = month - 1
                        },
                        label = { Text("Mois") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Année
                    OutlinedTextField(
                        value = selectedYear.toString(),
                        onValueChange = {
                            val year = it.toIntOrNull() ?: 2023
                            if (year >= 2023) selectedYear = year
                        },
                        label = { Text("Année") },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    calendar.set(selectedYear, selectedMonth, selectedDay)
                    onDateSelected(calendar.time)
                }
            ) {
                Text("Confirmer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}