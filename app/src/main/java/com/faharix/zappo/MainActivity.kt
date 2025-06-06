package com.faharix.zappo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.faharix.zappo.ui.screens.ContentType
import com.faharix.zappo.ui.screens.EditNoteScreen
import com.faharix.zappo.ui.screens.HomeScreen
import com.faharix.zappo.ui.screens.TrashScreen
import com.faharix.zappo.ui.theme.ZappoTheme
import com.faharix.zappo.viewmodel.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemInDarkTheme = isSystemInDarkTheme()
            var darkTheme by remember { mutableStateOf(systemInDarkTheme) }

            ZappoTheme(darkTheme = darkTheme) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val notesViewModel: NotesViewModel = hiltViewModel()
                    val notes by notesViewModel.notes.collectAsState(initial = emptyList())
                    val searchQuery by notesViewModel.searchQuery.collectAsState()

                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                notes = notes,
                                searchQuery = searchQuery,
                                onSearchQueryChange = notesViewModel::updateSearchQuery,
                                onNoteClick = { noteId ->
                                    navController.navigate("edit/$noteId")
                                },
                                onAddNoteClick = {
                                    navController.navigate("edit/-1")
                                },
                                onDeleteNote = notesViewModel::deleteNote, // This now means "move to trash"
                                onToggleTheme = { darkTheme = !darkTheme },
                                onNavigateToTrash = { navController.navigate("trash") } // New lambda
                            )
                        }
                        composable("trash") {
                            TrashScreen(
                                notesViewModel = notesViewModel, // Pass the existing view model
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("edit/{noteId}") { backStackEntry ->
                            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull() ?: -1
                            val note = if (noteId != -1) {
                                notes.find { it.id == noteId }
                            } else null

                            // Modification de la note ou tâche
                            EditNoteScreen(
                                note = note,
                                contentType = if (note?.isTask == true) ContentType.TASKS else ContentType.NOTES,
                                onSaveNote = { title, content, folder, isTask, isCompleted, dueDate, imageUris, textFormatting, reminderDateTime, reminderRecurrence, audioFilePath ->
                                    if (noteId == -1) {
                                        notesViewModel.addNote(title, content, folder, isTask, isCompleted, dueDate, imageUris, textFormatting, reminderDateTime, reminderRecurrence, audioFilePath)
                                    } else {
                                        notesViewModel.updateNote(noteId, title, content, folder, isTask, isCompleted, dueDate, imageUris, textFormatting, reminderDateTime, reminderRecurrence, audioFilePath)
                                    }
                                    navController.popBackStack()
                                },
                                onCancel = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}