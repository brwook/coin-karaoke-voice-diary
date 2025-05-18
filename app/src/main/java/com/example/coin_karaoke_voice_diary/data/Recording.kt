package com.example.coin_karaoke_voice_diary.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordings")
data class Recording(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filePath: String,
    /** Local path to a saved thumbnail image for offline use. */
    val thumbnailPath: String? = null,
    val title: String,
    val artist: String,
    val recordedAt: Long = System.currentTimeMillis()
)
