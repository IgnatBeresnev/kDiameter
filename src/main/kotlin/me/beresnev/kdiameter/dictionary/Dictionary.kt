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