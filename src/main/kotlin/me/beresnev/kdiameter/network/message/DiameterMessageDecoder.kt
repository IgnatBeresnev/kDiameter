package me.beresnev.kdiameter.network.message

import me.beresnev.kdiameter.extensions.readByte
import me.beresnev.kdiameter.extensions.readFourBytes
import me.beresnev.kdiameter.extensions.readThreeBytes
import me.beresnev.kdiameter.extensions.toUnsignedLong
import me.beresnev.kdiameter.model.Avp
import java.io.ByteArrayInputStream
import java.io.InputStream

class DiameterMessageDecoder {

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

        assertMessageVersionIsSupported(dataStream.readByte()) // first byte is version
        assertMessageLength(message.size, dataStream) // next three are for message length

        val commandFlags = CommandFlags(dataStream.readByte()) // next byte is for command flags
        val commandCode = dataStream.readThreeBytes() // next three are for command code

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

    private fun decodeAvps(dataStream: InputStream): List<Avp> {
        return emptyList()
    }
}