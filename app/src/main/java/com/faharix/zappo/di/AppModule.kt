package com.faharix.zappo.di

import android.content.Context
import com.faharix.zappo.data.AppDatabase
import com.faharix.zappo.data.NoteDao
import com.faharix.zappo.repository.NoteRepository
import com.google.android.datatransport.runtime.dagger.Module
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideNoteDao(database: AppDatabase): NoteDao {
        return database.noteDao()
    }

    @Singleton
    @Provides
    fun provideNoteRepository(noteDao: NoteDao): NoteRepository {
        return NoteRepository(noteDao)
    }
}