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

package me.beresnev.kdiameter.dictionary

import me.beresnev.kdiameter.dictionary.representation.ApplicationRepresentation
import me.beresnev.kdiameter.dictionary.representation.AvpRepresentation
import me.beresnev.kdiameter.dictionary.representation.CommandRepresentation
import me.beresnev.kdiameter.dictionary.representation.TypeRepresentation
import me.beresnev.kdiameter.dictionary.representation.VendorRepresentation
import net.jcip.annotations.NotThreadSafe

@NotThreadSafe
interface Dictionary {

    fun getType(name: String): TypeRepresentation?

    fun getApplication(id: Long): ApplicationRepresentation?

    fun getVendor(vendorId: String): VendorRepresentation?

    fun getCommand(code: Long): CommandRepresentation?

    fun getAvp(code: Long): AvpRepresentation? {
        return getAvp(code, 0L)
    }

    fun getAvp(code: Long, vendorId: Long): AvpRepresentation?

    fun getAvp(name: String): AvpRepresentation? {
        return getAvp(name, 0L)
    }

    fun getAvp(name: String, vendorId: Long): AvpRepresentation?
}