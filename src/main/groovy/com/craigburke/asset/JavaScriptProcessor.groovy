package com.craigburke.asset

import asset.pipeline.AbstractProcessor
import asset.pipeline.AssetCompiler
import asset.pipeline.AssetFile
import groovy.transform.CompileStatic

@CompileStatic
abstract class JavaScriptProcessor extends AbstractProcessor {

    JavaScriptProcessor(AssetCompiler precompiler) {
        super(precompiler)
    }

    abstract JavaScriptEngine getEngine()
    abstract String process(String input, AssetFile assetFile)
    String javaScript(Closure closure) { engine.run closure }

}
