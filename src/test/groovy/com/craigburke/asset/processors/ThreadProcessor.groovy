package com.craigburke.asset.processors

import asset.pipeline.AbstractProcessor
import asset.pipeline.AssetCompiler
import asset.pipeline.AssetFile
import com.craigburke.asset.JavaScriptEngine
import groovy.transform.Synchronized

class ThreadProcessor extends AbstractProcessor {

    static JavaScriptEngine jsEngine

    ThreadProcessor(AssetCompiler precompiler) {
        super(precompiler)
        setupEngine()
    }

    @Synchronized
    static void setupEngine() {
        if (!jsEngine) {
            jsEngine = new JavaScriptEngine()
        }
    }

    String process(String input, AssetFile file) {
        jsEngine.run {
            var1 = input
            var1
        }
    }

}
