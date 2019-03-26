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

package me.beresnev.kdiameter.extensions.stream

import java.io.OutputStream

// just a more clear name as to what we're writing,
// and a range check, so as not to associate int with 4 bytes
fun OutputStream.writeByte(byte: Int) {
    if (byte !in 0..127) throw IllegalArgumentException("OutOfBounds for $byte")
    this.write(byte)
}

fun OutputStream.writeFourBytes(value: Long) {
    this.write((value.ushr(24) and 0xFF).toInt())
    this.write((value.ushr(16) and 0xFF).toInt())
    this.write((value.ushr(8) and 0xFF).toInt())
    this.write((value.ushr(0) and 0xFF).toInt())
}

fun OutputStream.writeThreeBytes(value: Int) {
    this.write(value.ushr(16) and 0xFF)
    this.write(value.ushr(8) and 0xFF)
    this.write(value.ushr(0) and 0xFF)
}