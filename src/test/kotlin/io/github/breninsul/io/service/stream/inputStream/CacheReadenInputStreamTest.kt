/*
 * MIT License
 * Copyright (c) 2025 BreninSul
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.breninsul.io.service.stream.inputStream

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.PushbackInputStream

class CacheReadenInputStreamTest {

    @Test
    fun `test toUnreadPushbackInputStream`() {
        val data = "test".toByteArray()
        val inputStream = ByteArrayInputStream(data)
        val cacheStream = CacheReadenInputStream(inputStream, closeStream = false)

        val pushbackInputStream = cacheStream.toUnreadPushbackInputStream()
        val result = pushbackInputStream.readAllBytes()

        assertArrayEquals(data, result)
    }

    @Test
    fun `test toUnreadPushbackInputStream2`() {
        val data = "Hello World!".toByteArray()
        val inputStream = ByteArrayInputStream(data)
        val cacheStream = CacheReadenInputStream(inputStream, closeStream = false, bufferSize = 2)
        repeat(5){cacheStream.read()}
        val pushbackInputStream = cacheStream.toUnreadPushbackInputStream()
        val result = pushbackInputStream.readAllBytes()
        assertArrayEquals(data, result)
    }
    @Test
    fun `test toUnreadPushbackInputStreamReset`() {
        val data = "Hello World!".toByteArray()
        val inputStream = ByteArrayInputStream(data)
        val cacheStream = CacheReadenInputStream(inputStream, closeStream = false, bufferSize = 2)
        cacheStream.mark(5)
        repeat(5){cacheStream.read()}
        cacheStream.reset()
        val pushbackInputStream = cacheStream.toUnreadPushbackInputStream()
        val result = pushbackInputStream.readAllBytes()
        assertArrayEquals(data, result)
    }

    @Test
    fun `test read all bytes`() {
        val data = "test".toByteArray()
        val inputStream = ByteArrayInputStream(data)
        val cacheStream = CacheReadenInputStream(inputStream, closeStream = false)

        val result = cacheStream.readAllBytes()
        assertArrayEquals(data, result)
        val pushbackInputStream = cacheStream.toUnreadPushbackInputStream()
        val pushbackResult = pushbackInputStream.readAllBytes()
        assertArrayEquals(data, pushbackResult)
    }

    @Test
    fun `test read n bytes`() {
        val data = "test".toByteArray()
        val inputStream = ByteArrayInputStream(data)
        val cacheStream = CacheReadenInputStream(inputStream, closeStream = false)

        val result = cacheStream.readNBytes(2)
        assertArrayEquals("te".toByteArray(), result)
        val pushbackInputStream = cacheStream.toUnreadPushbackInputStream()
        val pushbackResult = pushbackInputStream.readNBytes(2)
        assertArrayEquals("te".toByteArray(), pushbackResult)
    }

    @Test
    fun `test read into byte array`() {
        val data = "test".toByteArray()
        val inputStream = ByteArrayInputStream(data)
        val cacheStream = CacheReadenInputStream(inputStream, closeStream = false)

        val buffer = ByteArray(4)
        val bytesRead = cacheStream.read(buffer)

        assertEquals(4, bytesRead)
        assertArrayEquals(data, buffer)
        val pushbackInputStream = cacheStream.toUnreadPushbackInputStream()
        val pushbackBuffer = ByteArray(4)
        val pushbackBytesRead = pushbackInputStream.read(pushbackBuffer)
        assertEquals(4, pushbackBytesRead)
        assertArrayEquals(data, pushbackBuffer)
    }

    @Test
    fun `test skip n bytes`() {
        val data = "test".toByteArray()
        val inputStream = ByteArrayInputStream(data)
        val cacheStream = CacheReadenInputStream(inputStream, closeStream = false)

        val skipped = cacheStream.skip(2)
        val result = cacheStream.readAllBytes()

        assertEquals(2, skipped)
        assertArrayEquals("st".toByteArray(), result)
        val pushbackInputStream = cacheStream.toUnreadPushbackInputStream()
        val pushbackSkipped = pushbackInputStream.skip(2)
        val pushbackResult = pushbackInputStream.readAllBytes()
        assertArrayEquals("st".toByteArray(), pushbackResult)
    }

    @Test
    fun `test skip n bytes2`() {
        val data = "test".toByteArray()
        val inputStream = ByteArrayInputStream(data)
        val cacheStream = CacheReadenInputStream(inputStream, closeStream = false)

        val skipped = cacheStream.skip(2)
        val result = cacheStream.readAllBytes()

        assertEquals(2, skipped)
        assertArrayEquals("st".toByteArray(), result)
        val pushbackInputStream = cacheStream.toUnreadPushbackInputStream()
        val pushbackResult = pushbackInputStream.readAllBytes()
        assertArrayEquals("test".toByteArray(), pushbackResult)
    }

    @Test
    fun `test mark and reset`() {
        val data = "test".toByteArray()
        val inputStream = ByteArrayInputStream(data)
        val cacheStream = CacheReadenInputStream(inputStream, closeStream = false)

        cacheStream.mark(0)
        val buffer1 = ByteArray(2)
        cacheStream.read(buffer1)
        cacheStream.reset()

        val buffer2 = ByteArray(3)
        cacheStream.read(buffer2)

        assertArrayEquals("te".toByteArray(), buffer1)
        assertArrayEquals("tes".toByteArray(), buffer2)
        val pushbackInputStream = cacheStream.toUnreadPushbackInputStream()
        val pushbackResult = pushbackInputStream.readAllBytes()
        assertArrayEquals("test".toByteArray(), pushbackResult)
    }



    @Test
    fun `test internalTransferTo`() {
        val data = "test".toByteArray()
        val inputStream = ByteArrayInputStream(data)
        val cacheStream = CacheReadenInputStream(inputStream, closeStream = false)

        val outputStream = ByteArrayOutputStream()
        val transferred = cacheStream.transferTo(outputStream)

        assertEquals(data.size.toLong(), transferred)
        assertArrayEquals(data, outputStream.toByteArray())
    }
}