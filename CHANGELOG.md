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