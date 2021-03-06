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

class DiameterMessageDump {
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
    public static byte[] CER_DUMP_FULL = [
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

    // Wireshark textual dump
    // Diameter Protocol
    // Version: 0x01
    // Length: 20
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
    // IDENTICAL TO FULL CER DUMP, ONLY THING CHANGED IS LENGTH (from 136 (0x88) to 20 (0x14))
    public static byte[] CER_DUMP_WITHOUT_AVPS = [
            0x01, 0x00, 0x00, 0x14, 0x80, 0x00, 0x01, 0x01,
            0x00, 0x00, 0x00, 0x00, 0x46, 0x18, 0xa7, 0xcc,
            0x0c, 0xe0, 0x00, 0x00
    ]

    // AVP: Origin-Host(264) l=17 f=-M- val=127.0.0.1
    // AVP: Origin-Realm(296) l=12 f=-M- val=pcrf
    // AVP: Host-IP-Address(257) l=14 f=-M- val=127.0.0.1
    // AVP: Vendor-Id(266) l=12 f=-M- val=0
    // AVP: Product-Name(269) l=19 f=--- val=PCRF-Tester
    // AVP: Auth-Application-Id(258) l=12 f=-M- val=3GPP Gx (16777238)
    // AVP: Firmware-Revision(267) l=12 f=--- val=1
    // AVP: Origin-State-Id(278) l=12 f=-M- val=1176020954
    public static byte[] CER_DUMP_ONLY_AVPS = [
            0x00, 0x00, 0x01, 0x08,
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
}
