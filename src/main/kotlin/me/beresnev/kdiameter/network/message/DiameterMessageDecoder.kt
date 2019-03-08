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

package me.beresnev.kdiameter.network.message

import me.beresnev.kdiameter.extensions.readByte
import me.beresnev.kdiameter.extensions.readFourBytes
import me.beresnev.kdiameter.extensions.readThreeBytes
import me.beresnev.kdiameter.extensions.toUnsignedLong
import me.beresnev.kdiameter.network.message.avp.Avp
import me.beresnev.kdiameter.network.message.flags.AvpFlags
import me.beresnev.kdiameter.network.message.flags.CommandFlags
import net.jcip.annotations.ThreadSafe
import java.io.ByteArrayInputStream
import java.io.InputStream

@ThreadSafe
object DiameterMessageDecoder {

    /** 0               1               2               3
     *  0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |    Version    |                 Message Length                |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * | Command Flags |                  Command Code                 |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                         Application-ID                        |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                      Hop-by-Hop Identifier                    |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                      End-to-End Identifier                    |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                             AVPs ...                          |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     */
    fun decode(message: ByteArray): DiameterMessage {
        val dataStream = ByteArrayInputStream(message)

        assertMessageVersionIsSupported(dataStream.readByte())
        assertMessageLength(message.size, dataStream)

        val commandFlags = CommandFlags(dataStream.readByte())
        val commandCode = dataStream.readThreeBytes()

        // while in RFC it says nothing of type (signed/unsigned), and since
        // applicationIds are user defined, we assume it can be unsigned
        val applicationId = dataStream.readFourBytes().toUnsignedLong()

        // RFC clearly states that both are unsigned 32-bit integers
        val hopByHop = dataStream.readFourBytes().toUnsignedLong()
        val endToEnd = dataStream.readFourBytes().toUnsignedLong()

        val avps = decodeAvps(dataStream)

        return DiameterMessage(commandFlags, commandCode, applicationId, hopByHop, endToEnd, avps)
    }

    /**
     * This Version field MUST be set to 1 to indicate Diameter Version 1
     */
    private fun assertMessageVersionIsSupported(version: Int) {
        if (version != 1) {
            throw IllegalArgumentException("Unsupported diameter message version: $version")
        }
    }

    /**
     * The Message Length field is three octets and indicates the length
     * of the Diameter message including the header fields and the padded
     * AVPs. Thus, the Message Length field is always a multiple of 4
     */
    private fun assertMessageLength(receivedBytes: Int, dataStream: InputStream) {
        val diameterMessageLength = dataStream.readThreeBytes() // won't overflow
        if (receivedBytes != diameterMessageLength) {
            throw IllegalArgumentException(
                "Diameter message is of length $diameterMessageLength, but received ${receivedBytes} bytes"
            )
        }
    }

    fun decodeAvps(data: ByteArray): List<Avp> {
        return decodeAvps(ByteArrayInputStream(data))
    }

    private fun decodeAvps(dataStream: InputStream): List<Avp> {
        if (dataStream.available() == 0) {
            return emptyList()
        }

        val avps = ArrayList<Avp>()
        while (dataStream.available() > 0) {
            avps.add(decodeAvp(dataStream))
        }
        return avps
    }

    /**
     * 0               1               2               3
     * 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                           AVP Code                          |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * | V M P r r r r r|                  AVP Length                |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                        Vendor-ID (opt)                      |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                            Data ...                         |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     */
    private fun decodeAvp(dataStream: InputStream): Avp {
        val avpCode = dataStream.readFourBytes().toUnsignedLong()
        val avpFlags = AvpFlags(dataStream.readByte())

        // The AVP Length field is three octets, and indicates the number of
        // octets in this AVP including the AVP Code field, AVP Length field,
        // AVP Flags field, Vendor-ID field (if present), and the AVP Data
        // field.  If a message is received with an invalid attribute length,
        // the message MUST be rejected.
        val avpLength = dataStream.readThreeBytes()

        val hasVendorId = avpFlags.isVendorSpecific
        val vendorId = if (hasVendorId) dataStream.readFourBytes().toUnsignedLong() else null

        // since we already read some of the bytes from the stream,
        // we need to calculate how many bytes are left for data
        // dataLength = avpLength - 4(code) - 1(flags) - 3(length) [- 4 (vendor)]
        val dataLength = avpLength - 8 - if (hasVendorId) 4 else 0

        val rawData = ByteArray(dataLength)
        dataStream.read(rawData, 0, dataLength)

        if (avpLength % 4 != 0) { // value not aligned, must skip empty padding bytes
            skipPadding(avpLength, dataStream)
        }
        return Avp.create(avpCode, avpFlags, vendorId, rawData)
    }

    /**
     * Each AVP of type OctetString MUST be padded to align on a 32-bit
     * boundary, while other AVP types align naturally.  A number of zero-
     * valued bytes are added to the end of the AVP Data field until a word
     * boundary is reached.
     *
     * NOTE! "The length of the padding is not reflected in the AVP Length field."
     * This is the reason we need to skip empty padding bytes.
     */
    private fun skipPadding(avpLength: Int, dataStream: InputStream) {
        var avpLengthWithPadding = avpLength.toLong()
        do {
            // https://en.wikipedia.org/wiki/Data_structure_alignment#Computing_padding
            val padding = (4L - avpLengthWithPadding % 4L) % 4L
            avpLengthWithPadding += dataStream.skip(padding)
        } while (avpLengthWithPadding % 4L != 0L)
    }
}