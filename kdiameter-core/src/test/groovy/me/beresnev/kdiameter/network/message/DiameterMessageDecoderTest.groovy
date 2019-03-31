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

    def "should decode message meta info without AVPs"() {
        when:
        def message = DiameterMessageDecoder.INSTANCE.decode(DiameterMessageDump.CER_DUMP_WITHOUT_AVPS)
        def commandFlags = message.commandFlags

        then:
        commandFlags.isRequest
        !commandFlags.isProxiable
        !commandFlags.isError
        !commandFlags.isPotentiallyRetransmitted

        message.commandCode == 257
        message.applicationId == 0

        message.hopByHop == 0x4618a7cc
        message.endToEnd == 0x0ce00000
    }

    @Unroll
    def "should decode message AVPs and assert values"() {
        when:
        def avps = DiameterMessageDecoder.INSTANCE.decodeAvps(DiameterMessageDump.CER_DUMP_ONLY_AVPS)

        then:
        avps.size() == 8

        expect:
        def avp = avps[index]
        avp.code == code

        def value = getTypedAvpDataForExpectedValue(avp.avpData, expectedValue)
        value == expectedValue

        where:
        index | code | expectedValue
        0     | 264L | "127.0.0.1"
        1     | 296L | "pcrf"
        2     | 257L | InetAddress.getByName("127.0.0.1")
        3     | 266L | 0
        4     | 269L | "PCRF-Tester"
        5     | 258L | 16777238
        6     | 267L | 1
        7     | 278L | 1176020954
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

    def "should decode message AVPs and assert only flags"() {
        when:
        def avps = DiameterMessageDecoder.INSTANCE.decodeAvps(DiameterMessageDump.CER_DUMP_ONLY_AVPS)

        then:
        avps.size() == 8

        expect:
        def avp = avps[index]
        avp.code == code

        avp.avpFlags.vendorSpecific == isVendorSpecific
        avp.avpFlags.mandatory == isMandatory
        avp.avpFlags.protected == isProtected

        where:
        index | code | isVendorSpecific | isMandatory | isProtected
        0     | 264L | false            | true        | false
        1     | 296L | false            | true        | false
        2     | 257L | false            | true        | false
        3     | 266L | false            | true        | false
        4     | 269L | false            | false       | false
        5     | 258L | false            | true        | false
        6     | 267L | false            | false       | false
        7     | 278L | false            | true        | false
    }

    def "should decode full message and have correct message and avps size"() {
        when:
        def message = DiameterMessageDecoder.INSTANCE.decode(DiameterMessageDump.CER_DUMP_FULL)

        then:
        message.commandCode == 257
        message.applicationId == 0

        message.hopByHop == 0x4618a7cc
        message.endToEnd == 0x0ce00000

        message.avps.size() == 8
    }

    def "should throw IllegalArgumentException for unsupported diameter message version"() {
        given:
        def dumpCopy = DiameterMessageDump.CER_DUMP_FULL.clone()

        when:
        dumpCopy[0] = 0x05 // change version, it's always the first byte
        def message = DiameterMessageDecoder.INSTANCE.decode(dumpCopy)

        then:
        IllegalArgumentException e = thrown()
        e.message == "Unsupported diameter message version: 5"
    }

    def "should throw IllegalArgumentException when received LESS bytes than diameter message carries"() {
        given:
        def dumpCopy = DiameterMessageDump.CER_DUMP_FULL.clone()

        // real value in dump is 0x88 (136)
        int newMessageLength = 0x99

        when:
        dumpCopy[3] = newMessageLength
        def message = DiameterMessageDecoder.INSTANCE.decode(dumpCopy)

        then:
        IllegalArgumentException e = thrown()
        e.message ==
                "Diameter message is of length ${newMessageLength}, " +
                "but received ${DiameterMessageDump.CER_DUMP_FULL.size()} bytes"
    }

    def "should throw IllegalArgumentException when received MORE bytes than diameter message carries"() {
        given:
        int garbageBytes = 10
        def dumpCopy = Arrays.copyOf(
                DiameterMessageDump.CER_DUMP_FULL,
                DiameterMessageDump.CER_DUMP_FULL.size() + garbageBytes
        )

        when:
        def message = DiameterMessageDecoder.INSTANCE.decode(dumpCopy)

        then:
        IllegalArgumentException e = thrown()
        e.message ==
                "Diameter message is of length ${DiameterMessageDump.CER_DUMP_FULL.size()}, " +
                "but received ${DiameterMessageDump.CER_DUMP_FULL.size() + garbageBytes} bytes"
    }
}
