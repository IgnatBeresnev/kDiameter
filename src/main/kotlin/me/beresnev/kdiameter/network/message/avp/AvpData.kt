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

package me.beresnev.kdiameter.network.message.avp

import me.beresnev.kdiameter.converter.FromByteConverter
import me.beresnev.kdiameter.network.message.DiameterMessageDecoder
import net.jcip.annotations.ThreadSafe
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

    fun asGroupedAvps(): List<Avp> {
        return DiameterMessageDecoder.decodeAvps(rawData)
    }
}