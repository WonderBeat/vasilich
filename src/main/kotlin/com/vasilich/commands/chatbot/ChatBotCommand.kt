package com.vasilich.commands.chatbot

import org.springframework.core.Ordered
import com.vasilich.commands.api.Command
import bitoflife.chatterbean.AliceBot

public class ChatBotCommand (private val alice: AliceBot): Command, Ordered {

    override fun getOrder(): Int {
        return Ordered.LOWEST_PRECEDENCE
    }
    override fun execute(msg: String): String? {
        val msgT = msg.trimLeading("Vasilich, ")
        return alice.respond(msgT)
    }
}
