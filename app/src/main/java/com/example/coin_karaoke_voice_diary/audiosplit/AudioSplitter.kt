package com.example.coin_karaoke_voice_diary.audiosplit

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs

/**
 * Utility for splitting a PCM WAV byte array by detecting silent sections.
 */
object AudioSplitter {
    /**
     * Split the given WAV PCM byte array by silence and return each segment as a [ByteArray].
     *
     * @param wavBytes WAV file bytes in 16‑bit PCM format.
     * @param threshold Amplitude considered silent. Defaults to 2000.
     * @param minSilenceMs Minimum duration of consecutive silence that triggers a split.
     */
    suspend fun splitBySilence(
        wavBytes: ByteArray,
        threshold: Int = 2000,
        minSilenceMs: Int = 300
    ): List<ByteArray> = withContext(Dispatchers.Default) {
        if (wavBytes.size < 44) return@withContext emptyList()

        // Parse sample rate and bits per sample from WAV header.
        val sampleRate = ByteBuffer.wrap(wavBytes, 24, 4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .int
        val bitsPerSample = ByteBuffer.wrap(wavBytes, 34, 2)
            .order(ByteOrder.LITTLE_ENDIAN)
            .short.toInt()
        val bytesPerSample = bitsPerSample / 8
        val dataOffset = 44 // assume standard header
        val audioLen = wavBytes.size - dataOffset
        val minSilenceSamples = (sampleRate * minSilenceMs) / 1000

        var i = 0
        var segmentStart = 0
        var silenceCount = 0
        val segments = mutableListOf<ByteArray>()

        fun readSample(offset: Int): Int = when (bitsPerSample) {
            16 -> ByteBuffer.wrap(wavBytes, dataOffset + offset, 2)
                .order(ByteOrder.LITTLE_ENDIAN)
                .short.toInt()
            else -> wavBytes[dataOffset + offset].toInt()
        }

        while (i < audioLen) {
            val sample = abs(readSample(i))
            if (sample <= threshold) {
                silenceCount++
                if (silenceCount >= minSilenceSamples) {
                    val cutEnd = i - (silenceCount * bytesPerSample) + bytesPerSample
                    if (segmentStart < cutEnd) {
                        segments.add(
                            wavBytes.copyOfRange(dataOffset + segmentStart, dataOffset + cutEnd)
                        )
                    }
                    // skip the silence block
                    i += bytesPerSample
                    while (i < audioLen && abs(readSample(i)) <= threshold) {
                        i += bytesPerSample
                    }
                    segmentStart = i
                    silenceCount = 0
                    continue
                }
            } else {
                silenceCount = 0
            }
            i += bytesPerSample
        }
        if (segmentStart < audioLen) {
            segments.add(wavBytes.copyOfRange(dataOffset + segmentStart, wavBytes.size))
        }
        return@withContext segments
    }
}
