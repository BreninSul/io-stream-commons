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

package io.github.breninsul.io.service.stream.outputStream

import java.io.OutputStream

abstract class OutputStreamWrapper(protected open val delegate: OutputStream) : OutputStream() {
    protected abstract fun internalWrite(b: Int)
    protected abstract fun internalWrite(b: ByteArray)
    protected abstract fun internalWrite(bb: ByteArray, off: Int, len: Int)
    protected abstract fun internalFlush()
    protected abstract fun internalClose()

    override fun write(b: Int) {
        internalWrite(b)
    }

    override fun write(b: ByteArray) {
        return internalWrite(b)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        return internalWrite(b, off, len)
    }

    override fun flush() {
        internalFlush()
    }

    override fun close() {
        internalClose()
    }

    override fun hashCode(): Int {
        return 31 + delegate.hashCode()
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

    override fun toString(): String {
        return "Wrapped $delegate"
    }
}