package me.beresnev.kdiameter.dictionary


import me.beresnev.kdiameter.dictionary.representation.attributes.ModalAttribute
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

class XmlDictionarySpec extends Specification {

    private static final def DEF_MAY_ENCRYPT = XmlDictionary.DEF_MAY_ENCRYPT
    private static final def DEF_MANDATORY = XmlDictionary.DEF_MANDATORY
    private static final def DEF_PROTECTED = XmlDictionary.DEF_PROTECTED
    private static final def DEF_VENDOR_BIT = XmlDictionary.DEF_VENDOR_BIT

    def "should parse full dictionary without exceptions and with correct count"() {
        given:
        def typeDefXml = new File(getDictionaryTestFile("complete_dictionary"))
        def dictionary = new XmlDictionary()

        when:
        dictionary.parse(typeDefXml)

        then:
        dictionary.types.size() == 18
        dictionary.applications.size() == 100

        dictionary.commands.size() == 67
        dictionary.vendors.size() == 12

        // there are two AVPs with code 300 but with different
        // name and vendor-id, so it is put under one AVP code
        def avpsByCode = dictionary.getAvpsByCodeAndVendorId()
        avpsByCode.size() == 1279
        avpsByCode.values().size() == 1279
        avpsByCode[300L].size() == 2

        // there are two AVPs with name "Bandwidth" but with
        // different codes and vendor id, so it's put under one name
        def avpsByName = dictionary.getAvpsByNameAndVendorId()
        avpsByName.size() == 1279
        avpsByName.values().size() == 1279
        avpsByName["Bandwidth"].size() == 2
    }

    @Unroll
    def "should correctly parse all type definitions with and without parent"() {
        given:
        def typeDefXml = new File(getDictionaryTestFile("typedef"))
        def dictionary = new XmlDictionary()

        when:
        dictionary.parse(typeDefXml)

        then:
        dictionary.types.size() == 4

        expect:
        dictionary.getType(typeName).typeParent == dictionary.types[typeParentName] // nullable parent

        where:
        typeName           | typeParentName
        "OctetString"      | null
        "UTF8String"       | "OctetString"
        "IPAddress"        | "OctetString"
        "DiameterIdentity" | "OctetString"
    }

    @Unroll
    def "should parse application definitions with or without uri"() {
        given:
        def typeDefXml = new File(getDictionaryTestFile("application"))
        def dictionary = new XmlDictionary()

        when:
        dictionary.parse(typeDefXml)

        then:
        dictionary.applications.size() == 4

        expect:
        def application = dictionary.getApplication(appId)
        application.id == appId
        application.name == appName
        application.uri == appUri

        where:
        appId     | appName                    | appUri
        0L        | "Diameter Common Messages" | "http://tools.ietf.org/html/rfc6733"
        3L        | "Diameter Base Accounting" | "http://tools.ietf.org/html/rfc6733"
        9L        | "Diameter QoS application" | "http://tools.ietf.org/html/rfc5866"
        16777239L | "Juniper Cluster"          | null
    }

    @Unroll
    def "should parse vendors"() {
        given:
        def typeDefXml = new File(getDictionaryTestFile("vendor"))
        def dictionary = new XmlDictionary()

        when:
        dictionary.parse(typeDefXml)

        then:
        dictionary.vendors.size() == 4

        expect:
        def vendor = dictionary.getVendor(vendorId)
        vendor.vendorId == vendorId
        vendor.code == code
        vendor.name == name

        where:
        vendorId | code | name
        "None"   | 0    | "None"
        "Merit"  | 61   | "Merit Networks"
        "USR"    | 429  | "US Robotics Corp."
        "Lucent" | 1751 | "Lucent Technologies"
    }

    @Unroll
    def "should parse commands"() {
        given:
        def typeDefXml = new File(getDictionaryTestFile("command"))
        def dictionary = new XmlDictionary()

        when:
        dictionary.parse(typeDefXml)

        then:
        dictionary.commands.size() == 3

        expect:
        def command = dictionary.getCommand(code)
        command.code == code
        command.name == name
        command.vendor.vendorId == vendorId

        where:
        code | name                    | vendorId
        257L | "Capabilities-Exchange" | "None"
        258L | "Re-Auth"               | "None"
        275L | "Session-Termination"   | "None"
    }

    @Unroll
    def "should parse simple avps and assert avpsByCode equals avpsByName"() {
        given:
        def typeDefXml = new File(getDictionaryTestFile("avp_simple"))
        def dictionary = new XmlDictionary()

        when:
        dictionary.parse(typeDefXml)

        then:
        def avpsByCodeSize = dictionary.getAvpsByCodeAndVendorId().size()
        def avpsByNameSize = dictionary.getAvpsByNameAndVendorId().size()
        (avpsByCodeSize == avpsByNameSize) && avpsByNameSize == 6

        expect:
        dictionary.getAvp(code, vendorId) == dictionary.getAvp(name, vendorId)

        where:
        code | name             | vendorId
        1L   | "User-Name"      | 0L
        2L   | "User-Password"  | 0L
        3L   | "CHAP-Password"  | 0L
        4L   | "NAS-IP-Address" | 0L
        5L   | "NAS-Port"       | 0L
        404L | "Key-ExpiryTime" | 10415L
    }

