package com.example.coin_karaoke_voice_diary.data

class RecordingRepository(private val dao: RecordingDao) {
    suspend fun addRecording(recording: Recording): Long = dao.insert(recording)

    suspend fun getRecordings(): List<Recording> = dao.getAll()

    suspend fun getRecording(id: Long): Recording? = dao.getById(id)
}
