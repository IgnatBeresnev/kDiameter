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

package me.beresnev.kdiameter.util

import net.jcip.annotations.NotThreadSafe

/**
 * Allows a maximum of 32 bits to be stored
 */
@NotThreadSafe // subject to word tearing
class BitSet(
    private var bitHolder: Int = 0
) {

    fun asInt() = bitHolder

    fun assertInByteRange() {
        if (bitHolder !in 0..127) {
            throw ArrayIndexOutOfBoundsException("OutOfBounds for $bitHolder")
        }
    }

    /**
     * @param bitIndex from 0 to 31
     */
    fun set(bitIndex: Int, value: Boolean) {
        checkBounds(bitIndex)

        bitHolder = if (value) {
            bitHolder or (1 shl bitIndex)
        } else {
            bitHolder and (1 shl bitIndex).inv()
        }
    }

    /**
     * @param bitIndex from 0 to 31
     */
    fun get(bitIndex: Int): Boolean {
        checkBounds(bitIndex)
        return bitHolder and (1 shl bitIndex) != 0
    }

    private fun checkBounds(bitIndex: Int) {
        if (bitIndex < 0 || bitIndex >= 32) {
            throw IndexOutOfBoundsException("$bitIndex")
        }
    }
}