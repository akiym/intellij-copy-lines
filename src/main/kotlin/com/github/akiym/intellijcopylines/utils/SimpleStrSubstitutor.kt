package com.github.akiym.intellijcopylines.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

class SimpleStrSubstitutor(private val map: Map<String, Any>) {
    private val pattern = Pattern.compile("""\$\{([A-Z_]+)}""")

    fun replace(format: String): String {
        val sb = StringBuilder()
        val m = pattern.matcher(format)
        while (m.find()) {
            val varName = m.group(1)
            val replacement = map.getOrDefault(varName, "").toString()
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement))
        }
        m.appendTail(sb)
        return sb.toString()
    }
}
