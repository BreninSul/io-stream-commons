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
import java.io.IOException

class CountedLimitedSizeInputStreamTest {

    @Test
    fun `test read single byte within limit`() {
        val input = "Hello, World!".byteInputStream()
        val limitedStream = CountedLimitedSizeInputStream(input, 20)

        val byte = limitedStream.read()
        assertEquals('H'.code, byte)
    }

    @Test
    fun `test read single byte exceeding limit`() {
        val input = "Hello, World!".byteInputStream()
        val limitedStream = CountedLimitedSizeInputStream(input.buffered(1), 5)
        repeat(5) { limitedStream.read() }
        assertThrows(CountedLimitedSizeInputStream.SizeExceededException::class.java) {
            limitedStream.read()
        }
    }
    @Test
    fun `test read single byte exceeding limit2`() {
        val input = "Hello, World!".byteInputStream()
        val limitedStream = CountedLimitedSizeInputStream(input.buffered(5), 5)
        limitedStream.read()
        limitedStream.mark(5)
        val readenBytes=limitedStream.readNBytes(3)
        limitedStream.reset()
        repeat(4) { limitedStream.read() }
        assertThrows(CountedLimitedSizeInputStream.SizeExceededException::class.java) {
            limitedStream.read()
        }
    }
    @Test
    fun `test read byte array within limit`() {
        val input = "Hello, World!".byteInputStream()
        val limitedStream = CountedLimitedSizeInputStream(input, 20)

        val buffer = ByteArray(5)
        val bytesRead = limitedStream.read(buffer, 0, 5)

        assertEquals(5, bytesRead)
        assertEquals("Hello", String(buffer))
    }

    @Test
    fun `test read byte array exceeding limit`() {
        val input = "Hello, World!".byteInputStream().buffered(5)
        val limitedStream = CountedLimitedSizeInputStream(input, 5)

        val buffer = ByteArray(10)
        assertThrows(CountedLimitedSizeInputStream.SizeExceededException::class.java) {
            limitedStream.read(buffer, 0, 10)
        }
    }

    @Test
    fun `test readAllBytes within limit`() {
        val input = "Hello, World!".byteInputStream()
        val limitedStream = CountedLimitedSizeInputStream(input, 20)

        val result = limitedStream.readAllBytes()
        assertEquals("Hello, World!", String(result))
    }

    @Test
    fun `test readAllBytes exceeding limit`() {
        val input = "Hello, World!".byteInputStream().buffered(5)
        val limitedStream = CountedLimitedSizeInputStream(input, 5)

        assertThrows(CountedLimitedSizeInputStream.SizeExceededException::class.java) {
            limitedStream.readAllBytes()
        }
    }

    @Test
    fun `test readNBytes within limit`() {
        val input = "Hello, World!".byteInputStream()
        val limitedStream = CountedLimitedSizeInputStream(input, 20)

        val result = limitedStream.readNBytes(5)
        assertEquals("Hello", String(result))
    }

    @Test
    fun `test readNBytes exceeding limit`() {
        val input = "Hello, World!".byteInputStream().buffered()
        val limitedStream = CountedLimitedSizeInputStream(input, 5)

        assertThrows(CountedLimitedSizeInputStream.SizeExceededException::class.java) {
            limitedStream.readNBytes(10)
        }
    }

    @Test
    fun `test skip bytes within limit`() {
        val input = "Hello, World!".byteInputStream().buffered(5)
        val limitedStream = CountedLimitedSizeInputStream(input, 10)

        val skipped = limitedStream.skip(5)
        assertEquals(5, skipped)
    }

    @Test
    fun `test skip bytes exceeding limit`() {
        val input = "Hello, World!".byteInputStream().buffered(5)
        val limitedStream = CountedLimitedSizeInputStream(input, 5)

        assertThrows(CountedLimitedSizeInputStream.SizeExceededException::class.java) {
            limitedStream.skip(10)
        }
    }

    @Test
    fun `test available bytes`() {
        val input = "Hello, World!".byteInputStream().buffered(5)
        val limitedStream = CountedLimitedSizeInputStream(input, 20)

        val available = limitedStream.available()
        assertEquals(input.available(), available)
    }

    @Test
    fun `test mark and reset`() {
        val input = "Hello, World!".byteInputStream().buffered(5)
        val limitedStream = CountedLimitedSizeInputStream(input, 20)

        assertTrue(limitedStream.markSupported())

        limitedStream.mark(5)
        repeat(5) { limitedStream.read() }
        limitedStream.reset()

        val byte = limitedStream.read()
        assertEquals('H'.code, byte)
    }

    @Test
    fun `test close`() {
        val input = "Hello, World!".byteInputStream().buffered(5)
        val limitedStream = CountedLimitedSizeInputStream(input, 20)

        limitedStream.close()
        assertThrows(IOException::class.java) {
            limitedStream.read()
        }
    }

    @Test
    fun `test getBytesRead after single byte read`() {
        val input = "Hello, World!".byteInputStream()
        val limitedStream = CountedLimitedSizeInputStream(input, 20)

        limitedStream.read()
        assertEquals(1, limitedStream.bytesRead())
    }

    @Test
    fun `test getBytesRead after multiple bytes read`() {
        val input = "Hello, World!".byteInputStream()
        val limitedStream = CountedLimitedSizeInputStream(input, 20)

        val buffer = ByteArray(5)
        limitedStream.read(buffer)
        assertEquals(5, limitedStream.bytesRead())
    }

    @Test
    fun `test getBytesRead after skipping bytes`() {
        val input = "Hello, World!".byteInputStream()
        val limitedStream = CountedLimitedSizeInputStream(input, 20)

        limitedStream.skip(5)
        assertEquals(5, limitedStream.bytesRead())
    }

    @Test
    fun `test getBytesRead after mark and reset`() {
        val input = "Hello, World!".byteInputStream()
        val limitedStream = CountedLimitedSizeInputStream(input, 20)

        limitedStream.mark(5)
        repeat(5) { limitedStream.read() }
        assertEquals(5, limitedStream.bytesRead())

        limitedStream.reset()
        assertEquals(0, limitedStream.bytesRead())
    }
}