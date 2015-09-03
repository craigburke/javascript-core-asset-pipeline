package com.craigburke.asset

import groovy.transform.CompileStatic
import org.mozilla.javascript.ScriptableObject

@CompileStatic
class CacheItem {
    ScriptableObject script = null
    boolean available = true
}
