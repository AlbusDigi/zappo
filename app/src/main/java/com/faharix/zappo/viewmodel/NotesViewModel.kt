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
import javax.inject.Inject
import kotlin.text.Typography.dagger

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    @OptIn(ExperimentalCoroutinesApi::class)
    val notes = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            repository.getAllNotes()
        } else {
            repository.searchNotes(query)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            repository.insertNote(title, content)
        }
    }

    fun updateNote(id: Int, title: String, content: String) {
        viewModelScope.launch {
            repository.updateNote(id, title, content)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
}