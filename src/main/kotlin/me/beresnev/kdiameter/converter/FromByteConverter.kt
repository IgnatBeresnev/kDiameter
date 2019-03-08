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

package me.beresnev.kdiameter.converter

object FromByteConverter {
    /**
     * Unsigned value is guaranteed to be within signed int bounds
     */
    fun toInt(b1: Int, b2: Int, b3: Int) = (b1 shl 16) + (b2 shl 8) + (b3 shl 0)

    /**
     * Might overflow
     */
    fun toInt(b1: Int, b2: Int, b3: Int, b4: Int) = (b1 shl 24) + (b2 shl 16) + (b3 shl 8) + (b4 shl 0)

    fun toInt(data: ByteArray): Int {
        if (data.size != 4) {
            throw IllegalArgumentException("Expected 4 bytes, got ${data.size}")
        }
        return (data[0].toInt() shl 24) +
                (data[1].toInt() shl 16) +
                (data[2].toInt() shl 8) +
                (data[3].toInt() shl 0)
    }
}