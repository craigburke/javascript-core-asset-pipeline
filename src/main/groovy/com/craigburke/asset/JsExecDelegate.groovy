package com.craigburke.asset

import groovy.transform.CompileStatic
import org.mozilla.javascript.Context

@CompileStatic
class JsExecDelegate {

    JavaScriptCacheItem cacheItem
    Context context

    void put(String name, value) {
        cacheItem.script.put(name, cacheItem.script, value)
    }

    def eval(String input) {
       context.evaluateString(cacheItem.script, input, "command", 0, null)
    }
}
