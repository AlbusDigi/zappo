package com.faharix.zappo.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val folder: String? = null,  // Nouveau champ pour le dossier
    val isTask: Boolean = false, // Indique si c'est une note ou une tâche
    val isCompleted: Boolean = false,
    val dueDate: Date? = null,// Pour les tâches uniquement
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date()
)
