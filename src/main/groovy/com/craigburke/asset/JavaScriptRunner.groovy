package com.craigburke.asset

import groovy.transform.CompileStatic

import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.Script
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject

@CompileStatic
class JavaScriptRunner {

    private Scriptable scope
    private Context context

    JavaScriptRunner(Script baseScript, ScriptableObject globalScope) {
        context = Context.enter()
        if (baseScript) {
            baseScript.exec(context, globalScope)
        }
        scope = context.newObject(globalScope)
        scope.setPrototype(globalScope)
        scope.setParentScope(null)
    }

    def methodMissing(String name, args) {
        Scriptable prototype = scope.getPrototype()
        Function jsFunction = (Function)prototype.get(name, prototype);
        jsFunction.call(context, scope, scope, args as Object[])
    }

    def getProperty(String name) {
        scope.get(name, scope)
    }

    void setProperty(String name, value) {
        scope.put(name, scope, value)
    }

}
