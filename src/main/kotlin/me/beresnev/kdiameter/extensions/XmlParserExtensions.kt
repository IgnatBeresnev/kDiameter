package me.beresnev.kdiameter.extensions

import me.beresnev.kdiameter.dictionary.representation.attributes.ModalVerbOption
import net.jcip.annotations.NotThreadSafe
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.NodeList

fun NamedNodeMap.getLong(name: String): Long {
    return this.getNamedItem(name).nodeValue.toLong()
}

fun NamedNodeMap.getString(name: String): String {
    return this.getNamedItem(name).nodeValue
}

fun NamedNodeMap.getNullableString(name: String): String? {
    return this.getNamedItem(name)?.nodeValue
}

fun NamedNodeMap.getAsModalVerbOption(name: String, defaultValue: ModalVerbOption): ModalVerbOption {
    val stringValue = this.getNullableString(name) ?: return defaultValue
    return when (stringValue.toLowerCase()) {
        "may" -> ModalVerbOption.MAY
        "must" -> ModalVerbOption.MUST
        "mustnot" -> ModalVerbOption.MUST_NOT
        else -> throw IllegalArgumentException("${stringValue} does not match any option")
    }
}

fun NamedNodeMap.getAsBinaryOption(name: String, defaultValue: Boolean): Boolean {
    val stringValue = this.getNullableString(name) ?: return defaultValue
    return when (stringValue.toLowerCase()) {
        "yes" -> true
        "no" -> false
        else -> throw IllegalArgumentException("${stringValue} does not match any option")
    }
}

fun NodeList.isEmpty(): Boolean {
    return this.length == 0
}

operator fun NodeList.iterator(): Iterator<Node> {
    return NodeIterator(this)
}

@NotThreadSafe
class NodeIterator(private val nodeList: NodeList) : Iterator<Node> {
    private var currentIndex = 0
    private val length = nodeList.length

    override fun next(): Node {
        return nodeList.item(currentIndex++)
    }

    override fun hasNext(): Boolean {
        return currentIndex < length
    }
}
