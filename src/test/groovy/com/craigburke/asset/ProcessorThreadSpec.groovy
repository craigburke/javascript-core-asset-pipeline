package com.craigburke.asset

import asset.pipeline.JsAssetFile
import com.craigburke.asset.processors.ThreadProcessor
import spock.lang.Specification
import java.util.concurrent.ConcurrentHashMap

class ProcessorThreadSpec extends Specification {

    def "Simulate parallel processors with several threads"() {
        given:
        ConcurrentHashMap results = [:]

        when:
        (1..threadPool).collect { index ->
            Thread.start {
                println "Starting thread: ${index}"
                results[index] = new ThreadProcessor().process("${input}${index}", new JsAssetFile())
            }
        }*.join()

        then:
        results.size() == threadPool

        and:
        results.every { key, value -> value == "${input}${key}" }

        where:
        input = 'foo'
        threadPool = 20
    }

}

