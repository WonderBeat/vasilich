package com.vasilich.commands.chatbot

import org.springframework.stereotype.Component
import com.vasilich.commands.Command
import org.springframework.core.Ordered

Component
public class ChatBotCommand: Command, Ordered {

    override fun getOrder(): Int {
        return Ordered.LOWEST_PRECEDENCE
    }
    override fun execute(msg: String): String? {
        return "ChatBot stub"
    }
}
