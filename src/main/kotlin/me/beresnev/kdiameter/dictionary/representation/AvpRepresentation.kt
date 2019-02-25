package me.beresnev.kdiameter.dictionary.representation

data class AvpRepresentation(
    val code: Long,
    val name: String,
    val vendor: VendorRepresentation?,

    val mandatory: String? = null,
    val protected: String? = null,
    val mayEncrypt: String? = null,
    val vendorBit: String? = null,

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

