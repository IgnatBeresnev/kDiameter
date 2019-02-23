package me.beresnev.kdiameter

import spock.lang.Specification

class WorldGreeterSpec extends Specification {

    def "should return hello world when greeted"() {
        given:
        def worldGreeter = new WorldGreeter()

        when:
        def greeting = worldGreeter.greet()

        then:
        greeting == "Hello, world!"
    }
}
