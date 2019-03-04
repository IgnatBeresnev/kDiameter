package me.beresnev.kdiameter.extensions

// for java compatibility we have to use long instead of UInt
// since UInt maps to Java's int and it can overflow
fun Int.toUnsignedLong() = this.toLong() and 0xffffffffL