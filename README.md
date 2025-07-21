This is lib provides common Input/Output stream implementations for JVM


### To use lib just add dependency

````kotlin
dependencies {
//Other dependencies
    implementation("io.github.breninsul:io-stream-commons:1.0.4")
//Other dependencies
}
````

### Provided implementations:
- `io.github.breninsul.io.service.stream.inputStream.CacheReadenInputStream`
- `io.github.breninsul.io.service.stream.inputStream.CountedLimitedSizeInputStream`
  and abstract implementations to help not to forget override ALL stream public methods:
- `io.github.breninsul.io.service.stream.inputStream.InputStreamWrapper`
- `io.github.breninsul.io.service.stream.outputStream.OutputStreamWrapper`

### CacheReadenInputStream usage example:
````kotlin
package io.github.breninsul.io.service.stream.example

import io.github.breninsul.io.service.stream.inputStream.CacheReadenInputStream
import java.io.ByteArrayInputStream

class CachedInputStreamExample {
    
    fun example() {
        val originalData = "test".toByteArray()
        val originalInputStream = ByteArrayInputStream("test".toByteArray())
        //create cached stream
        val cacheStream = CacheReadenInputStream(originalInputStream)
        //Or use extension
        val cacheStreamSecond = originalInputStream.toCacheReadenInputStream()

      //Read two bytes
        val readen2Bytes = cacheStream.readNBytes(2)
        //Get new InputStream in "original" condition, as 2 bytes haven't been readen
        val pushbackInputStream = cacheStream.toUnreadPushbackInputStream()
        val result = pushbackInputStream.readAllBytes()
        println("Original data: ${String(originalData)},Pushback data: ${String(result)},Readen 2 bytes: ${String(readen2Bytes)}")

    }
}
````

### CountedLimitedSizeInputStream usage example:
````kotlin
package io.github.breninsul.io.service.stream.example

import io.github.breninsul.io.service.stream.inputStream.CountedLimitedSizeInputStream

class CountedLimitedSizeInputStreamExample {
  
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
````