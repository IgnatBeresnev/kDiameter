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


import spock.lang.Specification
import spock.lang.Unroll

class DiameterMessageDecoderTest extends Specification {

    // Wireshark textual dump
    // Diameter Protocol
    // Version: 0x01
    // Length: 136
    // Flags: 0x80, Request
    //     1... .... = Request: Set
    //     .0.. .... = Proxyable: Not set
    //     ..0. .... = Error: Not set
    //     ...0 .... = T(Potentially re-transmitted message): Not set
    //     .... 0... = Reserved: Not set
    //     .... .0.. = Reserved: Not set
    //     .... ..0. = Reserved: Not set
    //     .... ...0 = Reserved: Not set
    // Command Code: 257 Capabilities-Exchange
    // ApplicationId: Diameter Common Messages (0)
    // Hop-by-Hop Identifier: 0x4618a7cc
    // End-to-End Identifier: 0x0ce00000
    //
    // AVP: Origin-Host(264) l=17 f=-M- val=127.0.0.1
    // AVP: Origin-Realm(296) l=12 f=-M- val=pcrf
    // AVP: Host-IP-Address(257) l=14 f=-M- val=127.0.0.1
    // AVP: Vendor-Id(266) l=12 f=-M- val=0
    // AVP: Product-Name(269) l=19 f=--- val=PCRF-Tester
    // AVP: Auth-Application-Id(258) l=12 f=-M- val=3GPP Gx (16777238)
    // AVP: Firmware-Revision(267) l=12 f=--- val=1
    // AVP: Origin-State-Id(278) l=12 f=-M- val=1176020954
    private static byte[] REAL_CER_DUMP = [
            0x01, 0x00, 0x00, 0x88, 0x80, 0x00, 0x01, 0x01,
            0x00, 0x00, 0x00, 0x00, 0x46, 0x18, 0xa7, 0xcc,
            0x0c, 0xe0, 0x00, 0x00, 0x00, 0x00, 0x01, 0x08,
            0x40, 0x00, 0x00, 0x11, 0x31, 0x32, 0x37, 0x2e,
            0x30, 0x2e, 0x30, 0x2e, 0x31, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x01, 0x28, 0x40, 0x00, 0x00, 0x0c,
            0x70, 0x63, 0x72, 0x66, 0x00, 0x00, 0x01, 0x01,
            0x40, 0x00, 0x00, 0x0e, 0x00, 0x01, 0x7f, 0x00,
            0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x01, 0x0a,
            0x40, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x01, 0x0d, 0x00, 0x00, 0x00, 0x13,
            0x50, 0x43, 0x52, 0x46, 0x2d, 0x54, 0x65, 0x73,
            0x74, 0x65, 0x72, 0x00, 0x00, 0x00, 0x01, 0x02,
            0x40, 0x00, 0x00, 0x0c, 0x01, 0x00, 0x00, 0x16,
            0x00, 0x00, 0x01, 0x0b, 0x00, 0x00, 0x00, 0x0c,
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x01, 0x16,
            0x40, 0x00, 0x00, 0x0c, 0x46, 0x18, 0xa7, 0xda
    ]

    def "should decode CER, without checking AVPs"() {
        when:
        def diameterMessage = DiameterMessageDecoder.INSTANCE.decode(REAL_CER_DUMP)
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
        def diameterMessage = DiameterMessageDecoder.INSTANCE.decode(REAL_CER_DUMP)

        then:
        diameterMessage.avps.size() == 8

        expect:
        def avp = diameterMessage.avpsMap.get(code)

        if (expectedValue instanceof String) {
            avp.avpData.asUTF8String() == expectedValue
        } else if (expectedValue instanceof Integer) {
            avp.avpData.asInt() == expectedValue
        } else {
            throw new IllegalStateException("Unknown value type")
        }

        where:
        code | expectedValue
        264L | "127.0.0.1"
        296L | "pcrf"
        257L | "127.0.0.1"
        266L | 0
        269L | "PCRF-Tester"
        258L | 16777238
        267L | 1
        278L | 1176020954
    }

    def "should decode CER AVPs and assert flags"() {
        when:
        def diameterMessage = DiameterMessageDecoder.INSTANCE.decode(REAL_CER_DUMP)

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
        def dumpCopy = REAL_CER_DUMP.clone()

        when:
        dumpCopy[0] = 0x05 // change version, it's always the first byte
        def diameterMessage = DiameterMessageDecoder.INSTANCE.decode(dumpCopy)

        then:
        IllegalArgumentException e = thrown()
        e.message == "Unsupported diameter message version: 5"
    }

    def "should throw IllegalArgumentException when received LESS bytes than diameter message carries"() {
        given:
        def dumpCopy = REAL_CER_DUMP.clone()

        // real value in dump is 0x88 (136)
        int newDiameterMessageLength = 0x99

        when:
        dumpCopy[3] = newDiameterMessageLength
        def diameterMessage = DiameterMessageDecoder.INSTANCE.decode(dumpCopy)

        then:
        IllegalArgumentException e = thrown()
        e.message ==
                "Diameter message is of length ${newDiameterMessageLength}, " +
                "but received ${REAL_CER_DUMP.size()} bytes"
    }

    def "should throw IllegalArgumentException when received MORE bytes than diameter message carries"() {
        given:
        int garbageBytes = 10
        def dumpCopy = Arrays.copyOf(REAL_CER_DUMP, REAL_CER_DUMP.size() + garbageBytes) // add some garbage

        when:
        def diameterMessage = DiameterMessageDecoder.INSTANCE.decode(dumpCopy)

        then:
        IllegalArgumentException e = thrown()
        e.message ==
                "Diameter message is of length ${REAL_CER_DUMP.size()}, " +
                "but received ${REAL_CER_DUMP.size() + garbageBytes} bytes"
    }
}
