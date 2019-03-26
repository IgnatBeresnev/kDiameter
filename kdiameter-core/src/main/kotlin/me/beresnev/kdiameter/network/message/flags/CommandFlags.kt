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

package me.beresnev.kdiameter.network.message.flags

import me.beresnev.kdiameter.util.BitSet

/**
 * >> Request
 * If set, the message is a request.  If cleared, the message is
 * an answer.
 *
 * >> Proxiable
 * If set, the message MAY be proxied, relayed, or redirected.  If
 * cleared, the message MUST be locally processed.
 *
 * >> Error
 * If set, the message contains a protocol error, and the message
 * will not conform to the CCF described for this command.
 * Messages with the 'E' bit set are commonly referred to as error
 * messages.  This bit MUST NOT be set in request messages (see
 * Section 7.2).
 *
 * >> Potentially retransmitted message
 * This flag is set after a link failover procedure, to aid the
 * removal of duplicate requests.  It is set when resending
 * requests not yet acknowledged, as an indication of a possible
 * duplicate due to a link failure.  This bit MUST be cleared when
 * sending a request for the first time; otherwise, the sender
 * MUST set this flag.  Diameter agents only need to be concerned
 * about the number of requests they send based on a single
 * received request; retransmissions by other entities need not be
 * tracked.  Diameter agents that receive a request with the T
 * flag set, MUST keep the T flag set in the forwarded request.
 * This flag MUST NOT be set if an error answer message (e.g., a
 * protocol error) has been received for the earlier message.  It
 * can be set only in cases where no answer has been received from
 * the server for a request, and the request has been sent again.
 * This flag MUST NOT be set in answer messages.
 */
data class CommandFlags(
    val isRequest: Boolean,
    val isProxiable: Boolean,
    val isError: Boolean,
    val isPotentiallyRetransmitted: Boolean
) {
    internal constructor(commandFlags: Int) : this(BitSet(commandFlags))

    //  7 6 5 4 3 2 1 0
    // +-+-+-+-+-+-+-+-+
    // |R P E T r r r r|
    // +-+-+-+-+-+-+-+-+
    // r(eserved)
    // These flag bits are reserved for future use; they MUST be set
    // to zero and ignored by the receiver.
    private constructor(flags: BitSet) : this(
        isRequest = flags.get(7),
        isProxiable = flags.get(6),
        isError = flags.get(5),
        isPotentiallyRetransmitted = flags.get(4)
    )

    /**
     * @see me.beresnev.kdiameter.util.BitSet.assertInByteRange
     */
    fun getAsAssertedByte(): Int {
        val bitSet = BitSet()
        bitSet.set(7, isRequest)
        bitSet.set(6, isProxiable)
        bitSet.set(5, isError)
        bitSet.set(4, isPotentiallyRetransmitted)

        bitSet.assertInByteRange()
        return bitSet.asInt()
    }
}