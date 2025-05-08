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
                    val trashedNotes by notesViewModel.trashedNotes.collectAsState(initial = emptyList())
                    // Changement de notes à activeNotes pour correspondre à l'implémentation de la corbeille
                    val note by notesViewModel.notes.collectAsState()
                    val searchQuery by notesViewModel.searchQuery.collectAsState()

                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                // Utiliser activeNotes au lieu de notes
                                notes = note,
                                searchQuery = searchQuery,
                                onSearchQueryChange = notesViewModel::updateSearchQuery,
                                onNoteClick = { noteId ->
                                    navController.navigate("edit/$noteId")
                                },
                                onAddNoteClick = {
                                    navController.navigate("edit/-1")
                                },
                                // Changer onDeleteNote à onMoveToTrash pour utiliser la corbeille
                                onMoveToTrash = notesViewModel::moveToTrash,
                                // Ajouter le paramètre manquant pour naviguer vers la corbeille
                                onNavigateToTrash = {
                                    navController.navigate("trash")
                                },
                                onToggleTheme = { darkTheme = !darkTheme }
                            )
                        }
                        composable("edit/{noteId}") { backStackEntry ->
                            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull() ?: -1
                            val note = if (noteId != -1) {
                                // Utiliser activeNotes au lieu de notes
                                note.find { it.id == noteId }
                            } else null

                            EditNoteScreen(
                                note = note,
                                contentType = if (note?.isTask == true) ContentType.TASKS else ContentType.NOTES,
                                onSaveNote = { title, content, folder, isTask, isCompleted, dueDate ->
                                    if (noteId == -1) {
                                        notesViewModel.addNote(title, content, folder, isTask, isCompleted, dueDate)
                                    } else {
                                        notesViewModel.updateNote(noteId, title, content, folder, isTask, isCompleted, dueDate)
                                    }
                                    navController.popBackStack()
                                },
                                onCancel = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("trash"){
                            TrashScreen(
                                trashedNotes = trashedNotes,
                                onRestoreNote = notesViewModel::restoreFromTrash,
                                onDeletePermanently = notesViewModel::deleteNotePermanently,
                                onEmptyTrash = notesViewModel::emptyTrash,
                                onNavigateBack = {
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