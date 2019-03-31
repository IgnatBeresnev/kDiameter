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
enum class Vendors(
    val vendorId: String,
    val code: Long
) {
    TGPP("TGPP", 10415L),
    TGPP_CX_DX("TGPPCX", 16777216L)
}