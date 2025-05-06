package com.faharix.zappo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faharix.zappo.data.Note
import com.faharix.zappo.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Dans NotesViewModel.kt
    @OptIn(ExperimentalCoroutinesApi::class)
    val notes = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            repository.getAllNotes()
        } else {
            repository.searchNotes(query)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Obtenir tous les dossiers uniques
    val folders = repository.getAllFolders()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addNote(title: String, content: String, folder: String? = null, isTask: Boolean = false, isCompleted: Boolean = false) {
        viewModelScope.launch {
            repository.insertNote(title, content, folder, isTask, isCompleted)
        }
    }
    fun addNote(
        title: String,
        content: String,
        folder: String? = null,
        isTask: Boolean = false,
        isCompleted: Boolean = false,
        dueDate: Date? = null
    ) {
        viewModelScope.launch {
            repository.insertNote(title, content, folder, isTask, isCompleted, dueDate)
        }
    }

    fun updateNote(
        id: Int,
        title: String,
        content: String,
        folder: String? = null,
        isTask: Boolean = false,
        isCompleted: Boolean = false,
        dueDate: Date? = null
    ) {
        viewModelScope.launch {
            repository.updateNote(id, title, content, folder, isTask, isCompleted, dueDate)
        }
    }

    fun updateNote(id: Int, title: String, content: String, folder: String? = null, isTask: Boolean = false, isCompleted: Boolean = false) {
        viewModelScope.launch {
            repository.updateNote(id, title, content, folder, isTask, isCompleted)
        }
    }

    fun toggleTaskCompletion(note: Note) {
        if (note.isTask) {
            viewModelScope.launch {
                repository.updateNote(
                    note.id,
                    note.title,
                    note.content,
                    note.folder,
                    true,
                    !note.isCompleted
                )
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
}