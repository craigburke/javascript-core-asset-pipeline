package com.craigburke.asset

import groovy.transform.CompileStatic

import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject

@CompileStatic
class JavaScriptRunner {

    private Scriptable scope

    JavaScriptRunner(ScriptableObject globalScope) {
        Context context = Context.enter()
        scope = context.newObject(globalScope)
        scope.prototype = globalScope
        scope.parentScope = null
    }

    void put(String name, value) {
        scope.put(name, scope, value)
    }

    def get(String name) {
        scope.get(name, scope)
    }

    def getProperty(String name) {
        get(name)
    }

    void setProperty(String name, value) {
        put(name, value)
    }

    def eval(String input) {
        context.evaluateString(scope, input, 'javascript eval command', 0, null)
    }

    static Context getContext() {
        Context.enter()
    }

}
