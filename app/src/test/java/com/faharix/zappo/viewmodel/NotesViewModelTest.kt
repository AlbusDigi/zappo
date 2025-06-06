package com.faharix.zappo.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.faharix.zappo.data.Note
import com.faharix.zappo.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.util.Date
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
class NotesViewModelTest {

    // Rule for LiveData and other architecture components
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Test dispatcher for coroutines
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockRepository: NoteRepository

    @Captor
    private lateinit var noteArgumentCaptor: ArgumentCaptor<Note>

    private lateinit var viewModel: NotesViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = NotesViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addNote should call repository insertNote with correct parameters including new fields`() = runTest {
        // Given
        val title = "Test Title"
        val content = "Test Content"
        val folder = "Test Folder"
        val isTask = true
        val isCompleted = false
        val dueDate = Date()
        val imageUris = listOf("uri1", "uri2")
        val textFormatting = "{\"bold\": true}"
        val reminderDateTime = System.currentTimeMillis()
        val reminderRecurrence = "Daily"
        val audioFilePath = "path/to/audio.mp3"

        // When
        viewModel.addNote(
            title, content, folder, isTask, isCompleted, dueDate,
            imageUris, textFormatting, reminderDateTime, reminderRecurrence, audioFilePath
        )
        testDispatcher.scheduler.advanceUntilIdle() // Ensure coroutine launched by viewModelScope completes

        // Then
        verify(mockRepository).insertNote(
            title, content, folder, isTask, isCompleted, dueDate,
            imageUris, textFormatting, reminderDateTime, reminderRecurrence, audioFilePath
        )
    }

    @Test
    fun `updateNote should call repository updateNote with correct parameters including new fields`() = runTest {
        // Given
        val noteId = 1
        val title = "Updated Title"
        val content = "Updated Content"
        val folder = "Updated Folder"
        val isTask = false
        val isCompleted = true
        val dueDate = Date()
        val imageUris = listOf("uri3", "uri4")
        val textFormatting = "{\"italic\": true}"
        val reminderDateTime = System.currentTimeMillis() + 10000
        val reminderRecurrence = "Weekly"
        val audioFilePath = "path/to/updated_audio.mp3"

        // When
        viewModel.updateNote(
            noteId, title, content, folder, isTask, isCompleted, dueDate,
            imageUris, textFormatting, reminderDateTime, reminderRecurrence, audioFilePath
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(mockRepository).updateNote(
            noteId, title, content, folder, isTask, isCompleted, dueDate,
            imageUris, textFormatting, reminderDateTime, reminderRecurrence, audioFilePath
        )
    }

    @Test
    fun `toggleTaskCompletion should preserve all fields including new ones`() = runTest {
        // Given
        val originalDueDate = Date()
        val originalImageUris = listOf("image1.jpg", "image2.png")
        val originalTextFormatting = "{\"bold\": true, \"color\": \"#FF0000\"}"
        val originalReminderDateTime = System.currentTimeMillis()
        val originalReminderRecurrence = "Daily"
        val originalAudioFilePath = "path/to/original_audio.mp3"

        val testNote = Note(
            id = 1,
            title = "Task Title",
            content = "Task Content",
            folder = "Task Folder",
            isTask = true,
            isCompleted = false,
            dueDate = originalDueDate,
            imageUris = originalImageUris,
            textFormatting = originalTextFormatting,
            reminderDateTime = originalReminderDateTime,
            reminderRecurrence = originalReminderRecurrence,
            audioFilePath = originalAudioFilePath,
            createdAt = Date(),
            modifiedAt = Date()
        )
        val expectedIsCompleted = !testNote.isCompleted

        // When
        viewModel.toggleTaskCompletion(testNote)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(mockRepository).updateNote(
            testNote.id,
            testNote.title,
            testNote.content,
            testNote.folder,
            testNote.isTask,
            expectedIsCompleted, // isCompleted should be toggled
            originalDueDate,
            originalImageUris,
            originalTextFormatting,
            originalReminderDateTime,
            originalReminderRecurrence,
            originalAudioFilePath
        )
    }
}
