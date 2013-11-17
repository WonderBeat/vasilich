package com.vasilich.commands.basic

import org.springframework.stereotype.Component
import com.vasilich.commands.api.Command

/**
 *  General command example.
 *  One should subscribe for "receive" topic and reply to "reply" one
 *  Config will be read from file on startup
 */
Component
public class Ping: Command {
    override fun execute(msg: String): String? = "pong"
}
