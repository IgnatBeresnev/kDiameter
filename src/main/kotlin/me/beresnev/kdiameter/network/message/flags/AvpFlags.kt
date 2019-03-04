package me.beresnev.kdiameter.network.message.flags

import me.beresnev.kdiameter.util.BitSet

/**
 * The AVP Flags field informs the receiver how each attribute must
 * be handled.  New Diameter applications SHOULD NOT define
 * additional AVP Flag bits.  However, note that new Diameter
 * applications MAY define additional bits within the AVP header, and
 * an unrecognized bit SHOULD be considered an error.  The sender of
 * the AVP MUST set 'R' (reserved) bits to 0 and the receiver SHOULD
 * ignore all 'R' (reserved) bits.  The 'P' bit has been reserved for
 * future usage of end-to-end security.  At the time of writing,
 * there are no end-to-end security mechanisms specified; therefore,
 * the 'P' bit SHOULD be set to 0.
 *
 * The 'M' bit, known as the Mandatory bit, indicates whether the
 * receiver of the AVP MUST parse and understand the semantics of the
 * AVP including its content.  The receiving entity MUST return an
 * appropriate error message if it receives an AVP that has the M-bit
 * set but does not understand it.  An exception applies when the AVP
 * is embedded within a Grouped AVP.  See Section 4.4 for details.
 * Diameter relay and redirect agents MUST NOT reject messages with
 * unrecognized AVPs.
 *
 * The 'M' bit MUST be set according to the rules defined in the
 * application specification that introduces or reuses this AVP.
 * Within a given application, the M-bit setting for an AVP is
 * defined either for all command types or for each command type.
 *
 * AVPs with the 'M' bit cleared are informational only; a receiver
 * that receives a message with such an AVP that is not supported, or
 * whose value is not supported, MAY simply ignore the AVP.
 *
 * The 'V' bit, known as the Vendor-Specific bit, indicates whether
 * the optional Vendor-ID field is present in the AVP header.  When
 * set, the AVP Code belongs to the specific vendor code address
 * space.
 */
data class AvpFlags(
    val isVendorSpecific: Boolean,
    val isMandatory: Boolean,
    val isProtected: Boolean
) {
    internal constructor(commandFlags: Int) : this(BitSet(commandFlags))

    //  7 6 5 4 3 2 1 0
    // +-+-+-+-+-+-+-+-+
    // |V M P r r r r r|
    // +-+-+-+-+-+-+-+-+
    // r(eserved)
    // These flag bits are reserved for future use; they MUST be set
    // to zero and ignored by the receiver.
    internal constructor(flags: BitSet) : this(
        isVendorSpecific = flags.get(7),
        isMandatory = flags.get(6),
        isProtected = flags.get(5)
    )
}