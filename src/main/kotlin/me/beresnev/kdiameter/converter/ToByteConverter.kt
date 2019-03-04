package me.beresnev.kdiameter.converter

object ToByteConverter {
    fun toByteArray(value: Int): ByteArray {
        return byteArrayOf(
            value.ushr(24).toByte(),
            value.ushr(16).toByte(),
            value.ushr(8).toByte(),
            value.toByte()
        )
    }
}