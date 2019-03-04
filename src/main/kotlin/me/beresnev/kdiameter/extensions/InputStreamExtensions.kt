package me.beresnev.kdiameter.extensions

import me.beresnev.kdiameter.converter.FromByteConverter
import java.io.InputStream

/**
 * @throws IllegalStateException if end of stream reached (read value is -1)
 */
fun InputStream.readByte(): Int {
    val readValue = this.read()
    if (readValue == -1)
        throw IllegalStateException("EOF")
    return readValue
}

/**
 * @throws IllegalStateException if end of stream reached (read value is -1)
 */
fun InputStream.readThreeBytes(): Int {
    val b1 = this.read()
    val b2 = this.read()
    val b3 = this.read()
    if (b1 or b2 or b3 < 0)
        throw IllegalStateException("EOF")
    return FromByteConverter.toInt(b1, b2, b3)
}

/**
 * @throws IllegalStateException if end of stream reached (read value is -1)
 */
fun InputStream.readFourBytes(): Int {
    val b1 = this.read()
    val b2 = this.read()
    val b3 = this.read()
    val b4 = this.read()
    if (b1 or b2 or b3 or b4 < 0)
        throw IllegalStateException("EOF")
    return FromByteConverter.toInt(b1, b2, b3, b4)
}