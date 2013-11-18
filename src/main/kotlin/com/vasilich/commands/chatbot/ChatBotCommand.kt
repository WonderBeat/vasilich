package com.vasilich.commands.chatbot

import org.springframework.core.Ordered
import com.vasilich.commands.api.Command
import bitoflife.chatterbean.AliceBot
import kotlin.properties.Delegates
import org.springframework.core.io.ClassPathResource

public class ChatBotCommand (context: String,
                             splitters: String,
                             substitutors: String,
                             aimlResources: String): Command, Ordered {

    val alice: AliceBot by Delegates.lazy {
        val aimlResources = loadAimsFromClasspath(aimlResources)
        ChatBotLoader.createBot(
                ClassPathResource(context).getInputStream(),
                ClassPathResource(splitters).getInputStream(),
                ClassPathResource(substitutors).getInputStream(), aimlResources)!!
    }

    override fun getOrder(): Int = Ordered.LOWEST_PRECEDENCE
    override fun execute(msg: String): String? {
        return alice.respond(msg)
    }
}
