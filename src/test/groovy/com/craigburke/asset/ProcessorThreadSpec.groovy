package com.craigburke.asset

import asset.pipeline.AssetCompiler
import asset.pipeline.AssetFile
import asset.pipeline.JsAssetFile
import spock.lang.Specification

class ProcessorThreadSpec extends Specification {

    def "Simulate parallel processors with several threads"() {
        given:
        def results = []

        when:
        (1..threadPool).collect { index ->
            Thread.start {
                println "Starting thread: ${index}"
                results << new SimpleProcessor().process(input, new JsAssetFile())
            }
        }*.join()

        then:
        results.size() == threadPool

        and:
        results.every { it == input }

        where:
        input = 'foo'
        threadPool = 20
    }

}

class SimpleProcessor extends JavaScriptProcessor {

    SimpleProcessor(AssetCompiler precompiler) {
        super(precompiler)
    }

    String process(String input, AssetFile file) {
        jsExec {
            put('input', input)
            eval('input')
        }
    }

}