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

import io.netty.buffer.Unpooled
import io.netty.buffer.ByteBuf
import java.io.*
import java.util.*

/**
 * A subclass of `InputStream` that provides a buffered reading mechanism
 * for an underlying input stream. This class keeps track of all bytes read
 * from the stream into an internal buffer, allowing for operations like
 * converting to a `PushbackInputStream` while retaining the already read
 * bytes.
 *
 * This is particularly useful in scenarios where data that has already
 * been read needs to be retained or processed further (e.g., for metadata
 * extraction or re-processing the same data).
 *
 * @param inputStream The underlying input stream to read data from.
 * @param closeStream Set to `true` if the underlying input stream should
 *    be closed when this stream is closed; otherwise, `false`.
 * @constructor Initializes the `BufferedReadInputStream` with the given
 *    input stream. Optionally closes the underlying input stream when
 *    `close()` is called.
 */
open class CacheReadenInputStream(
    inputStream: InputStream,
    protected open val closeStream: Boolean = false,
    protected open val bufferSize: Int = if (inputStream is ByteArrayInputStream) inputStream.available() else  UShort.MAX_VALUE.toInt()
) : InputStreamWrapper(inputStream) {
    protected open val buffer: ByteBuf = Unpooled.buffer(bufferSize)
    protected open var markPosition: Int = 0

    /**
     * Converts the current stream into a `PushbackInputStream` while
     * preserving any bytes that have already been read but are not yet
     * consumed. This method ensures that the previously read bytes can be
     * unread into the `PushbackInputStream` to support further processing.
     *
     * @return A `PushbackInputStream` instance wrapping the current stream,
     *    with already-read bytes pushed back into the stream.
     */
    open fun toUnreadPushbackInputStream(): PushbackInputStream {
        val alreadyRead = flushAndGetBufferBytes()
        val secondPartOfStreamReadStartsAt = alreadyRead.size
        val bufferedBytesCount = if (secondPartOfStreamReadStartsAt < 1) 1 else secondPartOfStreamReadStartsAt
        val pushbackInputStream = PushbackInputStream(delegate, bufferedBytesCount)
        pushbackInputStream.unread(alreadyRead)
        return pushbackInputStream
    }

    /**
     * Flushes the current internal buffer, retrieving all readable bytes, and
     * releases the buffer's resources.
     *
     * @return A byte array containing all the buffered data.
     */
    protected open fun flushAndGetBufferBytes(): ByteArray {
        try {
            val byteArray = ByteArray(buffer.readableBytes())
            buffer.readBytes(byteArray)
            return byteArray
        } finally {
            buffer.release()
        }
    }

    override fun internalReadAllBytes(): ByteArray {
        val bytes = delegate.readAllBytes()
        buffer.writeBytes(bytes)
        return bytes
    }

    override fun internalReadNBytes(len: Int): ByteArray {
        val bytes = delegate.readNBytes(len)
        buffer.writeBytes(bytes)
        return bytes
    }


    override fun internalRead(b: ByteArray): Int {
        val bytes = delegate.readNBytes(b.size)
        buffer.writeBytes(bytes)
        if (bytes.isEmpty()) {
            return -1
        }
        for (i in bytes.indices) {
            b[i] = bytes[i]
        }
        return bytes.size
    }

    override fun internalReadNBytes(b: ByteArray, off: Int, len: Int): Int {
        val bytes = delegate.readNBytes(len)
        buffer.writeBytes(bytes)
        if (bytes.isEmpty()) {
            return -1
        }
        for (i in bytes.indices) {
            b[off + i] = bytes[i]
        }
        return bytes.size
    }


    override fun internalSkipNBytes(n: Long) {
        val bytes = delegate.readNBytes(n.toInt())
        buffer.writeBytes(bytes)
        if (bytes.size != n.toInt()) {
            // skipped negative or too many bytes
            throw IOException("Unable to skip exactly")
        }
    }

    override fun internalSkip(n: Long): Long {
        val bytes = delegate.readNBytes(n.toInt())
        buffer.writeBytes(bytes)
        if (bytes.isEmpty()) {
            return 0
        }
        return bytes.size.toLong()
    }

    override fun internalRead(b: ByteArray, off: Int, len: Int): Int {
        val bytes = delegate.readNBytes(len)
        buffer.writeBytes(bytes)
        if (bytes.isEmpty()) {
            return -1
        }
        for (i in bytes.indices) {
            b[off + i] = bytes[i]
        }
        return bytes.size
    }

    override fun internalClose(): Unit {
        if (closeStream) {
            delegate.close()
        }
    }

    override fun internalRead(): Int {
        val nextByte = delegate.read()
        if (nextByte != -1) {
            buffer.writeByte(nextByte)
        }
        return nextByte
    }

    override fun internalMarkSupported(): Boolean {
        return delegate.markSupported()
    }

    override fun internalAvailable(): Int {
        return delegate.available()
    }

    override fun internalMark(readlimit: Int) {
        delegate.mark(readlimit)
        markPosition = buffer.writerIndex()
    }

    override fun internalReset() {
        delegate.reset()
        buffer.writerIndex(markPosition)
    }

    override fun internalTransferTo(out: OutputStream): Long {
        Objects.requireNonNull(out, "out")
        var transferred: Long = 0
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var read: Int
        while ((read(buffer, 0, DEFAULT_BUFFER_SIZE).also { read = it }) >= 0) {
            out.write(buffer, 0, read)
            if (transferred < Long.MAX_VALUE) {
                transferred = try {
                    Math.addExact(transferred, read.toLong())
                } catch (ignore: ArithmeticException) {
                    Long.MAX_VALUE
                }
            }
        }
        return transferred
    }

}
