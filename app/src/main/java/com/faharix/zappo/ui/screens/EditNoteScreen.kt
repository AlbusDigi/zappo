package com.faharix.zappo.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.faharix.zappo.data.Note
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    note: Note?,
    contentType: ContentType = ContentType.NOTES,
    onSaveNote: (String, String, String?, Boolean, Boolean, Date?, List<String>, String?, Long?, String?) -> Unit,
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


    val context = LocalContext.current

    // Launcher for picking images from the gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        imageUris = imageUris + uris.map { it.toString() }
    }

    // Launcher for taking a picture with the camera
    // TODO: Implement camera launcher and permission handling
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview() // Or TakePicture for saving to a file
    ) { bitmap ->
        // Handle the bitmap (e.g., save it to a file and get the URI)
        // For now, let's assume we get a URI directly or after saving
        // This part needs proper implementation for saving the image and getting its URI
        bitmap?.let {
            // Placeholder: Convert bitmap to URI (this is not a real URI that persists)
            // You'll need to save the bitmap to storage and get a content URI
            // For demonstration, adding a placeholder string.
            // In a real app, save bitmap to a file and use its URI.
            // val uri = saveBitmapAndGetUri(context, it) // You'd need this helper
            // imageUris = imageUris + uri.toString()
        }
    }


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
                    if (title.isBlank() && content.isBlank()){
                        return@FloatingActionButton
                    }
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
                        reminderRecurrence
                    )
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

            // Text Formatting Toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()) // Make it scrollable if icons don't fit
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Basic formatting
                IconButton(onClick = { Log.d("Formatting", "Bold clicked") }) {
                    Icon(Icons.Default.FormatBold, contentDescription = "Bold")
                }
                IconButton(onClick = { Log.d("Formatting", "Italic clicked") }) {
                    Icon(Icons.Default.FormatItalic, contentDescription = "Italic")
                }
                IconButton(onClick = { Log.d("Formatting", "Underline clicked") }) {
                    Icon(Icons.Default.FormatUnderlined, contentDescription = "Underline")
                }
                // Lists
                IconButton(onClick = { Log.d("Formatting", "Bulleted List clicked") }) {
                    Icon(Icons.Default.FormatListBulleted, contentDescription = "Bulleted List")
                }
                IconButton(onClick = { Log.d("Formatting", "Numbered List clicked") }) {
                    Icon(Icons.Default.FormatListNumbered, contentDescription = "Numbered List")
                }
                // Headings (placeholder for dropdown/dialog)
                IconButton(onClick = { Log.d("Formatting", "Headings clicked") }) {
                    Icon(Icons.Default.Title, contentDescription = "Headings")
                }
                // Text Color (placeholder for dialog)
                IconButton(onClick = { Log.d("Formatting", "Text Color clicked") }) {
                    Icon(Icons.Default.FormatColorText, contentDescription = "Text Color")
                }
                // Highlight (placeholder for dialog)
                IconButton(onClick = { Log.d("Formatting", "Highlight clicked") }) {
                    Icon(Icons.Default.Highlight, contentDescription = "Highlight Text")
                }
                // Alignment
                IconButton(onClick = { Log.d("Formatting", "Align Left clicked") }) {
                    Icon(Icons.AutoMirrored.Filled.FormatAlignLeft, contentDescription = "Align Left")
                }
                IconButton(onClick = { Log.d("Formatting", "Align Center clicked") }) {
                    Icon(Icons.Default.FormatAlignCenter, contentDescription = "Align Center")
                }
                IconButton(onClick = { Log.d("Formatting", "Align Right clicked") }) {
                    Icon(Icons.AutoMirrored.Filled.FormatAlignRight, contentDescription = "Align Right")
                }
                 IconButton(onClick = { Log.d("Formatting", "LaTeX/Math clicked") }) {
                    Icon(Icons.Default.Functions, contentDescription = "LaTeX/Math")
                }
            }


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

            Spacer(modifier = Modifier.height(16.dp))

            // Image selection buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Select from Gallery")
                }
                IconButton(onClick = { /* TODO: Implement camera permission and launch */ cameraLauncher.launch(null) }) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Take Photo")
                }
                IconButton(onClick = { showImageUrlDialog = true }) {
                    Icon(Icons.Default.Link, contentDescription = "Add Image URL")
                }
            }

            // Image preview area
            if (imageUris.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(imageUris) { uriString ->
                        Card(
                            modifier = Modifier
                                .size(100.dp)
                                .padding(4.dp)
                        ) {
                            // Placeholder for image display
                            // In a real app, use Coil or Glide to load the image from URI
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text("Img: ${uriString.takeLast(10)}") // Show last 10 chars of URI
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reminder Settings
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .clickable { showReminderDatePickerDialog = true }
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = "Set Reminder",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = reminderDateTime?.let {
                        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(it))
                    } ?: "Set Reminder",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .clickable { showRecurrenceDialog = true }
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Set Recurrence",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = reminderRecurrence ?: "Set Recurrence",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        }

        // Dialog for Image URL input
        if (showImageUrlDialog) {
            AlertDialog(
                onDismissRequest = { showImageUrlDialog = false },
                title = { Text("Add Image URL") },
                text = {
                    TextField(
                        value = imageUrlInput,
                        onValueChange = { imageUrlInput = it },
                        label = { Text("Image URL") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (imageUrlInput.isNotBlank()) {
                            imageUris = imageUris + imageUrlInput
                            imageUrlInput = ""
                        }
                        showImageUrlDialog = false
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    Button(onClick = { showImageUrlDialog = false }) {
                        Text("Cancel")
                    }
                }
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

        // Reminder Date Picker Dialog
        if (showReminderDatePickerDialog) {
            ReminderDatePickerDialog(
                onDismiss = { showReminderDatePickerDialog = false },
                onDateTimeSelected = { dateTimeMillis ->
                    reminderDateTime = dateTimeMillis
                    showReminderDatePickerDialog = false
                }
            )
        }

        // Recurrence Selection Dialog
        if (showRecurrenceDialog) {
            RecurrenceSelectionDialog(
                currentRecurrence = reminderRecurrence,
                onRecurrenceSelected = {
                    reminderRecurrence = if (it == "None") null else it
                    showRecurrenceDialog = false
                },
                onDismiss = { showRecurrenceDialog = false }
            )
        }
    }
}

@Composable
fun ReminderDatePickerDialog(
    onDismiss: () -> Unit,
    onDateTimeSelected: (Long) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var selectedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var selectedHour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }

    // Simple Date Picker (can be replaced with Material3 DatePicker when stable or a library)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Reminder Date and Time") },
        text = {
            Column {
                // Date selection (simplified)
                Text("Date: $selectedDay/${selectedMonth + 1}/$selectedYear")
                Button(onClick = {
                    android.app.DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            selectedYear = year
                            selectedMonth = month
                            selectedDay = dayOfMonth
                        },
                        selectedYear,
                        selectedMonth,
                        selectedDay
                    ).show()
                }) {
                    Text("Select Date")
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Time selection (simplified)
                Text("Time: $selectedHour:$selectedMinute")
                Button(onClick = {
                    android.app.TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            selectedHour = hourOfDay
                            selectedMinute = minute
                        },
                        selectedHour,
                        selectedMinute,
                        true // 24 hour view
                    ).show()
                }) {
                    Text("Select Time")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    calendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute)
                    onDateTimeSelected(calendar.timeInMillis)
                }
            ) {
                Text("Set Reminder")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RecurrenceSelectionDialog(
    currentRecurrence: String?,
    onRecurrenceSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val recurrenceOptions = listOf("None", "Daily", "Weekly", "Monthly", "Yearly")
    var expanded by remember { mutableStateOf(false) } // Not used for AlertDialog, but good for DropdownMenu

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Recurrence") },
        text = {
            Column {
                recurrenceOptions.forEach { option ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onRecurrenceSelected(option) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentRecurrence == option || (currentRecurrence == null && option == "None"),
                            onClick = { onRecurrenceSelected(option) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(option)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
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
                    imageVector = Icons.AutoMirrored.Filled.Note,
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

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 2.dp,

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

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 2.dp,
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