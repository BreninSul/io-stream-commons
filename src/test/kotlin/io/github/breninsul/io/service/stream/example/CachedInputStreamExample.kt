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

package io.github.breninsul.io.service.stream.example

import io.github.breninsul.io.service.stream.inputStream.CacheReadenInputStream
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

class CachedInputStreamExample {
    @Test
    fun example() {
        val originalData = "test".toByteArray()
        val originalInputStream = ByteArrayInputStream("test".toByteArray())
        //create cached stream
        val cacheStream = CacheReadenInputStream(originalInputStream)
        //Read two bytes
        val readen2Bytes = cacheStream.readNBytes(2)
        //Get new InputStream in "original" condition, as 2 bytes haven't been readen
        val pushbackInputStream = cacheStream.toUnreadPushbackInputStream()
        val result = pushbackInputStream.readAllBytes()
        println("Original data: ${String(originalData)},Pushback data: ${String(result)},Readen 2 bytes: ${String(readen2Bytes)}")

    }
}