package com.craigburke.asset

import com.craigburke.asset.processors.ConcatProcessor
import spock.lang.Specification
import spock.lang.Unroll

class ConcatProcessorSpec extends Specification {

    @Unroll('concat the strings #processor.prepend + #input + #processor.append')
    def "ConcatProcessor can set values and call the concatString function"() {
        expect:
        processor.process(input, null) == result

        where:
        input << ['foo', 'bar', 'foo', 'Hello World']

        processor = new ConcatProcessor()
        result = "${processor.prepend}${input}${processor.append}"
    }

}

