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

