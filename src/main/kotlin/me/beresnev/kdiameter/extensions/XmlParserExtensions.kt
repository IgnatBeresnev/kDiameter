/*
 * Copyright (C) 2019 Ignat Beresnev and individual contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.beresnev.kdiameter.extensions

import me.beresnev.kdiameter.dictionary.representation.attributes.ModalAttribute
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

fun NamedNodeMap.getAsModalVerbOption(name: String, defaultValue: ModalAttribute): ModalAttribute {
    val stringValue = this.getNullableString(name) ?: return defaultValue
    return when (stringValue.toLowerCase()) {
        "may" -> ModalAttribute.MAY
        "must" -> ModalAttribute.MUST
        "mustnot" -> ModalAttribute.MUST_NOT
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
