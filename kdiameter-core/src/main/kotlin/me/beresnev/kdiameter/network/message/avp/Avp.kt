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

import me.beresnev.kdiameter.converter.ToByteConverter
import me.beresnev.kdiameter.network.message.flags.AvpFlags
import java.net.InetAddress

/**
 * Diameter AVPs carry specific authentication, accounting,
 * authorization, and routing information as well as configuration
 * details for the request and reply.
 *
 * >> AVP Code
 * The AVP Code, combined with the Vendor-Id field, identifies the
 * attribute uniquely.  AVP numbers 1 through 255 are reserved for
 * reuse of RADIUS attributes, without setting the Vendor-Id field.
 * AVP numbers 256 and above are used for Diameter, which are
 * allocated by IANA (see Section 11.1.1).
 *
 * >> AVP Flags
 * @see me.beresnev.kdiameter.network.message.flags.AvpFlags
 *
 * >> Vendor-ID
 * The Vendor-ID field is present if the 'V' bit is set in the AVP
 * Flags field.  The optional four-octet Vendor-ID field contains the
 * IANA-assigned "SMI Network Management Private Enterprise Codes"
 * [ENTERPRISE] value, encoded in network byte order.  Any vendors or
 * standardization organizations that are also treated like vendors
 * in the IANA-managed "SMI Network Management Private Enterprise
 * Codes" space wishing to implement a vendor-specific Diameter AVP
 * MUST use their own Vendor-ID along with their privately managed
 * AVP address space, guaranteeing that they will not collide with
 * any other vendor's vendor-specific AVP(s) or with future IETF
 * AVPs.
 * A Vendor-ID value of zero (0) corresponds to the IETF-adopted AVP
 * values, as managed by IANA.  Since the absence of the Vendor-ID
 * field implies that the AVP in question is not vendor specific,
 * implementations MUST NOT use the value of zero (0) for the
 * Vendor-ID field.
 */
class Avp private constructor(
    val code: Long,

    val avpFlags: AvpFlags,
    val vendorId: Long?,

    val avpData: AvpData
) {
    companion object {
        @JvmStatic
        fun create(code: Long, avpFlags: AvpFlags, vendorId: Long?, value: Int): Avp {
            return create(code, avpFlags, vendorId, ToByteConverter.fromInt(value))
        }

        @JvmStatic
        fun create(code: Long, avpFlags: AvpFlags, vendorId: Long?, value: String): Avp {
            return create(code, avpFlags, vendorId, value.toByteArray())
        }

        @JvmStatic
        fun create(code: Long, avpFlags: AvpFlags, vendorId: Long?, inetAddress: InetAddress): Avp {
            return create(code, avpFlags, vendorId, ToByteConverter.fromInetAddress(inetAddress))
        }

        fun create(code: Long, avpFlags: AvpFlags, vendorId: Long?, value: ByteArray): Avp {
            return Avp(code, avpFlags, vendorId, AvpData(value))
        }
    }
}