    @Unroll
    def "should parse simple avps (not grouped/enum) and assert all enum attributes"() {
        given:
        def typeDefXml = new File(getDictionaryTestFile("avp_simple"))
        def dictionary = new XmlDictionary()

        when:
        dictionary.parse(typeDefXml)

        then:
        dictionary.getAvpsByCodeAndVendorId().size() == 6

        expect:
        def currentAvp = dictionary.getAvp(code, vendorCode)

        currentAvp.mayEncrypt == mayEncrypt
        currentAvp.mandatory == mandatory
        currentAvp.protected == protectedValue
        currentAvp.vendorBit == vendorBit

        where:
        code | vendorCode | mandatory           | mayEncrypt      | protectedValue      | vendorBit
        1L   | 0L         | DEF_MANDATORY       | DEF_MAY_ENCRYPT | DEF_PROTECTED       | DEF_VENDOR_BIT
        2L   | 0L         | ModalAttribute.MUST | DEF_MAY_ENCRYPT | DEF_PROTECTED       | DEF_VENDOR_BIT
        3L   | 0L         | ModalAttribute.MUST | true            | DEF_PROTECTED       | DEF_VENDOR_BIT
        4L   | 0L         | ModalAttribute.MUST | true            | ModalAttribute.MUST | DEF_VENDOR_BIT
        5L   | 0L         | ModalAttribute.MUST | true            | ModalAttribute.MUST | ModalAttribute.MUST_NOT
    }

    @Unroll
    def "should parse simple avps (not grouped/enum) and assert vendor"() {
        given:
        def typeDefXml = new File(getDictionaryTestFile("avp_simple"))
        def dictionary = new XmlDictionary()

        when:
        dictionary.parse(typeDefXml)

        then:
        dictionary.getAvpsByCodeAndVendorId().size() == 6

        expect:
        def currentAvp = dictionary.getAvp(code, vendorCode)
        currentAvp.vendor.vendorId == vendorId
        currentAvp.vendor.code == vendorCode
        currentAvp.vendor.name == vendorName

        where:
        code | vendorId | vendorCode | vendorName
        4L   | "None"   | 0L         | "None"
        404L | "TGPP"   | 10415L     | "3GPP"
    }

    @Unroll
    def "should parse simple avps (not grouped/enum) and assert type"() {
        given:
        def typeDefXml = new File(getDictionaryTestFile("avp_simple"))
        def dictionary = new XmlDictionary()

        when:
        dictionary.parse(typeDefXml)

        then:
        dictionary.getAvpsByCodeAndVendorId().size() == 6

        expect:
        def currentAvp = dictionary.getAvp(code, vendorCode)

        !currentAvp.type.isEnum()
        currentAvp.type.typeName == typeName
        currentAvp.type.typeParent?.typeName == typeParent

        where:
        code | vendorCode | typeName      | typeParent
        1L   | 0L         | "UTF8String"  | "OctetString"
        2L   | 0L         | "UTF8String"  | "OctetString"
        3L   | 0L         | "OctetString" | null
        4L   | 0L         | "OctetString" | null
        5L   | 0L         | "Unsigned32"  | null
        404L | 10415L     | "Time"        | null
    }

    @Unroll
    def "should parse enumerated avp with multiple values"() {
        given:
        def typeDefXml = new File(getDictionaryTestFile("avp_enum"))
        def dictionary = new XmlDictionary()
        def expectedAvpCode = 563L

        when:
        dictionary.parse(typeDefXml)
        def expectedAvp = dictionary.getAvp(expectedAvpCode)

        then:
        expectedAvp != null
        expectedAvp.name == "Day-Of-Week"
        expectedAvp.code == expectedAvpCode

        !expectedAvp.isGrouped()
        expectedAvp.isEnum()
        expectedAvp.enumValues.size() == 7

        expect:
        def enumValue = expectedAvp.enumValues[index]
        enumValue.name == name
        enumValue.code == code

        where:
        index | name        | code
        0     | "SUNDAY"    | 0
        1     | "MONDAY"    | 2
        2     | "TUESDAY"   | 4
        3     | "WEDNESDAY" | 8
        4     | "THURSDAY"  | 16
        5     | "FRIDAY"    | 32
        6     | "SATURDAY"  | 64
    }

    @Unroll
    def "should parse grouped avp with multiple nested avps"() {
        given:
        def typeDefXml = new File(getDictionaryTestFile("avp_grouped"))
        def dictionary = new XmlDictionary()
        def expectedAvpCode = 528L

        when:
        dictionary.parse(typeDefXml)
        def expectedAvp = dictionary.getAvp(expectedAvpCode)

        then:
        expectedAvp != null
        expectedAvp.name == "EUI64-Address-Mask"
        expectedAvp.code == expectedAvpCode

        !expectedAvp.isEnum()
        expectedAvp.isGrouped()
        expectedAvp.groupedAvps.size() == 2

        expect:
        expectedAvp.groupedAvps[index].name == name

        where:
        index | name
        0     | "EUI64-Address"
        1     | "EUI64-Address-Mask-Pattern"
    }

    @Ignore(value = "Helper function")
    def getDictionaryTestFile(def name) {
        return getClass().getResource("/dictionary/${name}.xml").toURI()
    }
}