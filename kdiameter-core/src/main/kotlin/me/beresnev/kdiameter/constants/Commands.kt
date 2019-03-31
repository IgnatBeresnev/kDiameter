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
enum class Commands(
    val code: Long
) {
    ABORT_SESSION(274L),
    ACCOUNTING(271L),
    AGGREGATED_RUCI_REPORT(8388721L),
    ALERT_SERVICE_CENTRE(8388648L),
    BOOSTRAPPING_INFO(310L),
    CANCEL_VCSG_LOCATION(8388642L),
    CAPABILITIES_EXCHANGE(257L),
    CAPABILITIES_UPDATE(328L),
    CONFIGURATION_INFORMATION(8388718L),
    CONNECTION_MANAGEMENT(8388732L),
    DEVICE_WATCHDOG(280L),
    DISCONNECT_PEER(282L),
    DISTRIBUTED_CHARGING(8388632L),
    ERICSSON_BINDING_DATA(8388657L),
    ERICSSON_TRACE_REPORT(8388717L),
    ERICSSON_SL(8388633L),
    ERICSSON_SN(8388634L),
    GBAPUSH_INFO(312L),
    GET_GATEWAY(8388655L),
    IKEV2_SK(329L),
    MIP6(325L),
    MO_DATA(8388733L),
    MO_FORWARD_SHORT_MESSAGE(8388645L),
    MT_DATA(8388734L),
    MT_FORWARD_SHORT_MESSAGE(8388646L),
    MESSAGE_PROCESS(311L),
    MODIFY_UECONTEXT(8388722L),
    NAT_CONTROL(330L),
    NIDD_INFORMATION(8388726L),
    NSN_CANCEL_LOCATIONMS(8388650L),
    NSN_PROFILE_UPDATEMS(8388652L),
    NSN_PUSH_NOTIFICATIONMS(8388654L),
    NSN_SUBSCRIBE_NOTIFICATIONSMS(8388653L),
    NSN_USER_DATAMS(8388651L),
    NON_AGGREGATED_RUCI_REPORT(8388720L),
    POLICY_DATA(314L),
    POLICY_INSTALL(315L),
    QOS_AUTHORIZATION(326L),
    QOS_INSTALL(327L),
    RE_AUTH(258L),
    REPORT_SM_DELIVERY_STATUS(8388649L),
    REPORTING_INFORMATION(8388719L),
    SEND_ROUTING_INFO_FOR_SM(8388647L),
    SESSION_TERMINATION(275L),
    SPENDING_LIMIT(8388635L),
    SPENDING_STATUS_NOTIFICATION(8388636L),
    SUBSCRIPTION_INFORMATION_APPLICATION(8388631L),
    TDF_SESSION(8388637L),
    TGPP_AUTHENTICATION_INFORMATION(318L),
    TGPP_CANCEL_LOCATION(317L),
    TGPP_DELETE_SUBSCRIBER_DATA(320L),
    TGPP_DELIVERY_REPORT(8388644L),
    TGPP_DEVICE_ACTION(8388639L),
    TGPP_DEVICE_NOTIFICATION(8388640L),
    TGPP_DEVICE_TRIGGER(8388643L),
    TGPP_INSERT_SUBSCRIBER_DATA(319L),
    TGPP_LCS_ROUTING_INFO(8388622L),
    TGPP_LOCATION_REPORT(8388621L),
    TGPP_ME_IDENTITY_CHECK(324L),
    TGPP_NOTIFY(323L),
    TGPP_PROVIDE_LOCATION(8388620L),
    TGPP_PURGE_UE(321L),
    TGPP_RESET(322L),
    TGPP_SUBSCRIBER_INFORMATION(8388641L),
    TGPP_UPDATE_LOCATION(316L),
    TGPP_UPDATE_VCSG_LOCATION(8388638L),
    TRIGGER_ESTABLISHMENT(8388656L),
}