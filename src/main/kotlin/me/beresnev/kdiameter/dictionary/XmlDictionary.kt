package me.beresnev.kdiameter.dictionary

import me.beresnev.kdiameter.dictionary.representation.ApplicationRepresentation
import me.beresnev.kdiameter.dictionary.representation.AvpRepresentation
import me.beresnev.kdiameter.dictionary.representation.CommandRepresentation
import me.beresnev.kdiameter.dictionary.representation.TypeRepresentation
import me.beresnev.kdiameter.dictionary.representation.VendorRepresentation
import me.beresnev.kdiameter.extensions.getNullableValue
import me.beresnev.kdiameter.extensions.getValue
import me.beresnev.kdiameter.extensions.isEmpty
import me.beresnev.kdiameter.extensions.iterator
import net.jcip.annotations.NotThreadSafe
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NamedNodeMap
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

@NotThreadSafe
class XmlDictionary {
    val types: MutableMap<String, TypeRepresentation> = mutableMapOf()
    val applications: MutableMap<Long, ApplicationRepresentation> = mutableMapOf() // key is id

    // key is vendor-id (string), not code (int)
    val vendors: MutableMap<String, VendorRepresentation> = mutableMapOf()
    val commands: MutableMap<Long, CommandRepresentation> = mutableMapOf() // key is code

    val avps: MutableMap<Long, MutableList<AvpRepresentation>> = mutableMapOf() // TODO [beresnev] make avpsByName

    fun parse(xmlFile: File) {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()

        val doc = dBuilder.parse(xmlFile)
        doc.documentElement.normalize() // https://stackoverflow.com/a/13787629/6395606

        parseTypes(doc)
        parseApplications(doc)
        parseVendors(doc)
        parseCommands(doc)
        parseAvps(doc)
    }

    // TODO [beresnev] anonymous classes to lambdas?
    private fun parseTypes(doc: Document) {
        executeOnAllNamedElements(doc, "typedefn", object : AttributesExecutable {
            override fun executeOnSingleElementAttributes(attributes: NamedNodeMap) {
                val typeName = attributes.getValue("type-name")
                val typeParent = attributes.getNullableValue("type-parent")

                types[typeName] = TypeRepresentation(
                    typeName,
                    typeParent = if (typeParent != null) types[typeParent] else null
                )
            }
        })
    }

    private fun parseApplications(doc: Document) {
        executeOnAllNamedElements(doc, "application", object : AttributesExecutable {
            override fun executeOnSingleElementAttributes(attributes: NamedNodeMap) {
                val id = attributes.getValue("id").toLong()
                val uri = attributes.getValue("uri")

                applications[id] = ApplicationRepresentation(
                    id = id,
                    name = attributes.getValue("name"),
                    uri = if ("none".equals(uri, ignoreCase = true)) null else uri
                )
            }
        })
    }

    private fun parseVendors(doc: Document) {
        executeOnAllNamedElements(doc, "vendor", object : AttributesExecutable {
            override fun executeOnSingleElementAttributes(attributes: NamedNodeMap) {
                val vendorId = attributes.getValue("vendor-id")
                vendors[vendorId] = VendorRepresentation(
                    vendorId = vendorId,
                    code = attributes.getValue("code").toLong(),
                    name = attributes.getValue("name")
                )
            }
        })
    }

    /**
     * Has to be parsed after initializing vendors map
     * @see XmlDictionary.vendors
     */
    private fun parseCommands(doc: Document) {
        executeOnAllNamedElements(doc, "command", object : AttributesExecutable {
            override fun executeOnSingleElementAttributes(attributes: NamedNodeMap) {
                val code = attributes.getValue("code").toLong()
                val vendorId = attributes.getValue("vendor-id")
                commands[code] = CommandRepresentation(
                    code = code,
                    name = attributes.getValue("name"),
                    vendor = if ("none".equals(vendorId, ignoreCase = true)) null else vendors[vendorId]
                )
            }
        })
    }

    private fun parseAvps(doc: Document) {
        val avpElements = doc.getElementsByTagName("avp")
        for (avpElementNode in avpElements) {
            val castedAvpElement = (avpElementNode as Element)
            val avpAttributes = castedAvpElement.attributes

            val code = avpAttributes.getValue("code").toLong()
            avps.getOrPut(code) { mutableListOf() }
                .add(
                    AvpRepresentation(
                        code = code,
                        name = avpAttributes.getValue("name"),
                        vendor = extractAvpVendor(avpAttributes),
                        mandatory = avpAttributes.getNullableValue("mandatory"),
                        protected = avpAttributes.getNullableValue("protected"),
                        mayEncrypt = avpAttributes.getNullableValue("may-encrypt"),
                        vendorBit = avpAttributes.getNullableValue("vendor-bit"),
                        type = extractAvpType(castedAvpElement),
                        enumValues = extractEnumValues(castedAvpElement),
                        groupedAvps = extractGroupedValues(castedAvpElement)
                    )
                )
        }
    }

    private fun extractAvpVendor(attributes: NamedNodeMap): VendorRepresentation? {
        val vendorId = attributes.getNullableValue("vendor-id") ?: return null
        return vendors[vendorId]
    }

    private fun extractAvpType(avpElement: Element): TypeRepresentation? {
        val typeElement = avpElement.getElementsByTagName("type")

        val typeName = typeElement.item(0)?.attributes?.getValue("type-name") ?: return null
        return types[typeName]
    }

    private fun extractEnumValues(avpElement: Element): List<AvpRepresentation.Enum> {
        val enumElements = avpElement.getElementsByTagName("enum")
        if (enumElements.isEmpty()) return emptyList()

        val enumValues = mutableListOf<AvpRepresentation.Enum>()
        for (enumElement in enumElements) {
            val enumAttributes = enumElement.attributes
            enumValues.add(
                AvpRepresentation.Enum(
                    name = enumAttributes.getValue("name"),
                    code = enumAttributes.getValue("code").toLong()
                )
            )
        }
        return enumValues
    }

    private fun extractGroupedValues(avpElement: Element): List<AvpRepresentation.GroupedAvp> {
        val groupedElements = avpElement.getElementsByTagName("grouped")
        if (groupedElements.isEmpty()) {
            return emptyList()
        } else if (groupedElements.length != 1) {
            // TODO [beresnev] log
        }

        val groupedElement = (groupedElements.item(0) as Element)
        val groupedAvps = groupedElement.getElementsByTagName("gavp")
        if (groupedAvps.isEmpty()) return emptyList()

        val groupedValues = mutableListOf<AvpRepresentation.GroupedAvp>()
        for (avp in groupedAvps) {
            groupedValues.add(
                AvpRepresentation.GroupedAvp(name = avp.attributes.getValue("name"))
            )
        }
        return groupedValues
    }

    private fun executeOnAllNamedElements(
        doc: Document,
        elementName: String,
        attributesExecutable: AttributesExecutable
    ) {
        val elementsByTagName = doc.getElementsByTagName(elementName)
        elementsByTagName.iterator().forEachRemaining {
            attributesExecutable.executeOnSingleElementAttributes(it.attributes)
        }
    }

    private interface AttributesExecutable {
        fun executeOnSingleElementAttributes(attributes: NamedNodeMap)
    }
}