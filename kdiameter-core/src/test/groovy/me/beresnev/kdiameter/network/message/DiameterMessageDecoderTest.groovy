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


import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

class DiameterMessageDecoderTest extends Specification {



    def "should decode CER, without checking AVPs"() {
        when:
        def diameterMessage = DiameterMessageDecoder.INSTANCE.decode(DiameterMessageDump.CER_DUMP_FULL)
        def commandFlags = diameterMessage.commandFlags

        then:
        commandFlags.isRequest
        !commandFlags.isProxiable
        !commandFlags.isError
        !commandFlags.isPotentiallyRetransmitted

        diameterMessage.commandCode == 257
        diameterMessage.applicationId == 0

        diameterMessage.hopByHop == 0x4618a7cc
        diameterMessage.endToEnd == 0x0ce00000
    }

    @Unroll
    def "should decode CER AVPs and assert avp values"() {
        when:
        def diameterMessage = DiameterMessageDecoder.INSTANCE.decode(DiameterMessageDump.CER_DUMP_FULL)

        then:
        diameterMessage.avps.size() == 8

        expect:
        def avp = diameterMessage.avpsMap.get(code)
        getTypedAvpDataForExpectedValue(avp.avpData, expectedValue) == expectedValue

        where:
        code | expectedValue
        264L | "127.0.0.1"
        296L | "pcrf"
        257L | InetAddress.getByName("127.0.0.1")
        266L | 0
        269L | "PCRF-Tester"
        258L | 16777238
        267L | 1
        278L | 1176020954
    }

    @Ignore("helper function")
    def getTypedAvpDataForExpectedValue(def avpData, def expectedValue) {
        if (expectedValue instanceof String) {
            return avpData.asUTF8String()
        } else if (expectedValue instanceof Integer) {
            return avpData.asInt()
        } else if (expectedValue instanceof InetAddress) {
            return avpData.asInetAddress()
        } else {
            throw new IllegalStateException("Unknown value type")
        }
    }

    def "should decode CER AVPs and assert flags"() {
        when:
        def diameterMessage = DiameterMessageDecoder.INSTANCE.decode(DiameterMessageDump.CER_DUMP_FULL)

        then:
        diameterMessage.avps.size() == 8

        expect:
        def avpsMap = diameterMessage.avpsMap
        def avp = avpsMap.get(code)

        avp.avpFlags.vendorSpecific == isVendorSpecific
        avp.avpFlags.mandatory == isMandatory
        avp.avpFlags.protected == isProtected

        where:
        code | isVendorSpecific | isMandatory | isProtected
        264L | false            | true        | false
        296L | false            | true        | false
        257L | false            | true        | false
        266L | false            | true        | false
        269L | false            | false       | false
        258L | false            | true        | false
        267L | false            | false       | false
        278L | false            | true        | false
    }

    def "should throw IllegalArgumentException for unsupported diameter message version"() {
        given:
        def dumpCopy = DiameterMessageDump.CER_DUMP_FULL.clone()

        when:
        dumpCopy[0] = 0x05 // change version, it's always the first byte
        def diameterMessage = DiameterMessageDecoder.INSTANCE.decode(dumpCopy)

        then:
        IllegalArgumentException e = thrown()
        e.message == "Unsupported diameter message version: 5"
    }

    def "should throw IllegalArgumentException when received LESS bytes than diameter message carries"() {
        given:
        def dumpCopy = DiameterMessageDump.CER_DUMP_FULL.clone()

        // real value in dump is 0x88 (136)
        int newDiameterMessageLength = 0x99

        when:
        dumpCopy[3] = newDiameterMessageLength
        def diameterMessage = DiameterMessageDecoder.INSTANCE.decode(dumpCopy)

        then:
        IllegalArgumentException e = thrown()
        e.message ==
                "Diameter message is of length ${newDiameterMessageLength}, " +
                "but received ${DiameterMessageDump.CER_DUMP_FULL.size()} bytes"
    }

    def "should throw IllegalArgumentException when received MORE bytes than diameter message carries"() {
        given:
        int garbageBytes = 10
        def dumpCopy = Arrays.copyOf(DiameterMessageDump.CER_DUMP_FULL, DiameterMessageDump.CER_DUMP_FULL.size() + garbageBytes) // add some garbage

        when:
        def diameterMessage = DiameterMessageDecoder.INSTANCE.decode(dumpCopy)

        then:
        IllegalArgumentException e = thrown()
        e.message ==
                "Diameter message is of length ${DiameterMessageDump.CER_DUMP_FULL.size()}, " +
                "but received ${DiameterMessageDump.CER_DUMP_FULL.size() + garbageBytes} bytes"
    }
}
