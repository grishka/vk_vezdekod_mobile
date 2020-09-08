package com.travels.searchtravels.utils

import android.text.TextUtils
import com.preview.planner.Define

object L {

    private val location: String
        get() {
            val className = L::class.java.name
            val traces = Thread.currentThread().stackTrace
            var found = false
            for (i in traces.indices) {
                val trace = traces[i]

                try {
                    if (found) {
                        if (!trace.className.startsWith(className)) {
                            val clazz = Class.forName(trace.className)
                            return "[" + getClassName(clazz) + ":" + trace.methodName + ":" + trace.lineNumber + "]: "
                        }
                    } else if (trace.className.startsWith(className)) {
                        found = true
                        continue
                    }
                } catch (e: ClassNotFoundException) {
                }

            }

            return "[]: "
        }

    fun d(str: String) {
        if (Define.LOG_ENABLE) {
            //Log.d(Define.TAG, getLocation() + str);
            println(location + str)
        }
    }

    fun v(str: String) {
        if (Define.LOG_ENABLE) {
            //Log.v(Define.TAG, getLocation() + str);
            println(location + str)
        }
    }

    fun e(str: String) {
        if (Define.LOG_ENABLE) {
            //Log.e(Define.TAG, str);
            println(location + str)
        }
    }

    private fun getClassName(clazz: Class<*>?): String {
        return if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.simpleName)) {
                clazz.simpleName
            } else getClassName(clazz.enclosingClass)

        } else ""

    }
}