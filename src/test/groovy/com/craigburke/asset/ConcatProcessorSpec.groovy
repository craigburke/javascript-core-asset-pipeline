package com.craigburke.asset

import asset.pipeline.AssetCompiler
import asset.pipeline.AssetFile
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

class ConcatProcessor extends JavaScriptProcessor {

    String prepend = 'console.log("'
    String append = '");'

    ConcatProcessor(AssetCompiler precompiler) {
        super(precompiler)
    }

    String process(String input, AssetFile file) {
        jsExec('concat-strings.js') {
            put('prepend', prepend)
            put('input', input)
            eval('var temp = concatStrings(prepend,input);')
            put('append', append)
            eval('concatStrings(temp,append)')
        }
    }

}