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

package me.beresnev.kdiameter.constants

/**
 * Parsed dictionary.xml which can be found in test resources
 * under /resources/dictionary/complete_dictionary.xml
 *
 * Use [me.beresnev.kdiameter.dictionary.Dictionary] if you
 * need to find a specific value instead of searching this enum
 *
 * This is made primarily for easier construction of Avp
 * objects for outgoing messages
 *
 * @date 2019-03-31
 */
enum class Applications(
    val code: Long
) {
    ADMI_MESSAGING_INTERFACE_APPLICATION(16777276L),
    ADMI_NOTIFICATION_APPLICATION(16777275L),
    CAMA(16777260L),
    CARA(16777259L),
    CAMIANT_DRMA(16777294L),
    CLOUDMARK_DIAMETER_INTERFACE(16777293L),
    COVERGENCE_SPECIFIC_SIP_ROUTING(16777242L),
    DIAMETER_BASE_ACCOUNTING(3L), // http://tools.ietf.org/html/rfc6733
    DIAMETER_CAPABILITIES_UPDATE(10L), // http://tools.ietf.org/html/rfc6737
    DIAMETER_COMMON_MESSAGES(0L), // http://tools.ietf.org/html/rfc6733
    DIAMETER_ERP(13L), // http://tools.ietf.org/html/rfc6942
    DIAMETER_IKE_SK_IKESK(11L), // http://tools.ietf.org/html/rfc6738
    DIAMETER_NAT_CONTROL_APPLICATION(12L), // http://tools.ietf.org/html/rfc6736
    DIAMETER_QOS_APPLICATION(9L), // http://tools.ietf.org/html/rfc5866
    ETSI_GOCAP(16777254L), // http://www.etsi.org/deliver/etsi_es%5C283000_283099%5C28303902%5C03.01.01_60%5Ces_28303902v030101p.pdf
    ETSI_RE(16777253L), // http://www.etsi.org/deliver/etsi_ts%5C183000_183099%5C183060%5C03.01.01_60%5Cts_183060v030101p.pdf
    ETSI_RR_DELEGATED_MODEL(16777279L), // http://www.etsi.org/deliver/etsi_ts%5C183000_183099%5C183071%5C03.01.01_60%5Cts_183071v030101p.pdf
    ETSI_RR_REQUEST_MODEL(16777278L), // http://www.etsi.org/deliver/etsi_ts%5C183000_183099%5C183071%5C03.01.01_60%5Cts_183071v030101p.pdf
    ETSI_A4(16777257L), // http://www.etsi.org/deliver/etsi_ts%5C183000_183099%5C183066%5C02.01.01_60%5Cts_183066v020101p.pdf
    FEMTOCELL_EXTENSION_TO_DIAMETER_EAP_APPLICATION(16777261L),
    GATEWAY_LOCATION_APPLICATION(16777321L),
    HP_DTD(16777305L),
    HOST_OBSERVER(16777339L),
    ITU_T_M13(16777307L), // http://www.itu.int/ITU-T/recommendations/rec.aspx?rec=11712
    ITU_T_M9(16777306L), // http://www.itu.int/ITU-T/recommendations/rec.aspx?rec=11570
    ITU_T_NC(16777325L), // http://www.itu.int/ITU-T/recommendations/rec.aspx?rec=12217
    ITU_T_NE(16777326L),
    ITU_T_NG(16777263L),
    ITU_T_RD(16777274L), // http://www.itu.int/ITU-T/recommendations/rec.aspx?rec=10224
    ITU_T_RI(16777271L), // http://www.itu.int/ITU-T/recommendations/rec.aspx?rec=9881
    ITU_T_RS(16777235L), // http://www.itu.int/ITU-T/recommendations/rec.aspx?rec=11971
    ITU_T_RT(16777258L), // http://www.itu.int/ITU-T/recommendations/rec.aspx?rec=11450
    ITU_T_RU(16777262L),
    ITU_T_RW(16777256L), // https://tools.ietf.org/html/rfc5431
    ITU_T_S_TC1(16777245L), // http://www.itu.int/ITU-T/recommendations/rec.aspx?rec=9340
    INTRADO_SLG(16777314L),
    JUNIPER_CLUSTER(16777239L),
    JUNIPER_DOMAIN_POLICY(16777338L),
    JUNIPER_JGX(16777273L),
    JUNIPER_POLICY_CONTROL_AAA(16777240L),
    JUNIPER_POLICY_CONTROL_JSRC(16777244L),
    JUNIPER_EXAMPLE(16777270L),
    JUNIPER_SESSIONS_RECOVERY(16777296L),
    MAGIC_CLIENT_INTERFACE_PROTOCOL_CIP(16777324L),
    PETER_SERVICE_VSI(16777277L),
    PI_TGPP2_DIAMETER_APPLICATION(16777298L), // http://www.3gpp2.org/Public_html/specs/X.S0057-A%20v2.0_20121018.pdf
    PILTE_INTERWORKING_DIAMETER_APPLICATION(16777295L), // http://www.3gpp2.org/Public_html/specs/X.S0057-0%20v3.0%20(clean)%20E-UTRAN-eHRPD%20Interworking.pdf
    POLICY_PROCESSING(16777243L),
    RELAY(4294967295L), // http://tools.ietf.org/html/rfc6733
    RIVADA_XD(16777329L),
    RIVADA_XF_1(16777332L),
    RIVADA_XF_2(16777333L),
    RIVADA_XH(16777331L),
    RIVADA_XM(16777330L),
    RIVADA_XP(16777334L),
    S6B_APPLICATION_ONE_AAA(16777999L),
    SANDVINE_RF(16777299L),
    SUBSCRIPTION_INFORMATION_APPLICATION(16777300L),
    TGPP_GX_1(16777224L), // http://www.3gpp.org/ftp/Specs/html-info/29210.htm
    TGPP_GX_2(16777238L), // http://www.3GPP.org/ftp/Specs/html-info/29210.htm
    TGPP_GX_OVER_GY(16777225L), // http://www.3GPP.org/ftp/Specs/html-info/29210.htm
    TGPP_GXX(16777266L), // http://www.3gpp.org/ftp/Specs/html-info/29212.htm
    TGPP_MB2_C(16777335L), // http://www.3gpp.org/ftp/Specs/html-info/29468.htm
    TGPP_MM10(16777226L), // http://www.3GPP.org/ftp/Specs/html-info/29140.htm
    TGPP_NP(16777342L), // http://www.3gpp.org/ftp/Specs/html-info/29217.htm
    TGPP_PC2(16777337L), // http://www.3gpp.org/ftp/Specs/html-info/29343.htm
    TGPP_PC6_PC7(16777340L), // http://www.3gpp.org/ftp/Specs/html-info/29345.htm
    TGPP_PR(16777230L), // http://www.3gpp.org/ftp/Specs/html-info/29234.htm
    TGPP_RE_RF(16777218L), // http://www.3GPP.org/ftp/Specs/html-info/32296.htm
    TGPP_RX_RELEASE_6(16777229L), // http://www.3GPP.org/ftp/Specs/html-info/29211.htm
    TGPP_S13_S13(16777252L), // http://tools.ietf.org/html/rfc5516
    TGPP_S15(16777318L), // http://www.3gpp.org/ftp/Specs/html-info/29212.htm
    TGPP_S6A_S6D(16777251L), // http://tools.ietf.org/html/rfc5516
    TGPP_S6B(16777272L), // http://www.3gpp.org/ftp/Specs/html-info/29273.htm
    TGPP_S6M(16777310L), // http://www.3gpp.org/ftp/Specs/html-info/29336.htm
    TGPP_S6T(16777345L), // http://www.3gpp.org/ftp/Specs/archive/29_series/29.336/29336-e20.zip
    TGPP_S7A(16777308L), // http://www.3gpp.org/ftp/Specs/html-info/29272.htm
    TGPP_S9A_1(16777319L), // http://www.3gpp.org/ftp/Specs/html-info/29215.htm
    TGPP_S9A_2(16777320L), // http://www.3gpp.org/ftp/Specs/html-info/29215.htm
    TGPP_SGD(16777313L), // http://www.3gpp.org/ftp/Specs/html-info/29338.htm
    TGPP_SLG(16777255L), // http://www.3gpp.org/ftp/Specs/html-info/29172.htm
    TGPP_SLH(16777291L), // http://www.3gpp.org/ftp/Specs/html-info/29173.htm
    TGPP_STA(16777250L), // http://www.3gpp.org/ftp/Specs/html-info/29273.htm
    TGPP_SWM(16777264L), // http://www.3gpp.org/ftp/Specs/html-info/29273.htm
    TGPP_SWX(16777265L), // http://www.3gpp.org/ftp/Specs/html-info/29273.htm
    TGPP_SD(16777303L), // http://www.3gpp.org/ftp/Specs/html-info/29212.htm
    TGPP_SY(16777302L), // http://www.3gpp.org/ftp/Specs/html-info/29219.htm
    TGPP_T4(16777311L), // http://www.3gpp.org/ftp/Specs/html-info/29337.htm
    TGPP_TSP(16777309L), // http://www.3gpp.org/ftp/Specs/html-info/29368.htm
    TGPP_WX(16777219L), // http://www.3GPP.org/ftp/Specs/html-info/29234.htm
    TGPP_ZH(16777221L), // http://www.3GPP.org/ftp/Specs/html-info/29109.htm
    TGPP_ZN(16777220L), // http://www.3GPP.org/ftp/Specs/html-info/29109.htm
    TGPP_ZPN(16777268L), // http://www.3gpp.org/ftp/Specs/html-info/29109.htm
    TGPP2_CAN_ACCESS_AUTHENTICATION_AND_AUTHORIZATION(16777247L), // http://www.3gpp2.org/Public_html/specs/X.S0054-100-0_v2.0_080909.pdf
    TGPP2_WLAN_INTERWORKING_ACCESS_AUTHENTICATION_AND_AUTHORIZATION(16777248L), // http://www.3gpp2.org/Public_html/specs/X.S0028-200-A_v1.0_080625.pdf
    TGPP2_WLAN_INTERWORKING_ACCOUNTING(16777249L), // http://www.3gpp2.org/Public_html/specs/X.S0028-200-A_v1.0_080625.pdf
    VEDICIS_LIVEPROXY(16777297L),
    VERIZON_SESSION_RECOVERY(16777322L),
    VERIZON_FEMTO_LOC(16777316L),
    IPTEGO_USPI(16777241L),
}