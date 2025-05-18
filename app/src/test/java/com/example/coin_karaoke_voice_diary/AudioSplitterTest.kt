package com.example.coin_karaoke_voice_diary

import com.example.coin_karaoke_voice_diary.audiosplit.AudioSplitter
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AudioSplitterTest {

    private fun generateWav(samples: ShortArray, sampleRate: Int = 8000): ByteArray {
        val headerSize = 44
        val bytes = ByteArray(headerSize + samples.size * 2)
        // RIFF header
        bytes[0] = 'R'.code.toByte(); bytes[1] = 'I'.code.toByte()
        bytes[2] = 'F'.code.toByte(); bytes[3] = 'F'.code.toByte()
        val dataSize = samples.size * 2
        ByteBuffer.wrap(bytes, 4, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(36 + dataSize)
        bytes[8] = 'W'.code.toByte(); bytes[9] = 'A'.code.toByte(); bytes[10] = 'V'.code.toByte(); bytes[11] = 'E'.code.toByte()
        // fmt chunk
        bytes[12] = 'f'.code.toByte(); bytes[13] = 'm'.code.toByte(); bytes[14] = 't'.code.toByte(); bytes[15] = ' '.code.toByte()
        ByteBuffer.wrap(bytes, 16, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(16)
        ByteBuffer.wrap(bytes, 20, 2).order(ByteOrder.LITTLE_ENDIAN).putShort(1) // PCM
        ByteBuffer.wrap(bytes, 22, 2).order(ByteOrder.LITTLE_ENDIAN).putShort(1) // mono
        ByteBuffer.wrap(bytes, 24, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(sampleRate)
        ByteBuffer.wrap(bytes, 28, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(sampleRate * 2)
        ByteBuffer.wrap(bytes, 32, 2).order(ByteOrder.LITTLE_ENDIAN).putShort(2)
        ByteBuffer.wrap(bytes, 34, 2).order(ByteOrder.LITTLE_ENDIAN).putShort(16)
        // data chunk
        bytes[36] = 'd'.code.toByte(); bytes[37] = 'a'.code.toByte(); bytes[38] = 't'.code.toByte(); bytes[39] = 'a'.code.toByte()
        ByteBuffer.wrap(bytes, 40, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(dataSize)
        var offset = headerSize
        samples.forEach {
            ByteBuffer.wrap(bytes, offset, 2).order(ByteOrder.LITTLE_ENDIAN).putShort(it)
            offset += 2
        }
        return bytes
    }

    @Test
    fun `splitBySilence returns segments`() = runBlocking {
        val tone = ShortArray(4000) { 10000 }
        val silence = ShortArray(2000) { 0 }
        val samples = tone + silence + tone
        val wav = generateWav(samples)

        val parts = AudioSplitter.splitBySilence(wav, threshold = 500, minSilenceMs = 200)
        assertEquals(2, parts.size)
        val expectedBytesPerPart = tone.size * 2
        assertEquals(expectedBytesPerPart, parts[0].size)
        assertEquals(expectedBytesPerPart, parts[1].size)
    }
}
