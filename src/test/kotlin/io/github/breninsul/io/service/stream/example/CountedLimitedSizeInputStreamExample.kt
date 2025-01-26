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

import io.github.breninsul.io.service.stream.inputStream.CountedLimitedSizeInputStream
import org.junit.jupiter.api.Test

class CountedLimitedSizeInputStreamExample {
    @Test
    fun example() {
        val input = "Hello, World!".byteInputStream()
        //Try to limit
        val sizeLimit = 5
        val limitedStream = CountedLimitedSizeInputStream(input, sizeLimit)
        //Read byte
        limitedStream.read()
        //Mark at position 1
        limitedStream.mark(sizeLimit)
        //Read 3 bytes
        val readenBytes=limitedStream.readNBytes(3)
        //And return to 1st byte
        limitedStream.reset()
        //Read 4 bytes
        limitedStream.readNBytes(sizeLimit-1)
        println("Size limit $sizeLimit, readen bytes count:${limitedStream.bytesRead()}, expected $sizeLimit")
        //This access will throw exception because of limitation
        try {
            limitedStream.read()
        } catch (e:CountedLimitedSizeInputStream.SizeExceededException){
            println("Oh! We've reached limit ${e.message}")
        }
    }
}