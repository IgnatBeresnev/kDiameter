package me.beresnev.kdiameter.converter

import java.net.InetAddress

object FromByteConverter {
    // unsigned value is guaranteed to be within signed int bounds
    fun toInt(b1: Int, b2: Int, b3: Int): Int {
        return (b1 shl 16) + (b2 shl 8) + (b3 shl 0)
    }

    // might overflow
    fun toInt(b1: Int, b2: Int, b3: Int, b4: Int): Int {
        return (b1 shl 24) + (b2 shl 16) + (b3 shl 8) + (b4 shl 0)
    }

    fun toInt(data: ByteArray): Int {
        if (data.size != 4) {
            throw IllegalArgumentException("Expected 4 bytes, got ${data.size}")
        }
        return (data[0].toInt() shl 24) +
                (data[1].toInt() shl 16) +
                (data[2].toInt() shl 8) +
                (data[3].toInt() shl 0)
    }

//    fun toInetAddress(rawData: ByteArray): InetAddress {
//        try {
//            // The IPAddress format is derived from the OctetString AVP Base
//            // Format. It represents 32 bit (IPv4) [17] or 128 bit (IPv6) [16]
//            // address, most significant octet first. The format of the
//            // address (IPv4 or IPv6) is determined by the length. If the
//            // attribute value is an IPv4 address, the AVP Length field MUST
//            // be 12 (16 if 'V' bit is enabled), otherwise the AVP Length
//            // field MUST be set to 24 (28 if the 'V' bit is enabled) for IPv6
//            // addresses.
//            val address = Arrays.copyOfRange(rawData, 2, rawData.size)
//            return InetAddress.getByAddress(address)
//        } catch (e: Exception) {
//            throw IllegalStateException(e)
//        }
//    }

    fun toInetAddress(rawData: ByteArray): InetAddress {
        val inetAddress: InetAddress
        try {
            val isIPv6 = rawData[1].toInt() != 1
            val address = ByteArray(if (isIPv6) 16 else 4)
            System.arraycopy(rawData, 2, address, 0, address.size)
            inetAddress = if (isIPv6) InetAddress.getByAddress(address) else InetAddress.getByAddress(address)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        return inetAddress
    }
}