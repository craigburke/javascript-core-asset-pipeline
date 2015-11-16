package com.craigburke.asset

import groovy.transform.CompileStatic
import org.mozilla.javascript.Context
import org.mozilla.javascript.Script
import org.mozilla.javascript.ScriptableObject

@CompileStatic
class JavaScriptEngine {

    private ScriptableObject globalScope
    private Script baseScript

    JavaScriptEngine(String baseJs = "", boolean interpreted = false) {
        Context context = Context.enter()
        try {
            if (interpreted) {
                context.optimizationLevel = -1
            }
            globalScope = context.initStandardObjects(null, true)

            if (baseJs) {
                baseScript = context.compileString(baseJs, 'Base script', 0, null)
            }
        }
        finally {
            context.exit()
        }
    }

    String run(Closure closure) {
        String result

        try {
            JavaScriptRunner runner = new JavaScriptRunner(baseScript, globalScope)
            Closure clonedClosure = closure.rehydrate(runner, closure.owner, closure.owner)
            result = clonedClosure()
        }
        catch (Exception ex) {
            throw new Exception("JavaScript execution failed: ${ex.message}", ex)
        }
        finally {
            cleanup()
        }
        result
    }

    void cleanup() {
        try {
            Context.exit()
        }
        catch (Exception ex) { }
    }

}
