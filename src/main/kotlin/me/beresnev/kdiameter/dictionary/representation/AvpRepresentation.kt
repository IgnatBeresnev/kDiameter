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

package me.beresnev.kdiameter.dictionary.representation

import me.beresnev.kdiameter.dictionary.representation.attributes.ModalAttribute

data class AvpRepresentation(
    val code: Long,
    val name: String,

    val vendor: VendorRepresentation,

    val mayEncrypt: Boolean = true, // "YES" or "NO"
    val mandatory: ModalAttribute = ModalAttribute.MAY,
    val protected: ModalAttribute = ModalAttribute.MAY,
    val vendorBit: ModalAttribute = ModalAttribute.MAY,

    val type: TypeRepresentation? = null,

    val groupedAvps: List<GroupedAvp> = emptyList(),
    val enumValues: List<Enum> = emptyList()
) {
    data class GroupedAvp(
        val name: String
    )

    data class Enum(
        val name: String,
        val code: Long
    )

    fun isGrouped(): Boolean {
        return groupedAvps.isNotEmpty()
    }

    fun isEnum(): Boolean {
        return enumValues.isNotEmpty()
    }
}

