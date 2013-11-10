package com.vasilich.commands.chatbot

import java.io.InputStream
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

fun loadAimsFromClasspath(classpathPattern: String): Collection<InputStream> {
    val resolver = PathMatchingResourcePatternResolver()
    return resolver.getResources(classpathPattern)!!.map { it.getInputStream() }.filterNotNull()
}
