package com.vasilich.commands.chatbot

import org.springframework.core.Ordered
import com.vasilich.commands.api.Command
import bitoflife.chatterbean.AliceBot
import kotlin.properties.Delegates

public class ChatBotCommand (private val alice: AliceBot): Command, Ordered {

    override fun getOrder(): Int = Ordered.LOWEST_PRECEDENCE
    override fun execute(msg: String): String? = alice.respond(msg)
}
