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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.faharix.zappo.ui.screens.EditNoteScreen
import com.faharix.zappo.ui.screens.HomeScreen
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
                    val notesViewModel: NotesViewModel = viewModel()
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
                                onDeleteNote = notesViewModel::deleteNote,
                                onToggleTheme = { darkTheme = !darkTheme }
                            )
                        }
                        composable("edit/{noteId}") { backStackEntry ->
                            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull() ?: -1
                            val note = if (noteId != -1) {
                                notes.find { it.id == noteId }
                            } else null

                            EditNoteScreen(
                                note = note,
                                onSaveNote = { title, content ->
                                    if (noteId == -1) {
                                        notesViewModel.addNote(title, content)
                                    } else {
                                        notesViewModel.updateNote(noteId, title, content)
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