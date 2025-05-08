package com.faharix.zappo.repository

import com.faharix.zappo.data.Note
import com.faharix.zappo.data.NoteDao
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(private val noteDao: NoteDao) {

    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    fun getAllActiveNotes(): Flow<List<Note>> = noteDao.getAllActiveNotes()

    fun getAllTrashedNotes(): Flow<List<Note>> = noteDao.getAllTrashedNotes()

    fun searchActiveNotes(query: String): Flow<List<Note>> = noteDao.searchActiveNotes(query)

    fun searchNotes(query: String): Flow<List<Note>> = noteDao.searchNotes(query)

    // Utiliser la méthode du DAO pour obtenir tous les dossiers uniques
    fun getAllFolders(): Flow<List<String>> = noteDao.getAllFolders()

    suspend fun getNoteById(id: Int): Note? = noteDao.getNoteById(id)

    suspend fun insertNote(
        title: String,
        content: String,
        folder: String? = null,
        isTask: Boolean = false,
        isCompleted: Boolean = false
    ): Long {
        val note = Note(
            title = title,
            content = content,
            folder = folder,
            isTask = isTask,
            isCompleted = isCompleted,
            createdAt = Date(),
            modifiedAt = Date()
        )
        return noteDao.insertNote(note)
    }

    // Dans NoteRepository.kt, mettez à jour les méthodes pour prendre en charge la date d'échéance
    suspend fun insertNote(
        title: String,
        content: String,
        folder: String? = null,
        isTask: Boolean = false,
        isCompleted: Boolean = false,
        dueDate: Date? = null
    ): Long {
        val note = Note(
            title = title,
            content = content,
            folder = folder,
            isTask = isTask,
            isCompleted = isCompleted,
            dueDate = dueDate,
            createdAt = Date(),
            modifiedAt = Date()
        )
        return noteDao.insertNote(note)
    }

    suspend fun updateNote(
        id: Int,
        title: String,
        content: String,
        folder: String? = null,
        isTask: Boolean = false,
        isCompleted: Boolean = false,
        dueDate: Date? = null
    ) {
        val note = noteDao.getNoteById(id)
        note?.let {
            val updatedNote = it.copy(
                title = title,
                content = content,
                folder = folder,
                isTask = isTask,
                isCompleted = isCompleted,
                dueDate = dueDate,
                modifiedAt = Date()
            )
            noteDao.updateNote(updatedNote)
        }
    }
    // Mettre une note dans la corbeille
    suspend fun moveToTrash(note: Note) {
        noteDao.moveToTrash(note.id, Date().time)
    }
    // Restaurer une note de la corbeille
    suspend fun restoreFromTrash(note: Note) {
        noteDao.restoreFromTrash(note.id)
    }

    // Vider la corbeille
    suspend fun emptyTrash() {
        noteDao.emptyTrash()
    }

    // Supprimer définitivement une note
    suspend fun deleteNotePermanently(note: Note) {
        noteDao.deleteNote(note)
    }
}
