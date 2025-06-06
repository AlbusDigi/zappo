package com.faharix.zappo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY modifiedAt DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%') ORDER BY modifiedAt DESC")
    fun searchNotes(query: String): Flow<List<Note>>

    @Query("SELECT DISTINCT folder FROM notes WHERE folder IS NOT NULL ORDER BY folder")
    fun getAllFolders(): Flow<List<String>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    // Soft delete a note
    @Query("UPDATE notes SET isDeleted = 1, modifiedAt = :timestamp WHERE id = :noteId")
    suspend fun deleteNote(noteId: Int, timestamp: Date = Date())

    // Permanently delete a note
    @Delete
    suspend fun permanentlyDeleteNote(note: Note)

    // Restore a soft-deleted note
    @Query("UPDATE notes SET isDeleted = 0, modifiedAt = :timestamp WHERE id = :noteId")
    suspend fun restoreNote(noteId: Int, timestamp: Date = Date())

    // Get all soft-deleted notes
    @Query("SELECT * FROM notes WHERE isDeleted = 1 ORDER BY modifiedAt DESC")
    fun getDeletedNotes(): Flow<List<Note>>
}