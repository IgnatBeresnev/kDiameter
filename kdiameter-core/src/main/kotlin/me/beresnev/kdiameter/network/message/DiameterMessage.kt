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

import me.beresnev.kdiameter.network.message.avp.Avp
import me.beresnev.kdiameter.network.message.avp.AvpsMap
import me.beresnev.kdiameter.network.message.flags.CommandFlags

/**
 * >> Command Code
 * The Command Code field is three octets and is used in order to
 * communicate the command associated with the message.  The 24-bit
 * address space is managed by IANA (see Section 3.1).  Command Code
 * values 16,777,214 and 16,777,215 (hexadecimal values FFFFFE-
 * FFFFFF) are reserved for experimental use (see Section 11.2).
 *
 * >> Application-ID
 * Application-ID is four octets and is used to identify for which
 * application the message is applicable.  The application can be an
 * authentication application, an accounting application, or a
 * vendor-specific application.
 * The value of the Application-ID field in the header MUST be the
 * same as any relevant Application-Id AVPs contained in the message.
 *
 * >> Hop-by-Hop Identifier
 * The Hop-by-Hop Identifier is an unsigned 32-bit integer field (in
 * network byte order) that aids in matching requests and replies.
 * The sender MUST ensure that the Hop-by-Hop Identifier in a request
 * is unique on a given connection at any given time, and it MAY
 * attempt to ensure that the number is unique across reboots.  The
 * sender of an answer message MUST ensure that the Hop-by-Hop
 * Identifier field contains the same value that was found in the
 * corresponding request.  The Hop-by-Hop Identifier is normally a
 * monotonically increasing number, whose start value was randomly
 * generated.  An answer message that is received with an unknown
 * Hop-by-Hop Identifier MUST be discarded.
 *
 * >> End-to-End Identifier
 * The End-to-End Identifier is an unsigned 32-bit integer field (in
 * network byte order) that is used to detect duplicate messages.
 * Upon reboot, implementations MAY set the high order 12 bits to
 * contain the low order 12 bits of current time, and the low order
 * 20 bits to a random value.  Senders of request messages MUST
 * insert a unique identifier on each message.  The identifier MUST
 * remain locally unique for a period of at least 4 minutes, even
 * across reboots.  The originator of an answer message MUST ensure
 * that the End-to-End Identifier field contains the same value that
 * was found in the corresponding request.  The End-to-End Identifier
 * MUST NOT be modified by Diameter agents of any kind.  The
 * combination of the Origin-Host AVP (Section 6.3) and this field is
 * used to detect duplicates.  Duplicate requests SHOULD cause the
 * same answer to be transmitted (modulo the Hop-by-Hop Identifier
 * field and any routing AVPs that may be present), and they MUST NOT
 * affect any state that was set when the original request was
 * processed.  Duplicate answer messages that are to be locally
 * consumed (see Section 6.2) SHOULD be silently discarded.
 *
 * >> AVPs
 * AVPs are a method of encapsulating information relevant to the
 * Diameter message.  See Section 4 for more information on AVPs.
 */
data class DiameterMessage(
    val commandFlags: CommandFlags,
    val commandCode: Int,

    val applicationId: Long,

    val hopByHop: Long,
    val endToEnd: Long,

    val avps: List<Avp>
) {
    val avpsMap: AvpsMap by lazy {
        AvpsMap(avps)
    }
}