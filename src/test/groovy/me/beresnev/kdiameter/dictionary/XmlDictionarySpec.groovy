package me.beresnev.kdiameter.dictionary


import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

class XmlDictionarySpec extends Specification {

    @Unroll
    def "should correctly parse all type definitions with and without parent"() {
        given:
        def typeDefXml = new File(getDictionaryTestFile("typedef"))
        def dictionary = new XmlDictionary()

        when:
        dictionary.parse(typeDefXml)
        def types = dictionary.types

        then:
        types.size() == 4

        expect:
        types.get(typeName).typeParent == types[typeParentName]

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
        def applications = dictionary.applications

        then:
        applications.size() == 4

        expect:
        def application = applications[appId]
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
        def vendors = dictionary.vendors

        then:
        vendors.size() == 4

        expect:
        def vendor = vendors[vendorId]
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
        def commands = dictionary.commands

        then:
        commands.size() == 3

        expect:
        def command = commands[code]
        command.code == code
        command.name == name
        command.vendor == vendor

        where:
        code | name                    | vendor
        257L | "Capabilities-Exchange" | null
        258L | "Re-Auth"               | null
        275L | "Session-Termination"   | null
    }

    @Unroll
    def "should parse enumerated avp with multiple values"() {
        given:
        def typeDefXml = new File(getDictionaryTestFile("avp_enum"))
        def dictionary = new XmlDictionary()
        def expectedAvpCode = 563L

        when:
        dictionary.parse(typeDefXml)
        def avps = dictionary.avps
        def avpsWithExpectedCode = avps.get(expectedAvpCode)
        def expectedAvp = avpsWithExpectedCode?.get(0)

        then:
        avps.size() == 1
        avpsWithExpectedCode != null
        avpsWithExpectedCode.size() == 1

        expectedAvp != null
        expectedAvp.name == "Day-Of-Week"
        expectedAvp.code == expectedAvpCode

        !expectedAvp.isGrouped()
        expectedAvp.isEnum()
        expectedAvp.enumValues.size() == 7

        expect:
        def enumValue = expectedAvp.enumValues.get(index)
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
        def avps = dictionary.avps
        def avpsWithExpectedCode = avps.get(expectedAvpCode)
        def expectedAvp = avpsWithExpectedCode?.get(0)

        then:
        avps.size() == 1
        avpsWithExpectedCode != null
        avpsWithExpectedCode.size() == 1

        expectedAvp != null
        expectedAvp.name == "EUI64-Address-Mask"
        expectedAvp.code == expectedAvpCode

        !expectedAvp.isEnum()
        expectedAvp.isGrouped()
        expectedAvp.groupedAvps.size() == 2

        expect:
        expectedAvp.groupedAvps.get(index).name == name

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