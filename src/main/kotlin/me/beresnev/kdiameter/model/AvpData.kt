@file:Suppress("ArrayInDataClass")

package me.beresnev.kdiameter.model

import me.beresnev.kdiameter.converter.FromByteConverter
import me.beresnev.kdiameter.network.message.DiameterMessageDecoder
import net.jcip.annotations.ThreadSafe
import java.net.InetAddress
import java.nio.charset.Charset

/**
 * Provides convenient mapping methods for received
 * AVP data, which comes in the form of bytes
 *
 * Returned values should be saved/cached, especially
 * the most expensive [asGroupedAvps]
 *
 * To avoid runtime mapping exceptions, you MUST be sure
 * that AVP data (underlying byte array) is indeed the type
 * you're trying to map to. If not sure, get [raw] and
 * perform mapping on your own.
 */
@ThreadSafe
class AvpData(
    private val rawData: ByteArray
) {
    fun raw(): ByteArray {
        return rawData
    }

    fun asUTF8String(): String {
        return asString(Charsets.UTF_8)
    }

    fun asString(charSet: Charset): String {
        return String(rawData, charSet)
    }

    fun asInt(): Int {
        return FromByteConverter.toInt(rawData)
    }

    fun asInetAddress(): InetAddress {
        return FromByteConverter.toInetAddress(rawData)
    }

    fun asGroupedAvps(): List<Avp> {
        return DiameterMessageDecoder.decodeAvps(rawData)
    }
}