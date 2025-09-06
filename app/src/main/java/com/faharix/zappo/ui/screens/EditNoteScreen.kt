package com.faharix.zappo.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.faharix.zappo.data.Note
import com.faharix.zappo.ui.theme.FahariXGradientColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    note: Note?,
    contentType: ContentType = ContentType.NOTES,
    onSaveNote: (String, String, String?, Boolean, Boolean, Date?, List<String>, String?, Long?, String?, String?) -> Unit,
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
    var imageUris by remember { mutableStateOf(note?.imageUris ?: emptyList<String>()) }
    var showImageUrlDialog by remember { mutableStateOf(false) }
    var imageUrlInput by remember { mutableStateOf("") }
    var textFormatting by remember { mutableStateOf(note?.textFormatting) }
    var reminderDateTime by remember { mutableStateOf(note?.reminderDateTime) }
    var reminderRecurrence by remember { mutableStateOf(note?.reminderRecurrence) }
    var showReminderDatePickerDialog by remember { mutableStateOf(false) }
    var showRecurrenceDialog by remember { mutableStateOf(false) }
    var audioFilePath by remember { mutableStateOf(note?.audioFilePath) }
    var showDeleteAudioDialog by remember { mutableStateOf(false) }
    var showToolbar by remember { mutableStateOf(false) }

    val isNewNote = note == null
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Top Bar avec gradient FahariX
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent,
            shadowElevation = if (showToolbar) 4.dp else 2.dp
        ) {
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Actions rapides avec couleurs FahariX
                    IconButton(onClick = { /* Pin note */ }) {
                        Icon(
                            imageVector = Icons.Outlined.PushPin,
                            contentDescription = "Épingler",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = { showReminderDatePickerDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationAdd,
                            contentDescription = "Rappel",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }

                    IconButton(onClick = { /* Archive */ }) {
                        Icon(
                            imageVector = Icons.Outlined.Archive,
                            contentDescription = "Archiver",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }

        // Contenu principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Type selector avec style FahariX
            if (isNewNote) {
                FahariXTypeSelectorStyle(
                    isTask = isTask,
                    onTypeChange = { isTask = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Folder chip avec gradient
            if (folder.isNotEmpty()) {
                FahariXFolderChip(
                    folderName = folder,
                    onClick = { showFolderDialog = true }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Task completion avec style FahariX
            AnimatedVisibility(visible = isTask) {
                FahariXTaskCompletionRow(
                    isCompleted = isCompleted,
                    onToggle = { isCompleted = it }
                )
            }

            // Title field avec style FahariX
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = {
                    Text(
                        "Titre",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Content field avec style FahariX
            TextField(
                value = content,
                onValueChange = { content = it },
                placeholder = {
                    Text(
                        if (isTask) "Créer une liste..." else "Prendre une note...",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 200.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Images preview avec style FahariX
            if (imageUris.isNotEmpty()) {
                FahariXImagePreviewSection(imageUris = imageUris)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Audio preview avec style FahariX
            audioFilePath?.let {
                FahariXAudioPreviewSection(
                    audioPath = it,
                    onDelete = { showDeleteAudioDialog = true }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom toolbar avec gradient FahariX
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent,
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                            )
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Toolbar toggle
                    IconButton(onClick = { showToolbar = !showToolbar }) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Plus d'options",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Save button avec style FahariX
                    Button(
                        onClick = {
                            if (title.isNotBlank() || content.isNotBlank()) {
                                onSaveNote(
                                    title.trim(),
                                    content.trim(),
                                    if (folder.isBlank()) null else folder.trim(),
                                    isTask,
                                    isCompleted,
                                    dueDate,
                                    imageUris,
                                    textFormatting,
                                    reminderDateTime,
                                    reminderRecurrence,
                                    audioFilePath
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            "Terminé",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Expanded toolbar avec style FahariX
        AnimatedVisibility(visible = showToolbar) {
            FahariXExpandedToolbar(
                onImageFromGallery = { /* Implement */ },
                onImageFromCamera = { /* Implement */ },
                onImageFromUrl = { showImageUrlDialog = true },
                onRecordAudio = { /* Implement */ },
                onSetReminder = { showReminderDatePickerDialog = true },
                onSelectFolder = { showFolderDialog = true }
            )
        }
    }

    // Dialogs avec style FahariX
    if (showFolderDialog) {
        FahariXFolderSelectionDialog(
            currentFolder = folder,
            onFolderSelected = { selectedFolder ->
                folder = selectedFolder
                showFolderDialog = false
            },
            onDismiss = { showFolderDialog = false }
        )
    }

    if (showImageUrlDialog) {
        FahariXImageUrlDialog(
            imageUrlInput = imageUrlInput,
            onImageUrlChange = { imageUrlInput = it },
            onConfirm = {
                if (imageUrlInput.isNotBlank()) {
                    imageUris = imageUris + imageUrlInput
                    imageUrlInput = ""
                }
                showImageUrlDialog = false
            },
            onDismiss = { showImageUrlDialog = false }
        )
    }
}

@Composable
fun FahariXTypeSelectorStyle(
    isTask: Boolean,
    onTypeChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(25.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    )
                )
            )
            .padding(4.dp)
    ) {
        FahariXTypeOption(
            text = "Note",
            icon = Icons.Outlined.Description,
            isSelected = !isTask,
            onClick = { onTypeChange(false) },
            modifier = Modifier.weight(1f),
            selectedColor = MaterialTheme.colorScheme.primary
        )

        FahariXTypeOption(
            text = "Liste",
            icon = Icons.Outlined.CheckBox,
            isSelected = isTask,
            onClick = { onTypeChange(true) },
            modifier = Modifier.weight(1f),
            selectedColor = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun FahariXTypeOption(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) selectedColor.copy(alpha = 0.2f)
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun FahariXFolderChip(
    folderName: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Folder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = folderName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun FahariXTaskCompletionRow(
    isCompleted: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isCompleted) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = onToggle,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.secondary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (isCompleted) "Tâche terminée" else "Tâche en cours",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (isCompleted) {
                    Text(
                        text = "Félicitations ! 🎉",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
fun FahariXImagePreviewSection(imageUris: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhotoLibrary,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Images (${imageUris.size})",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(imageUris) { uri ->
                    Card(
                        modifier = Modifier.size(80.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Image,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FahariXAudioPreviewSection(
    audioPath: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AudioFile,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Enregistrement audio",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Appuyez pour écouter",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Supprimer",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun FahariXExpandedToolbar(
    onImageFromGallery: () -> Unit,
    onImageFromCamera: () -> Unit,
    onImageFromUrl: () -> Unit,
    onRecordAudio: () -> Unit,
    onSetReminder: () -> Unit,
    onSelectFolder: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.1f)
                        )
                    )
                )
        ) {
            LazyRow(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    FahariXToolbarButton(
                        icon = Icons.Outlined.PhotoLibrary,
                        text = "Galerie",
                        onClick = onImageFromGallery,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                item {
                    FahariXToolbarButton(
                        icon = Icons.Outlined.CameraAlt,
                        text = "Photo",
                        onClick = onImageFromCamera,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                item {
                    FahariXToolbarButton(
                        icon = Icons.Outlined.Mic,
                        text = "Audio",
                        onClick = onRecordAudio,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                item {
                    FahariXToolbarButton(
                        icon = Icons.Outlined.NotificationAdd,
                        text = "Rappel",
                        onClick = onSetReminder,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                item {
                    FahariXToolbarButton(
                        icon = Icons.Outlined.Folder,
                        text = "Dossier",
                        onClick = onSelectFolder,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun FahariXToolbarButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = color.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun FahariXImageUrlDialog(
    imageUrlInput: String,
    onImageUrlChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Link,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                "Ajouter une image",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            OutlinedTextField(
                value = imageUrlInput,
                onValueChange = onImageUrlChange,
                label = { Text("URL de l'image") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Annuler")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun FahariXFolderSelectionDialog(
    currentFolder: String,
    onFolderSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newFolderName by remember { mutableStateOf("") }
    var isCreatingNewFolder by remember { mutableStateOf(false) }

    val existingFolders = listOf("Personnel", "Travail", "Projets", "Idées")

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        title = {
            Text(
                "Sélectionner un dossier",
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
        },
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
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            focusedLabelColor = MaterialTheme.colorScheme.secondary,
                            cursorColor = MaterialTheme.colorScheme.secondary
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
                                selectedColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Aucun dossier")
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

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
                                    selectedColor = MaterialTheme.colorScheme.secondary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Outlined.Folder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(folderName)
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    // Option pour créer un nouveau dossier
                    Button(
                        onClick = { isCreatingNewFolder = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.CreateNewFolder, contentDescription = null)
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
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Créer")
                }
            } else {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Fermer")
                }
            }
        },
        dismissButton = {
            if (isCreatingNewFolder) {
                TextButton(
                    onClick = { isCreatingNewFolder = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Annuler")
                }
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}