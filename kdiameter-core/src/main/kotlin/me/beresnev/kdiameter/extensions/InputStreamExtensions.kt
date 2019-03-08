/*
 * Copyright (C) 2019 Ignat Beresnev and individual contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.beresnev.kdiameter.extensions

import java.io.InputStream

/**
 * @throws IllegalStateException if end of stream reached (read value is -1)
 */
fun InputStream.readByte(): Int {
    val readValue = this.read()
    if (readValue == -1) {
        throw IllegalStateException("EOF")
    }
    return readValue
}

/**
 * @throws IllegalStateException if end of stream reached (read value is -1)
 */
fun InputStream.readThreeBytes(): Int {
    val b1 = this.read()
    val b2 = this.read()
    val b3 = this.read()
    if (b1 or b2 or b3 < 0) {
        throw IllegalStateException("EOF")
    }
    return (b1 shl 16) + (b2 shl 8) + (b3 shl 0)
}

/**
 * @throws IllegalStateException if end of stream reached (read value is -1)
 */
fun InputStream.readFourBytes(): Int {
    val b1 = this.read()
    val b2 = this.read()
    val b3 = this.read()
    val b4 = this.read()
    if (b1 or b2 or b3 or b4 < 0) {
        throw IllegalStateException("EOF")
    }
    return (b1 shl 24) + (b2 shl 16) + (b3 shl 8) + (b4 shl 0)
}