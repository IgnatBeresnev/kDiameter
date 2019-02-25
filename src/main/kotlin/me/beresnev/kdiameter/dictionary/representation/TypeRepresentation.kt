package me.beresnev.kdiameter.dictionary.representation

data class TypeRepresentation(
    val typeName: String,
    val typeParent: TypeRepresentation?
) {
    fun isEnum(): Boolean {
        return "Enumerated".equals(typeName, ignoreCase = true)
    }
}