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
import java.net.InetAddress
import java.nio.charset.Charset

object FromByteConverter {

    fun toString(data: ByteArray, charSet: Charset) = String(data, charSet)

    fun toInt(data: ByteArray): Int {
        if (data.size != 4) {
            throw IllegalArgumentException("Expected 4 bytes, got ${data.size}")
        }
        return data[0].toInt() shl 24 or
                (data[1].toInt() and 0xFF shl 16) or
                (data[2].toInt() and 0xFF shl 8) or
                (data[3].toInt() and 0xFF)
    }

    /**
     * Address
     * The Address format is derived from the OctetString Basic AVP
     * Format.  It is a discriminated union representing, for example, a
     * 32-bit (IPv4) [RFC0791] or 128-bit (IPv6) [RFC4291] address, most
     * significant octet first.  The first two octets of the Address AVP
     * represent the AddressType, which contains an Address Family,
     * defined in [AddressFamily]. The AddressType is used to discriminate
     * the content and format of the remaining octets.
     */
    fun toInetAddress(rawData: ByteArray): InetAddress {
        val isIPv6 = rawData[1].toInt() != AddressFamily.IPV4.num
        val address = ByteArray(if (isIPv6) 16 else 4)
        System.arraycopy(rawData, 2, address, 0, address.size)
        return InetAddress.getByAddress(address)
    }
}