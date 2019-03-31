0.0.6
-----
* Diameter message encoder. Convert message DTOs to bytes
* Basic tests based on a real CER dump
* Codacy and travis checks, GNU GPL license

0.0.5
-----
* Diameter message decoder. Convert received bytes to human-usable message
* Basic tests based on a real CER dump
* BitSet utility class, for easier manipulation with bit flags

0.0.4
-----
* Closes [Issue #1](https://github.com/IgnatBeresnev/kDiameter/issues/1)
* Dictionary interface, XmlDictionary as the only implementation ATM
* More tests for simple AVPs (vendor, type, attributes, etc)

0.0.3
-----
* XmlDictionary-related refactoring
* Testcase to cover parsing 9k lines dictionary.xml
* Increase travis_wait to 10 minutes instead of 5 to give us some more time to work with 
in the future since it already takes a couple of minutes to compile everything

0.0.2
-----
* dictionary.xml parsing with optimistic unit tests
* Extensions for DOM xml parser
* Introduce JCIP annotations, such as @ThreadSafe

0.0.1
-----
* Add shadowJar gradle plugin (fatJar), execute during build task
* Add version.properties file along with version.gradle extension