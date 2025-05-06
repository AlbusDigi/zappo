package com.faharix.zappo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Note::class], version = 2) // Version augmentée à 2
@TypeConverters(Converters::class) // Pour les conversions de Date
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Définition de la migration de la version 1 à 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Ajouter les nouvelles colonnes
                database.execSQL("ALTER TABLE notes ADD COLUMN folder TEXT")
                database.execSQL("ALTER TABLE notes ADD COLUMN isTask INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE notes ADD COLUMN isCompleted INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notes_database"
                )
                    .addMigrations(MIGRATION_1_2) // Utiliser la migration définie
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}