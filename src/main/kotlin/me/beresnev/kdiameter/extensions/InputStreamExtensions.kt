package me.beresnev.kdiameter.extensions

import java.io.InputStream

/**
 * "valid" in this sense means that it will never return -1
 *
 * @throws IllegalStateException if end of stream reached (read value is -1)
 */
fun InputStream.readByte(): Int {
    val readValue = this.read()
    if (readValue == -1)
        throw IllegalStateException("EOF")
    return readValue
}

// unsigned value is guaranteed to be within signed int bounds
fun InputStream.readThreeBytes(): Int {
    val b1 = this.read()
    val b2 = this.read()
    val b3 = this.read()
    if (b1 or b2 or b3 < 0)
        throw IllegalStateException("EOF")
    return (b1 shl 16) + (b2 shl 8) + (b3 shl 0)
}

// might overflow
fun InputStream.readFourBytes(): Int {
    val ch1 = this.read()
    val ch2 = this.read()
    val ch3 = this.read()
    val ch4 = this.read()
    if (ch1 or ch2 or ch3 or ch4 < 0)
        throw IllegalStateException("EOF")
    return (ch1 shl 24) + (ch2 shl 16) + (ch3 shl 8) + (ch4 shl 0)
}