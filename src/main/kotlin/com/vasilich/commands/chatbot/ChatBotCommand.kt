package com.vasilich.commands.chatbot

import org.springframework.stereotype.Component
import org.springframework.core.Ordered
import com.vasilich.commands.api.Command
import bitoflife.chatterbean.AliceBot
import org.springframework.beans.factory.annotation.Autowired

public class ChatBotCommand (private val alice: AliceBot): Command, Ordered {

    override fun getOrder(): Int {
        return Ordered.LOWEST_PRECEDENCE
    }
    override fun execute(msg: String): String? {
        return alice.respond(msg)
    }
}
