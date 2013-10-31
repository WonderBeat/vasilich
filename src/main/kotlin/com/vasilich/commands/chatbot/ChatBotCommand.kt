package com.vasilich.commands.chatbot

import org.springframework.stereotype.Component
import org.springframework.core.Ordered
import com.vasilich.commands.api.Command

Component
public class ChatBotCommand: Command, Ordered {

    override fun getOrder(): Int {
        return Ordered.LOWEST_PRECEDENCE
    }
    override fun execute(msg: String): String? {
        return "ChatBot stub"
    }
}
