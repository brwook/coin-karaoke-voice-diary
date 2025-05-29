package com.example.coin_karaoke_voice_diary.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecordingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recording: Recording): Long

    @Query(
        "SELECT id, filePath, thumbnail_path, title, artist, recordedAt FROM recordings ORDER BY recordedAt DESC"
    )
    suspend fun getAll(): List<Recording>

    @Query(
        "SELECT id, filePath, thumbnail_path, title, artist, recordedAt FROM recordings WHERE id = :id"
    )
    suspend fun getById(id: Long): Recording?
}
