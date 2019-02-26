package me.beresnev.kdiameter.dictionary

import me.beresnev.kdiameter.dictionary.representation.ApplicationRepresentation
import me.beresnev.kdiameter.dictionary.representation.AvpRepresentation
import me.beresnev.kdiameter.dictionary.representation.CommandRepresentation
import me.beresnev.kdiameter.dictionary.representation.TypeRepresentation
import me.beresnev.kdiameter.dictionary.representation.VendorRepresentation
import me.beresnev.kdiameter.dictionary.representation.attributes.ModalVerbOption
import me.beresnev.kdiameter.extensions.equalsIgnoreCase
import me.beresnev.kdiameter.extensions.getAsBinaryOption
import me.beresnev.kdiameter.extensions.getAsModalVerbOption
import me.beresnev.kdiameter.extensions.getLong
import me.beresnev.kdiameter.extensions.getNullableString
import me.beresnev.kdiameter.extensions.getString
import me.beresnev.kdiameter.extensions.isEmpty
import me.beresnev.kdiameter.extensions.iterator
import net.jcip.annotations.NotThreadSafe
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NamedNodeMap
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

@NotThreadSafe
open class XmlDictionary {

    protected val types: MutableMap<String, TypeRepresentation> = HashMap()
    protected val applications: MutableMap<Long, ApplicationRepresentation> = HashMap() // key is id

    protected val vendors: MutableMap<String, VendorRepresentation> = HashMap() // key is vendor-id (string)
    protected val commands: MutableMap<Long, CommandRepresentation> = HashMap() // key is code

    protected val avpsByCode: MutableMap<Long, MutableMap<Long, AvpRepresentation>> = HashMap()
    protected val avpsByName: MutableMap<String, MutableMap<Long, AvpRepresentation>> = HashMap()

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

    // <typedefn type-name="OctetString"/>
    // <typedefn type-name="UTF8String" type-parent="OctetString"/>
    private fun parseTypes(doc: Document) {
        executeOnAllNamedElementsAttributes(doc, "typedefn") {
            val typeName = it.getString("type-name")
            val typeParent = it.getNullableString("type-parent")

            types[typeName] = TypeRepresentation(
                typeName,
                typeParent = if (typeParent != null) types[typeParent] else null
            )
        }
    }

    // <application id="9" name="Diameter QoS application" uri="http://tools.ietf.org/html/rfc5866"/>
    // <application id="16777239" name="Juniper Cluster" uri="none"/>
    private fun parseApplications(doc: Document) {
        executeOnAllNamedElementsAttributes(doc, "application") {
            val id = it.getLong("id")
            val uri = it.getString("uri")

            applications[id] = ApplicationRepresentation(
                id = id,
                name = it.getString("name"),
                uri = if ("none".equalsIgnoreCase(uri)) null else uri
            )
        }
    }

    // <vendor vendor-id="Lucent" code="1751" name="Lucent Technologies"/>
    private fun parseVendors(doc: Document) {
        vendors["None"] = VendorRepresentation("None", 0L, "None")
        executeOnAllNamedElementsAttributes(doc, "vendor") {
            val vendorId = it.getString("vendor-id")
            vendors[vendorId] = VendorRepresentation(
                vendorId = vendorId,
                code = it.getLong("code"),
                name = it.getString("name")
            )
        }
    }

    // <command name="QoS-Install" code="327" vendor-id="None"/>
    private fun parseCommands(doc: Document) {
        if (vendors.isEmpty()) {
            throw IllegalStateException("Vendors have to be parsed first for linking")
        }

        executeOnAllNamedElementsAttributes(doc, "command") {
            val code = it.getLong("code")
            val vendorId = it.getString("vendor-id")
            commands[code] = CommandRepresentation(
                code = code,
                name = it.getString("name"),
                vendor = vendors[vendorId] ?: throw IllegalArgumentException("Unknown vendor id $vendorId")
            )
        }
    }

