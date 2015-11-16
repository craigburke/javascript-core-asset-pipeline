package com.craigburke.asset.processors

import asset.pipeline.AbstractProcessor
import asset.pipeline.AssetCompiler
import asset.pipeline.AssetFile
import com.craigburke.asset.JavaScriptEngine
import groovy.transform.Synchronized

class ConcatProcessor extends AbstractProcessor {

    String prepend = 'console.log("'
    String append = '");'

    static JavaScriptEngine jsEngine

    ConcatProcessor(AssetCompiler precompiler) {
        super(precompiler)
        setupEngine()
    }

    @Synchronized
    static void setupEngine() {
        if (!jsEngine) {
            URL concatStrings = ConcatProcessor.classLoader.getResource('concat-strings.js')
            jsEngine = new JavaScriptEngine(concatStrings.text)
        }
    }

    String process(String input, AssetFile file) {
        jsEngine.run {
            var1 = prepend
            var2 = input

            temp = concatStrings(var1, var2)
            var3 = append
            concatStrings(temp, var3)
        }
    }

}
