package com.vasilich.commands

import org.springframework.stereotype.Component
import reactor.core.Observable
import reactor.event.Event
import reactor.spring.annotation.Selector
import reactor.spring.annotation.ReplyTo
import com.vasilich.config.Config
import javax.annotation.PostConstruct
import reactor.event.selector.Selectors
import com.vasilich.connectors.xmpp.Topics
import org.springframework.beans.factory.annotation.Autowired

/**
 *  General command example.
 *  One should subscribe for "receive" topic and reply to "reply" one
 *  Config will be read from file on startup
 */
Component
public class Ping: Command {

    override fun execute(msg: String): String? {
        return "pong"
    }

}
