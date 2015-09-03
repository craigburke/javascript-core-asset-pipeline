package com.craigburke.asset.processors

import asset.pipeline.AssetCompiler
import asset.pipeline.AssetFile
import com.craigburke.asset.JavaScriptEngine
import com.craigburke.asset.JavaScriptProcessor
import groovy.transform.Synchronized

class ConcatProcessor extends JavaScriptProcessor {

    String prepend = 'console.log("'
    String append = '");'

    ConcatProcessor(AssetCompiler precompiler) {
        super(precompiler)
    }

    static JavaScriptEngine concatEngine

    @Synchronized
    JavaScriptEngine getEngine() {
        if (!concatEngine) {
            concatEngine = new JavaScriptEngine('concat-strings.js')
        }
        concatEngine
    }

    String process(String input, AssetFile file) {
        javaScript {
            var1 = prepend
            var2 = input

            temp = eval 'concatStrings(var1,var2);'
            var3 = append
            eval 'concatStrings(temp,var3)'
        }
    }

}
