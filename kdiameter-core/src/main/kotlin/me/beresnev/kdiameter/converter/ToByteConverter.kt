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

import me.beresnev.kdiameter.constants.AddressFamily
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress

object ToByteConverter {
    fun fromInt(value: Int): ByteArray {
        return byteArrayOf(
            value.ushr(24).toByte(),
            value.ushr(16).toByte(),
            value.ushr(8).toByte(),
            value.toByte()
        )
    }

    /**
     * Address
     * The Address format is derived from the OctetString Basic AVP
     * Format.  It is a discriminated union representing, for example, a
     * 32-bit (IPv4) [RFC0791] or 128-bit (IPv6) [RFC4291] address, most
     * significant octet first.  The first two octets of the Address AVP
     * represent the AddressType, which contains an Address Family,
     * defined in [IANAADFAM].  The AddressType is used to discriminate
     * the content and format of the remaining octets.
     */
    fun fromInetAddress(inputAddress: InetAddress): ByteArray {
        val actualAddressBytes = inputAddress.address
        val outputAddressBytes = ByteArray(actualAddressBytes.size + 2)

        val addressType = when (inputAddress) {
            is Inet4Address -> AddressFamily.IPV4.num
            is Inet6Address -> AddressFamily.IPV6.num
            else -> throw IllegalArgumentException("Unknown InetAddress: $inputAddress")
        }
        outputAddressBytes[0] = (addressType shr 8 and 0xFF).toByte()
        outputAddressBytes[1] = (addressType shr 0 and 0xFF).toByte()

        System.arraycopy(actualAddressBytes, 0, outputAddressBytes, 2, actualAddressBytes.size)
        return outputAddressBytes
    }
}