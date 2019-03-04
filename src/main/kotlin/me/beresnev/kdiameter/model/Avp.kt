package me.beresnev.kdiameter.model

import me.beresnev.kdiameter.converter.ToByteConverter
import me.beresnev.kdiameter.network.message.flags.AvpFlags

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
 * @see [me.beresnev.kdiameter.network.message.AvpFlags]
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
        fun create(code: Long, avpFlags: AvpFlags, vendorId: Long?, value: Int): Avp {
            return create(code, avpFlags, vendorId, ToByteConverter.toByteArray(value))
        }

        fun create(code: Long, avpFlags: AvpFlags, vendorId: Long?, value: String): Avp {
            return create(code, avpFlags, vendorId, value.toByteArray())
        }

        fun create(code: Long, avpFlags: AvpFlags, vendorId: Long?, value: ByteArray): Avp {
            return Avp(code, avpFlags, vendorId, AvpData(value))
        }
    }
}
