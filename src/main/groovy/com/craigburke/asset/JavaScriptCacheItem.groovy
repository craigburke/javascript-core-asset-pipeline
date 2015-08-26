package com.craigburke.asset

import groovy.transform.CompileStatic
import org.mozilla.javascript.ScriptableObject

@CompileStatic
class JavaScriptCacheItem {
    ScriptableObject script = null
    boolean available = true
}