    // <avp name="NAS-Port" code="5" mandatory="must" may-encrypt="yes" protected="may" vendor-bit="mustnot">
    //     <type type-name="Unsigned32"/>
    // </avp>
    // <avp name="Service-Type" code="6" mandatory="must" may-encrypt="yes" protected="may" vendor-bit="mustnot">
    //     <type type-name="Enumerated"/>
    //     <enum name="Unknown" code="0"/>
    //     <enum name="Login" code="1"/>
    // </avp>
    // <avp name="Proxy-Info" code="284" mandatory="must" may-encrypt="no" protected="mustnot" vendor-bit="mustnot">
    //     <grouped>
    //         <gavp name="Proxy-Host"/>
    //         <gavp name="Proxy-State"/>
    //     </grouped>
    // </avp>
    private fun parseAvps(doc: Document) {
        val avpElements = doc.getElementsByTagName("avp")
        for (avpElementNode in avpElements) {
            val castedAvpElement = (avpElementNode as Element)
            val avpAttributes = castedAvpElement.attributes

            val avpRepresentation = AvpRepresentation(
                code = avpAttributes.getLong("code"),
                name = avpAttributes.getString("name"),
                vendor = extractAvpVendor(avpAttributes),
                mayEncrypt = avpAttributes.getAsBinaryOption("may-encrypt", defaultValue = true),
                mandatory = avpAttributes.getAsModalVerbOption("mandatory", defaultValue = ModalVerbOption.MAY),
                protected = avpAttributes.getAsModalVerbOption("protected", defaultValue = ModalVerbOption.MAY),
                vendorBit = avpAttributes.getAsModalVerbOption("vendor-bit", defaultValue = ModalVerbOption.MAY),
                type = extractAvpType(castedAvpElement),
                enumValues = extractEnumValues(castedAvpElement),
                groupedAvps = extractGroupedValues(castedAvpElement)
            )

            val vendorCode = avpRepresentation.vendor.code
            avpsByCode.getOrPut(avpRepresentation.code) { HashMap() }[vendorCode] = avpRepresentation
            avpsByName.getOrPut(avpRepresentation.name) { HashMap() }[vendorCode] = avpRepresentation
        }
    }

    /**
     * @return vendor with vendor-id == "None" if no vendor-id set
     */
    private fun extractAvpVendor(attributes: NamedNodeMap): VendorRepresentation {
        val vendorId = attributes.getNullableString("vendor-id")
        return (if (vendorId == null) vendors["None"] else vendors[vendorId]) ?: throw IllegalStateException()
    }

    private fun extractAvpType(avpElement: Element): TypeRepresentation? {
        val typeElement = avpElement.getElementsByTagName("type")

        val typeName = typeElement.item(0)?.attributes?.getString("type-name") ?: return null
        return types[typeName]
    }

    // <avp name="Service-Type" code="6" mandatory="must" may-encrypt="yes" protected="may" vendor-bit="mustnot">
    //     <type type-name="Enumerated"/>
    //     <enum name="Unknown" code="0"/>
    //     <enum name="Login" code="1"/>
    // </avp>
    private fun extractEnumValues(avpElement: Element): List<AvpRepresentation.Enum> {
        val enumElements = avpElement.getElementsByTagName("enum")
        if (enumElements.isEmpty()) return emptyList()

        val enumValues = ArrayList<AvpRepresentation.Enum>()
        for (enumElement in enumElements) { // cannot be made into stream since not Iterable<>
            val enumAttributes = enumElement.attributes
            enumValues.add(
                AvpRepresentation.Enum(
                    name = enumAttributes.getString("name"),
                    code = enumAttributes.getLong("code")
                )
            )
        }
        return enumValues
    }

    // <avp name="Proxy-Info" code="284" mandatory="must" may-encrypt="no" protected="mustnot" vendor-bit="mustnot">
    //     <grouped>
    //         <gavp name="Proxy-Host"/>
    //         <gavp name="Proxy-State"/>
    //     </grouped>
    // </avp>
    private fun extractGroupedValues(avpElement: Element): List<AvpRepresentation.GroupedAvp> {
        val groupedElements = avpElement.getElementsByTagName("grouped")
        if (groupedElements.isEmpty()) {
            return emptyList()
        } else if (groupedElements.length != 1) {
            throw IllegalStateException("Expected one <grouped> within <avp>, got: ${groupedElements.length}")
        }

        val groupedElement = (groupedElements.item(0) as Element)
        val groupedAvps = groupedElement.getElementsByTagName("gavp")
        if (groupedAvps.isEmpty()) return emptyList()

        val groupedValues = ArrayList<AvpRepresentation.GroupedAvp>()
        for (avp in groupedAvps) {
            groupedValues.add(AvpRepresentation.GroupedAvp(avp.attributes.getString("name")))
        }
        return groupedValues
    }

    private fun executeOnAllNamedElementsAttributes(
        doc: Document,
        elementName: String,
        attributesExecutable: (attributes: NamedNodeMap) -> Unit
    ) {
        val elementsByTagName = doc.getElementsByTagName(elementName)
        elementsByTagName.iterator().forEachRemaining {
            attributesExecutable.invoke(it.attributes)
        }
    }
}