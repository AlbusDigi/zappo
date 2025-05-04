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

    fun searchNotes(query: String): Flow<List<Note>> = noteDao.searchNotes(query)

    suspend fun getNoteById(id: Int): Note? = noteDao.getNoteById(id)

    suspend fun insertNote(title: String, content: String): Long {
        val note = Note(
            title = title,
            content = content,
            createdAt = Date(),
            modifiedAt = Date()
        )
        return noteDao.insertNote(note)
    }

    suspend fun updateNote(id: Int, title: String, content: String) {
        val note = noteDao.getNoteById(id)
        note?.let {
            val updatedNote = it.copy(
                title = title,
                content = content,
                modifiedAt = Date()
            )
            noteDao.updateNote(updatedNote)
        }
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }
}