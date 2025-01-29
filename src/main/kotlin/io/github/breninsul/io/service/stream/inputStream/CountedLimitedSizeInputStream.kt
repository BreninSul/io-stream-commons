package io.github.breninsul.io.service.stream.inputStream

import java.io.InputStream
import java.io.OutputStream
import java.util.*

open class CountedLimitedSizeInputStream(
    inputStream: InputStream,
    protected open val sizeLimit: Int?,
    protected open var bytesRead: Long = 0L
) : InputStreamWrapper(inputStream) {
    protected open var markPosition: Int = 0
    protected open val haveToLimit = !(sizeLimit == null || sizeLimit!! < 0 || sizeLimit == Int.MAX_VALUE)

    init {
        throwIfLimitExceeded()
    }

    open class SizeExceededException(message: String?, val limit: Int, val bytesRead: Long) : RuntimeException(message)
    open fun bytesRead(): Long {return bytesRead}
    override fun internalReadAllBytes(): ByteArray {
        //If don't have to limit just remember bytes that have readen
        if (!haveToLimit) {
            val readBefore = bytesRead
            val bytes = delegate.readAllBytes()
            val uncountedBytesRead = bytes.size - (bytesRead - readBefore)
            bytesRead += uncountedBytesRead
            return bytes
        }
        //Or read all bytes left and try to reed one more
        val bytesLeft = (sizeLimit!! - bytesRead)
        val tillLimit = internalReadNBytes(bytesLeft.toInt())
        if (tillLimit.size < bytesLeft.toInt()) {
            return tillLimit
        }
        //Check if there is some more bytes
        val additionalByte = internalRead()
        return tillLimit
    }

    override fun internalReadNBytes(b: ByteArray, off: Int, len: Int): Int {
        val readBefore = bytesRead
        val read = delegate.readNBytes(b, off, len)
        val uncountedBytesRead = read - (bytesRead - readBefore)
        bytesRead += uncountedBytesRead
        throwIfLimitExceeded()
        return read
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


    override fun internalSkipNBytes(n: Long) {
        val readBefore = bytesRead
        delegate.skipNBytes(n)
        val uncountedBytesRead = n - (bytesRead - readBefore)
        bytesRead += uncountedBytesRead
        throwIfLimitExceeded()
    }

    override fun internalSkip(n: Long): Long {
        val readBefore = bytesRead
        val skipped = delegate.skip(n)
        val uncountedBytesRead = skipped - (bytesRead - readBefore)
        bytesRead += uncountedBytesRead
        throwIfLimitExceeded()
        return skipped
    }

    override fun internalRead(b: ByteArray, off: Int, len: Int): Int {
        val readBefore = bytesRead
        val read = delegate.read(b, off, len)
        val uncountedBytesRead = read - (bytesRead - readBefore)
        bytesRead += uncountedBytesRead
        throwIfLimitExceeded()
        return read
    }

    override fun internalRead(b: ByteArray): Int {
        val readBefore = bytesRead
        val read = delegate.read(b)
        val uncountedBytesRead = read - (bytesRead - readBefore)
        bytesRead += uncountedBytesRead
        throwIfLimitExceeded()
        return read
    }

    override fun internalReadNBytes(count: Int): ByteArray {
        val readBefore = bytesRead
        val bytes = delegate.readNBytes(count)
        val uncountedBytesRead = bytes.size - (bytesRead - readBefore)
        bytesRead += uncountedBytesRead
        throwIfLimitExceeded()
        return bytes
    }

    override fun internalClose() {
        delegate.close()
    }

    override fun internalRead(): Int {
        val nextByte = delegate.read()
        if (nextByte != -1) {
            bytesRead += 1
            throwIfLimitExceeded()
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
        markPosition = bytesRead.toInt()
    }

    override fun internalReset() {
        delegate.reset()
        bytesRead = markPosition.toLong()
    }

    protected open fun throwIfLimitExceeded() {
        if (hasReachedSizeLimit()) {
            throwException()
        }
    }

    protected open fun throwException() {
        throw SizeExceededException(
            "File size limit $sizeLimit exceeded ($bytesRead bytes)",
            sizeLimit!!, bytesRead
        )
    }

    private fun hasReachedSizeLimit() = haveToLimit && this.bytesRead > this.sizeLimit!!

}