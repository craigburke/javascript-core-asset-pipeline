package com.craigburke.asset

import asset.pipeline.AbstractProcessor
import asset.pipeline.AssetCompiler
import asset.pipeline.AssetFile
import groovy.transform.CompileStatic
import groovy.transform.Synchronized
import org.mozilla.javascript.Context
import org.mozilla.javascript.Script
import org.mozilla.javascript.ScriptableObject

@CompileStatic
abstract class JavaScriptProcessor extends AbstractProcessor {

    private static Script loadedBaseScript
    private static final List<JavaScriptCacheItem> cache

    JavaScriptProcessor(AssetCompiler precompiler) {
        super(precompiler)
    }

    abstract String process(String input, AssetFile assetFile)

    static {
        int cores = Runtime.runtime.availableProcessors()
        cache = (1..cores).collect { new JavaScriptCacheItem() }
    }

    @Synchronized
    static private void loadBaseScript(String name) {
        if (!loadedBaseScript) {
            URL baseScriptResource = JavaScriptProcessor.classLoader.getResource(name)
            Context context = Context.enter()
            loadedBaseScript = context.compileString(baseScriptResource.text, baseScriptResource.file, 0, null)
        }
    }

    static String jsExec(String baseScriptName = null, Closure closure) {
        JavaScriptCacheItem cacheItem
        String result

        try {
            if (baseScriptName) {
                loadBaseScript(baseScriptName)
            }
            Context context = Context.enter()
            cacheItem = getCacheItem(context)
            JsExecDelegate delegate = new JsExecDelegate(cacheItem: cacheItem, context: context)
            Closure clonedClosure = closure.rehydrate(delegate, closure.owner, closure.owner)
            result = clonedClosure()
        }
        catch (Exception ex) {
            throw new Exception("${baseScriptName} failed: ${ex.message}", ex)
        }
        finally {
            try {
                Context.exit()
            }
            catch (Exception ex) {
            }
            if (cacheItem) {
                releaseCacheItem(cacheItem)
            }
        }
        result
    }

    @Synchronized('cache')
    private static JavaScriptCacheItem loadCacheItem() {
        JavaScriptCacheItem cacheItem

        while (!cacheItem) {
            def availableCacheItems = cache.findAll { it.available }

            if (availableCacheItems) {
                cacheItem = availableCacheItems.sort { it.script }.first()
                cacheItem.available = false
            } else {
                cache.wait()
            }
        }
        cacheItem
    }

    private static JavaScriptCacheItem getCacheItem(Context context) {
        JavaScriptCacheItem cacheItem = loadCacheItem()

        if (!cacheItem.script) {
            ScriptableObject jsScope = context.initStandardObjects(null, true)
            if (loadedBaseScript) {
                loadedBaseScript.exec(context, jsScope)
            }
            cacheItem.script = jsScope
        }

        cacheItem
    }

    @Synchronized('cache')
    static void releaseCacheItem(JavaScriptCacheItem cacheItem) {
        cacheItem.available = true
        cache.notify()
    }

}
