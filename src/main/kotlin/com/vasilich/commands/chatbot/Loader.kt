package com.vasilich.commands.chatbot

import org.springframework.core.io.ClassPathResource
import java.io.FileInputStream
import java.io.InputStream
import bitoflife.chatterbean.parser.AliceBotParser

fun loadAimsFromClasspath(folderName: String): Collection<InputStream> {
    val folder = ClassPathResource(folderName)
    return folder.getFile()!!.listFiles()!!.map {
        when {
            it.getName().endsWith(".aiml") -> FileInputStream(it)
            else -> null
        }
    }.filterNotNull()
}
