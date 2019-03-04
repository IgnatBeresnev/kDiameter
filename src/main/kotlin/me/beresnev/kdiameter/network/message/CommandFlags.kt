package me.beresnev.kdiameter.network.message

import me.beresnev.kdiameter.util.BitSet

/**
 * The Command Flags field is eight bits.
 * The following bits are assigned:
 *  7 6 5 4 3 2 1 0
 * +-+-+-+-+-+-+-+-+
 * |R P E T r r r r|
 * +-+-+-+-+-+-+-+-+
 *
 * Edit: RFC's countdown is the other way around, from 0 to 7
 * Here in the doc I've changed it to be from 7 to 0 for clarity
 *
 * >> R(equest)
 * If set, the message is a request.  If cleared, the message is
 * an answer.
 *
 * >> P(roxiable)
 * If set, the message MAY be proxied, relayed, or redirected.  If
 * cleared, the message MUST be locally processed.
 *
 * >> E(rror)
 * If set, the message contains a protocol error, and the message
 * will not conform to the CCF described for this command.
 * Messages with the 'E' bit set are commonly referred to as error
 * messages.  This bit MUST NOT be set in request messages (see
 * Section 7.2).
 *
 * >> T(Potentially retransmitted message)
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
 *
 * r(eserved)
 * These flag bits are reserved for future use; they MUST be set
 * to zero and ignored by the receiver.
 */
data class CommandFlags(
    val isRequest: Boolean,
    val isProxiable: Boolean,
    val isError: Boolean,
    val isPotentiallyRetransmitted: Boolean
) {
    internal constructor(commandFlags: Int) : this(BitSet(commandFlags))

    internal constructor(flags: BitSet) : this(
        isRequest = flags.get(7),
        isProxiable = flags.get(6),
        isError = flags.get(5),
        isPotentiallyRetransmitted = flags.get(4)
    )
}