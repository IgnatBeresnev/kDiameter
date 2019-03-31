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

import me.beresnev.kdiameter.extensions.stream.writeByte
import me.beresnev.kdiameter.extensions.stream.writeFourBytes
import me.beresnev.kdiameter.extensions.stream.writeThreeBytes
import me.beresnev.kdiameter.network.message.avp.Avp
import java.io.ByteArrayOutputStream

object DiameterMessageEncoder {

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
     *
     * @see DiameterMessageDecoder.decode
     */
    fun encode(message: DiameterMessage): ByteArray {
        val encodedAvps = encodeAvps(message.avps)

        // Version(1) + MessageLength(3) + Flags(1) + CommandCode(3)
        // Application-Id(4) + HopByHop(4) + EndToEnd(4) == 20
        val messageLength = 20 + encodedAvps.size

        val byteStream = ByteArrayOutputStream()
        byteStream.writeByte(1) // support only for version 1
        byteStream.writeThreeBytes(messageLength)
        byteStream.writeByte(message.commandFlags.getAsAssertedByte())
        byteStream.writeThreeBytes(message.commandCode)
        byteStream.writeFourBytes(message.applicationId)
        byteStream.writeFourBytes(message.hopByHop)
        byteStream.writeFourBytes(message.endToEnd)
        byteStream.write(encodedAvps)

        return byteStream.toByteArray()
    }

    fun encodeAvps(avps: List<Avp>): ByteArray {
        val byteStream = ByteArrayOutputStream()
        avps.forEach { avp -> writeAvp(avp, byteStream) }

        return byteStream.toByteArray()
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
     *
     * @see DiameterMessageDecoder.decodeAvp
     */
    private fun writeAvp(avp: Avp, byteStream: ByteArrayOutputStream) {
        val data = avp.avpData.raw()
        // The length of the padding is not reflected in the AVP Length field.
        // we'll be using this field to compute AVP Length, even if it does
        // not accurately represent data we're writing to the stream
        val dataLength = data.size
        val alignedData = alignDataIfNeeded(data)

        val vendorId = avp.vendorId

        // The AVP Length field is three octets, and indicates the number of
        // octets in this AVP including the AVP Code field, AVP Length field,
        // AVP Flags field, Vendor-ID field (if present), and the AVP Data
        // field.  If a message is received with an invalid attribute length,
        // the message MUST be rejected.
        // AVP Code (4) + flags (1) + AVP Length (3) = 8
        val avpLength = 8 + (if (vendorId != null) 4 else 0) + dataLength

        byteStream.writeFourBytes(avp.code)
        byteStream.writeByte(avp.avpFlags.getAsAssertedByte())
        byteStream.writeThreeBytes(avpLength)
        if (vendorId != null) {
            byteStream.writeFourBytes(vendorId)
        }
        byteStream.write(alignedData)
    }

    /**
     * Each AVP of type OctetString MUST be padded to align on a 32-bit
     * boundary, while other AVP types align naturally.  A number of zero-
     * valued bytes are added to the end of the AVP Data field until a word
     * boundary is reached.
     *
     * NOTE! "The length of the padding is not reflected in the AVP Length field."
     * This is the reason we need to skip empty padding bytes.
     *
     * @see DiameterMessageDecoder.skipPadding
     */
    private fun alignDataIfNeeded(data: ByteArray): ByteArray {
        val dataSize = data.size
        if (dataSize % 4 == 0) {
            return data
        }

        // https://en.wikipedia.org/wiki/Data_structure_alignment#Computing_padding
        val padding = (4 - dataSize % 4) % 4
        return data.copyOf(dataSize + padding)
    }
}