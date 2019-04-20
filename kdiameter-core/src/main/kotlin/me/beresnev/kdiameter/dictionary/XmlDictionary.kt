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

package me.beresnev.kdiameter.dictionary

import me.beresnev.kdiameter.dictionary.representation.ApplicationRepresentation
import me.beresnev.kdiameter.dictionary.representation.AvpRepresentation
import me.beresnev.kdiameter.dictionary.representation.CommandRepresentation
import me.beresnev.kdiameter.dictionary.representation.TypeRepresentation
import me.beresnev.kdiameter.dictionary.representation.VendorRepresentation
import me.beresnev.kdiameter.dictionary.representation.attributes.ModalAttribute
import me.beresnev.kdiameter.extensions.data.equalsIgnoreCase
import me.beresnev.kdiameter.extensions.getAsBinaryOption
import me.beresnev.kdiameter.extensions.getAsModalVerbOption
import me.beresnev.kdiameter.extensions.getLong
import me.beresnev.kdiameter.extensions.getNullableString
import me.beresnev.kdiameter.extensions.getString
import me.beresnev.kdiameter.extensions.isEmpty
import me.beresnev.kdiameter.extensions.iterator
import me.beresnev.kdiameter.extensions.mapToList
import net.jcip.annotations.NotThreadSafe
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NamedNodeMap
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

@NotThreadSafe
open class XmlDictionary : Dictionary {

    companion object DefaultValues {
        const val DEF_VENDOR_ID = "None"
        const val DEF_VENDOR_CODE = 0L

        const val DEF_MAY_ENCRYPT = true
        val DEF_MANDATORY = ModalAttribute.MAY
        val DEF_PROTECTED = ModalAttribute.MAY
        val DEF_VENDOR_BIT = ModalAttribute.MAY
    }

    protected val types: MutableMap<String, TypeRepresentation> = HashMap()
    protected val applications: MutableMap<Long, ApplicationRepresentation> = HashMap() // key is id

    protected val vendors: MutableMap<String, VendorRepresentation> = HashMap() // key is vendor-id (string)
    protected val commands: MutableMap<Long, CommandRepresentation> = HashMap() // key is code

    protected val avpsByCodeAndVendorId: MutableMap<Long, MutableMap<Long, AvpRepresentation>> = HashMap()
    protected val avpsByNameAndVendorId: MutableMap<String, MutableMap<Long, AvpRepresentation>> = HashMap()

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
        // adding a default vendor to use it for all AVPs where vendor-id is not set
        vendors[DEF_VENDOR_ID] = VendorRepresentation(DEF_VENDOR_ID, DEF_VENDOR_CODE, DEF_VENDOR_ID)

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
            val castedAvpElement = avpElementNode as? Element ?: throw IllegalArgumentException()
            val avpAttributes = castedAvpElement.attributes

            val avpRepresentation = AvpRepresentation(
                code = avpAttributes.getLong("code"),
                name = avpAttributes.getString("name"),
                vendor = extractAvpVendor(avpAttributes),
                mayEncrypt = avpAttributes.getAsBinaryOption("may-encrypt", defaultValue = DEF_MAY_ENCRYPT),
                mandatory = avpAttributes.getAsModalVerbOption("mandatory", defaultValue = DEF_MANDATORY),
                protected = avpAttributes.getAsModalVerbOption("protected", defaultValue = DEF_PROTECTED),
                vendorBit = avpAttributes.getAsModalVerbOption("vendor-bit", defaultValue = DEF_VENDOR_BIT),
                type = extractAvpType(castedAvpElement),
                enumValues = extractEnumValues(castedAvpElement),
                groupedAvps = extractGroupedValues(castedAvpElement)
            )

            val vendorCode = avpRepresentation.vendor.code
            avpsByCodeAndVendorId.getOrPut(avpRepresentation.code) { HashMap() }[vendorCode] = avpRepresentation
            avpsByNameAndVendorId.getOrPut(avpRepresentation.name) { HashMap() }[vendorCode] = avpRepresentation
        }
    }

    /**
     * @return default vendor if vendor-id is not set
     */
    private fun extractAvpVendor(attributes: NamedNodeMap): VendorRepresentation {
        return attributes.getNullableString("vendor-id").let { id ->
            (if (id == null) vendors[DEF_VENDOR_ID] else vendors[id]) ?: throw IllegalStateException()
        }
    }

    private fun extractAvpType(avpElement: Element): TypeRepresentation? {
        val typeName = avpElement.getElementsByTagName("type").let { type ->
            type.item(0)?.attributes?.getString("type-name") ?: return null
        }

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

        return enumElements.mapToList {
            with(it.attributes) {
                AvpRepresentation.Enum(
                    name = this.getString("name"),
                    code = this.getLong("code")
                )
            }
        }
    }

    // <avp name="Proxy-Info" code="284" mandatory="must" may-encrypt="no" protected="mustnot" vendor-bit="mustnot">
    //     <grouped>
    //         <gavp name="Proxy-Host"/>
    //         <gavp name="Proxy-State"/>
    //     </grouped>
    // </avp>
    private fun extractGroupedValues(avpElement: Element): List<AvpRepresentation.GroupedAvp> {
        val groupedElement = avpElement.getElementsByTagName("grouped").let { gavp ->
            if (gavp.isEmpty()) {
                return emptyList()
            } else if (gavp.length != 1) {
                throw IllegalStateException("Expected one <grouped> within <avp>, got: ${gavp.length}")
            }

            gavp.item(0) as? Element ?: throw IllegalArgumentException()
        }

        val groupedAvps = groupedElement.getElementsByTagName("gavp")
        if (groupedAvps.isEmpty()) return emptyList()

        return groupedAvps.mapToList {
            AvpRepresentation.GroupedAvp(it.attributes.getString("name"))
        }
    }

    private fun executeOnAllNamedElementsAttributes(
        doc: Document,
        elementName: String,
        attributesExecutable: (attributes: NamedNodeMap) -> Unit
    ) {
        doc.getElementsByTagName(elementName).iterator().forEachRemaining {
            attributesExecutable(it.attributes)
        }
    }

    override fun getType(name: String) = types[name]

    override fun getApplication(id: Long) = applications[id]

    override fun getVendor(vendorId: String) = vendors[vendorId]

    override fun getCommand(code: Long) = commands[code]

    override fun getAvp(code: Long, vendorId: Long) = avpsByCodeAndVendorId[code]?.get(vendorId)

    override fun getAvp(name: String, vendorId: Long) = avpsByNameAndVendorId[name]?.get(vendorId)
}