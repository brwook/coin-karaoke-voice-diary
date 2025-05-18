package com.example.coin_karaoke_voice_diary

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import com.example.coin_karaoke_voice_diary.data.AppDatabase
import com.example.coin_karaoke_voice_diary.data.Recording
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class RecordingDaoTest {
    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = AppDatabase.createInMemory(context)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insert_and_query() = runBlocking {
        val dao = db.recordingDao()
        val recording = Recording(
            filePath = "path/to/file.wav",
            thumbnailPath = "path/to/thumbnail.jpg",
            title = "Song Title",
            artist = "Artist",
            recordedAt = 1L
        )
        dao.insert(recording)

        val all = dao.getAll()
        assertEquals(1, all.size)
        val item = all[0]
        assertEquals("Song Title", item.title)
        assertEquals("Artist", item.artist)
        assertEquals("path/to/file.wav", item.filePath)
        assertEquals("path/to/thumbnail.jpg", item.thumbnailPath)

        val byId = dao.getById(item.id)
        assertEquals(item, byId)
    }
}
