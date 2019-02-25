package me.beresnev.kdiameter.dictionary.representation

data class ApplicationRepresentation(
    val id: Long,
    val name: String,
    val uri: String? = null // "none" is equal to null
)