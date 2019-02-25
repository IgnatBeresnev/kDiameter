package me.beresnev.kdiameter.dictionary.representation

data class CommandRepresentation(
    val name: String,
    val code: Long,
    val vendor: VendorRepresentation? = null // "none" is equal to none
)