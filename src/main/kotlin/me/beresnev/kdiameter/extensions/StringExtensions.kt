package me.beresnev.kdiameter.extensions

fun String.equalsIgnoreCase(other: String): Boolean {
    return this.equals(other, ignoreCase = true)
}