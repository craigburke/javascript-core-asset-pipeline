package com.craigburke.asset

import groovy.transform.CompileStatic
import groovy.transform.Synchronized
import org.mozilla.javascript.Context
import org.mozilla.javascript.Script
import org.mozilla.javascript.ScriptableObject

@CompileStatic
class JavaScriptEngine {

    private ScriptableObject globalScope
    private Script baseScript

    private List<CacheItem> cache

    JavaScriptEngine(String baseScriptName = null, boolean interpreted = false) {
        Context context = Context.enter()
        try {
            if (interpreted) {
                context.optimizationLevel = -1
            }
            globalScope = context.initStandardObjects(null, true)

            if (baseScriptName) {
                URL baseScriptResource = JavaScriptProcessor.classLoader.getResource(baseScriptName)
                baseScript = context.compileString(baseScriptResource.text, baseScriptResource.file, 0, null)
            }

            int cores = Runtime.runtime.availableProcessors()
            cache = (1..cores).collect { new CacheItem() }
        }
        finally {
            context.exit()
        }
    }

    @Synchronized('cache')
    private CacheItem loadCacheItem() {
        CacheItem cacheItem = cache.find { it.available }

        while (!cacheItem) {
            cache

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

    private CacheItem getCacheItem(Context context) {
        CacheItem cacheItem = loadCacheItem()

        if (!cacheItem.script) {
            cacheItem.script = globalScope
        }
        if (baseScript) {
            baseScript.exec(context, globalScope)
        }

        cacheItem
    }

    String run(Closure closure) {
        CacheItem cacheItem
        String result

        try {
            Context context = Context.enter()
            cacheItem = getCacheItem(context)
            JavaScriptRunner runner = new JavaScriptRunner(globalScope)
            Closure clonedClosure = closure.rehydrate(runner, closure.owner, closure.owner)
            result = clonedClosure()
        }
        catch (Exception ex) {
            throw new Exception("JavaScript execution failed: ${ex.message}", ex)
        }
        finally {
            cleanup(cacheItem)
        }
        result
    }

    void cleanup(CacheItem cacheItem) {
        try {
            Context.exit()
        }
        catch (Exception ex) { }

        if (cacheItem) {
            releaseCacheItem(cacheItem)
        }
    }

    @Synchronized('cache')
    void releaseCacheItem(CacheItem cacheItem) {
        cacheItem.available = true
        cache.notify()
    }


}
