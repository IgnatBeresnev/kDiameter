package me.beresnev.kdiameter.network.message

import spock.lang.Specification

class DiameterMessageDecoderTest extends Specification {

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
        given:
        def messageDecoder = new DiameterMessageDecoder()

        when:
        def diameterMessage = messageDecoder.decode(REAL_CER_DUMP)
        def commandFlags = diameterMessage.commandFlags

        then:
        commandFlags.isRequest

        !commandFlags.isProxiable
        !commandFlags.isError
        !commandFlags.isPotentiallyRetransmitted

        diameterMessage.commandCode == 257

        diameterMessage.applicationId == 0

        diameterMessage.hopByHop == 1176020940 // 0x4618a7cc in dump
        diameterMessage.endToEnd == 216006656 // 0x0ce00000 in dump
    }

    def "should throw IllegalArgumentException for unsupported diameter message version"() {
        given:
        def messageDecoder = new DiameterMessageDecoder()
        def dumpCopy = REAL_CER_DUMP.clone()

        when:
        dumpCopy[0] = 0x05 // change version, it's always the first byte
        def diameterMessage = messageDecoder.decode(dumpCopy)

        then:
        IllegalArgumentException e = thrown()
        e.message == "Unsupported diameter message version: 5"
    }

    def "should throw IllegalArgumentException when received LESS bytes than diameter message carries"() {
        given:
        def messageDecoder = new DiameterMessageDecoder()
        def dumpCopy = REAL_CER_DUMP.clone()

        // real value in dump is 0x88 (136)
        int newDiameterMessageLength = 0x99

        when:
        dumpCopy[3] = newDiameterMessageLength
        def diameterMessage = messageDecoder.decode(dumpCopy)

        then:
        IllegalArgumentException e = thrown()
        e.message ==
                "Diameter message is of length ${newDiameterMessageLength}, " +
                "but received ${REAL_CER_DUMP.size()} bytes"
    }

    def "should throw IllegalArgumentException when received MORE bytes than diameter message carries"() {
        given:
        def messageDecoder = new DiameterMessageDecoder()

        int garbageBytes = 10
        def dumpCopy = Arrays.copyOf(REAL_CER_DUMP, REAL_CER_DUMP.size() + garbageBytes) // add some garbage

        when:
        def diameterMessage = messageDecoder.decode(dumpCopy)

        then:
        IllegalArgumentException e = thrown()
        e.message ==
                "Diameter message is of length ${REAL_CER_DUMP.size()}, " +
                "but received ${REAL_CER_DUMP.size() + garbageBytes} bytes"
    }
}
