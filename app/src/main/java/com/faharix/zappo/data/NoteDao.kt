package com.faharix.zappo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY modifiedAt DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY modifiedAt DESC")
    fun searchNotes(query: String): Flow<List<Note>>

    @Query("SELECT DISTINCT folder FROM notes WHERE folder IS NOT NULL ORDER BY folder")
    fun getAllFolders(): Flow<List<String>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?

    @Query("SELECT * FROM notes WHERE isInTrash = 0 ORDER BY modifiedAt DESC")
    fun getAllActiveNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE isInTrash = 1 ORDER BY deletedAt DESC")
    fun getAllTrashedNotes(): Flow<List<Note>>
    // Rechercher parmi les notes actives
    @Query("SELECT * FROM notes WHERE isInTrash = 0 AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%') ORDER BY modifiedAt DESC")
    fun searchActiveNotes(query: String): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Query("UPDATE notes SET isInTrash = 1, deletedAt = :deletedAt WHERE id = :id")
    suspend fun moveToTrash(id: Int, deletedAt: Long)

    // Restaurer une note de la corbeille
    @Query("UPDATE notes SET isInTrash = 0, deletedAt = NULL WHERE id = :id")
    suspend fun restoreFromTrash(id: Int)

    // Vider la corbeille (supprimer définitivement toutes les notes dans la corbeille)
    @Query("DELETE FROM notes WHERE isInTrash = 1")
    suspend fun emptyTrash()

    @Delete
    suspend fun deleteNote(note: Note)
}