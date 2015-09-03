package com.craigburke.asset.processors

import asset.pipeline.AssetCompiler
import asset.pipeline.AssetFile
import com.craigburke.asset.JavaScriptEngine
import com.craigburke.asset.JavaScriptProcessor
import groovy.transform.Synchronized

class ThreadProcessor extends JavaScriptProcessor {

    static JavaScriptEngine simpleEngine

    ThreadProcessor(AssetCompiler precompiler) {
        super(precompiler)
    }

    @Synchronized
    JavaScriptEngine getEngine() {
        if (!simpleEngine) {
            simpleEngine = new JavaScriptEngine()
        }
        simpleEngine
    }

    String process(String input, AssetFile file) {
        javaScript {
            var1 = input
            eval('var1')
        }
    }

}
