package me.beresnev.kdiameter.dictionary.representation

import me.beresnev.kdiameter.extensions.equalsIgnoreCase

data class TypeRepresentation(
    val typeName: String,
    val typeParent: TypeRepresentation?
) {
    fun isEnum(): Boolean {
        return "Enumerated".equalsIgnoreCase(typeName)
    }
}