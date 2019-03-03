package me.beresnev.kdiameter.util

import net.jcip.annotations.NotThreadSafe

/**
 * Allows a maximum of 32 bits to be stored
 */
@NotThreadSafe // as well as subject to word tearing
data class BitSet(
    var bitHolder: Int = 0
) {

    fun getAsByte(): Byte {
        return bitHolder.toByte()
    }

    fun getAsInt(): Int {
        return bitHolder
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