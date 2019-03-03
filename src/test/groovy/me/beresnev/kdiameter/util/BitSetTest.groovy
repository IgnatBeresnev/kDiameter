package me.beresnev.kdiameter.util

import spock.lang.Specification
import spock.lang.Unroll

class BitSetTest extends Specification {

    @Unroll
    def "should correctly decode bits from provided bit holder"() {
        expect:
        def bitSet = new BitSet(bitHolder)

        bitSet.get(3) == fourthBit
        bitSet.get(2) == thirdBit
        bitSet.get(1) == secondBit
        bitSet.get(0) == firstBit

        where:
        bitHolder | fourthBit | thirdBit | secondBit | firstBit
        1         | false     | false    | false     | true // 0001
        3         | false     | false    | true      | true // 0011
        7         | false     | true     | true      | true // 0111
        15        | true      | true     | true      | true // 1111
    }

    def "should correctly encode bits to an int"() {
        expect:
        def bitSet = new BitSet()

        bitSet.set(3, fourthBit)
        bitSet.set(2, thirdBit)
        bitSet.set(1, secondBit)
        bitSet.set(0, firstBit)

        bitSet.getAsInt() == bitHolder

        where:
        fourthBit | thirdBit | secondBit | firstBit | bitHolder
        false     | false    | false     | true     | 1  // 0001
        false     | false    | true      | true     | 3  // 0011
        false     | true     | true      | true     | 7  // 0111
        true      | true     | true      | true     | 15 // 1111
    }

    def "should throw IndexOutOfBounds for bit index below zero"() {
        given:
        def bitSet = new BitSet()

        when:
        bitSet.get(-1)

        then:
        IndexOutOfBoundsException e = thrown()
    }

    def "should throw IndexOutOfBounds for bit index above 31"() {
        given:
        def bitSet = new BitSet()

        when:
        bitSet.get(32)

        then:
        IndexOutOfBoundsException e = thrown()
    }
}
