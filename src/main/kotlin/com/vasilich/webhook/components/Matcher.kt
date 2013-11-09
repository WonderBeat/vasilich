package com.vasilich.webhook.processors

import java.util.regex.Pattern

fun createExactMatcher(matchString: String): (String) -> Collection<String>? = {
    if(it.equalsIgnoreCase(matchString)) listOf(matchString) else null
}

/**
 * Returns groups, from regexp
 */
fun createRegexMatcher(regex: String): (String) -> Collection<String>? {
    val pattern = Pattern.compile(regex)
    return { line ->
        val matcher = pattern.matcher(line)
        when(matcher.find()) {
            true -> (0.. matcher.groupCount()).iterator().map{ matcher.group(it)!! }
                    .toArrayList().tail
            else -> null
        }
    }
}
