package me.beresnev.kdiameter.extensions

import net.jcip.annotations.NotThreadSafe
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.NodeList

fun NamedNodeMap.getValue(name: String): String {
    return this.getNamedItem(name).nodeValue
}

fun NamedNodeMap.getNullableValue(name: String): String? {
    return this.getNamedItem(name)?.nodeValue
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
