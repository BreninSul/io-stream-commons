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

import java.io.InputStream
import java.io.OutputStream

abstract class InputStreamWrapper(protected open val delegate: InputStream) : InputStream() {
    protected abstract fun internalTransferTo(out: OutputStream): Long
    protected abstract fun internalSkipNBytes(n: Long)
    protected abstract fun internalReadNBytes(len: Int): ByteArray;
    protected abstract fun internalReadAllBytes(): ByteArray
    protected abstract fun internalReadNBytes(b: ByteArray, off: Int, len: Int): Int
    protected abstract fun internalRead(): Int
    protected abstract fun internalRead(b: ByteArray, off: Int, len: Int): Int
    protected abstract fun internalRead(b: ByteArray): Int
    protected abstract fun internalSkip(n: Long): Long
    protected abstract fun internalAvailable(): Int
    protected abstract fun internalClose()
    protected abstract fun internalMarkSupported(): Boolean
    protected abstract fun internalMark(readlimit: Int)
    protected abstract fun internalReset()
    override fun read(): Int {
        return internalRead()
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return internalRead(b, off, len)
    }

    override fun skip(n: Long): Long {
        return internalSkip(n)
    }

    override fun available(): Int {
        return internalAvailable()
    }

    override fun close() {
        internalClose()
    }

    override fun read(b: ByteArray): Int {
        return internalRead(b)
    }

    override fun markSupported(): Boolean {
        return internalMarkSupported()
    }

    override fun mark(readlimit: Int) {
        return internalMark(readlimit)
    }

    override fun reset() {
        return internalReset()
    }

    override fun readNBytes(b: ByteArray, off: Int, len: Int): Int {
        return internalReadNBytes(b, off, len)
    }

    override fun readAllBytes(): ByteArray {
        return internalReadAllBytes()
    }

    override fun readNBytes(len: Int): ByteArray {
        return internalReadNBytes(len)
    }

    override fun skipNBytes(n: Long) {
        return internalSkipNBytes(n)
    }

    override fun transferTo(out: OutputStream): Long {
        return internalTransferTo(out)
    }

    override fun toString(): String {
        return "Wrapped $delegate"
    }

    override fun hashCode(): Int {
        return 32 + delegate.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        return if (other.javaClass == this.javaClass) {
            delegate == other
        } else {
            false
        }
    }

}