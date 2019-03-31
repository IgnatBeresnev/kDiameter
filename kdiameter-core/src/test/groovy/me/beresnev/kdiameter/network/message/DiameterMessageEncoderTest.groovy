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
import me.beresnev.kdiameter.network.message.flags.AvpFlags
import me.beresnev.kdiameter.network.message.flags.CommandFlags
import spock.lang.Specification

class DiameterMessageEncoderTest extends Specification {

    def "should encode message meta info without AVPs"() {
        given:
        CommandFlags commandFlags = new CommandFlags(true, false, false, false)
        def commandCode = 257
        def applicationId = 0
        def hopByHop = 0x4618a7cc
        def endToEnd = 0x0ce00000
        def avps = []

        def diameterMessage = new DiameterMessage(commandFlags, commandCode, applicationId, hopByHop, endToEnd, avps)

        when:
        def encodedMessage = DiameterMessageEncoder.INSTANCE.encode(diameterMessage)

        then:
        encodedMessage == DiameterMessageDump.CER_DUMP_WITHOUT_AVPS
    }

    def "should encode and assert only AVPs"() {
        given:
        def originHost = Avp.create(264, new AvpFlags(false, true, false), null, "127.0.0.1")
        def originRealm = Avp.create(296, new AvpFlags(false, true, false), null, "pcrf")
        def hostIpAddress = Avp.create(257, new AvpFlags(false, true, false), null, InetAddress.getByName("127.0.0.1"))
        def vendorId = Avp.create(266, new AvpFlags(false, true, false), null, 0)
        def productName = Avp.create(269, new AvpFlags(false, false, false), null, "PCRF-Tester")
        def authAppId = Avp.create(258, new AvpFlags(false, true, false), null, 16777238)
        def firmwareRevision = Avp.create(267, new AvpFlags(false, false, false), null, 1)
        def originStateId = Avp.create(278, new AvpFlags(false, true, false), null, 1176020954)
        def allAvps = [
                originHost, originRealm, hostIpAddress, vendorId,
                productName, authAppId, firmwareRevision, originStateId
        ]

        when:
        def encodedAvps = DiameterMessageEncoder.INSTANCE.encodeAvps(allAvps)

        then:
        encodedAvps == DiameterMessageDump.CER_DUMP_ONLY_AVPS
    }

    def "should encode full message with meta info and AVPs"() {
        given:
        CommandFlags commandFlags = new CommandFlags(true, false, false, false)
        def commandCode = 257
        def applicationId = 0
        def hopByHop = 0x4618a7cc
        def endToEnd = 0x0ce00000

        def originHost = Avp.create(264, new AvpFlags(false, true, false), null, "127.0.0.1")
        def originRealm = Avp.create(296, new AvpFlags(false, true, false), null, "pcrf")
        def hostIpAddress = Avp.create(257, new AvpFlags(false, true, false), null, InetAddress.getByName("127.0.0.1"))
        def vendorId = Avp.create(266, new AvpFlags(false, true, false), null, 0)
        def productName = Avp.create(269, new AvpFlags(false, false, false), null, "PCRF-Tester")
        def authAppId = Avp.create(258, new AvpFlags(false, true, false), null, 16777238)
        def firmwareRevision = Avp.create(267, new AvpFlags(false, false, false), null, 1)
        def originStateId = Avp.create(278, new AvpFlags(false, true, false), null, 1176020954)
        def avps = [
                originHost, originRealm, hostIpAddress, vendorId,
                productName, authAppId, firmwareRevision, originStateId
        ]

        def diameterMessage = new DiameterMessage(commandFlags, commandCode, applicationId, hopByHop, endToEnd, avps)

        when:
        def encodedMessage = DiameterMessageEncoder.INSTANCE.encode(diameterMessage)

        then:
        encodedMessage == DiameterMessageDump.CER_DUMP_FULL
    }
}
