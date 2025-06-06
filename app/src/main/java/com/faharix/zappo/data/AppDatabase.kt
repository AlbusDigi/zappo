package com.faharix.zappo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Note::class], version = 6) // Version incremented to 6
@TypeConverters(Converters::class) // Pour les conversions de Date
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Définition de la migration de la version 1 à 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {

                database.execSQL("ALTER TABLE notes ADD COLUMN folder TEXT")
                database.execSQL("ALTER TABLE notes ADD COLUMN isTask INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE notes ADD COLUMN isCompleted INTEGER NOT NULL DEFAULT 0")

            }
        }
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Ajouter la colonne dueDate
                database.execSQL("ALTER TABLE notes ADD COLUMN dueDate INTEGER")
            }
        }

        // Migration from version 3 to 4: Add isDeleted column
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE notes ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Migration from version 4 to 5: Add imageUris, textFormatting, reminderDateTime, reminderRecurrence columns
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE notes ADD COLUMN imageUris TEXT") // Storing List<String> as TEXT
                database.execSQL("ALTER TABLE notes ADD COLUMN textFormatting TEXT")
                database.execSQL("ALTER TABLE notes ADD COLUMN reminderDateTime INTEGER") // Storing Long? as INTEGER
                database.execSQL("ALTER TABLE notes ADD COLUMN reminderRecurrence TEXT")
            }
        }

        // Migration from version 5 to 6: Add audioFilePath column
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE notes ADD COLUMN audioFilePath TEXT") // Storing String? as TEXT, allows NULL
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notes_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6) // Added MIGRATION_5_6
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}