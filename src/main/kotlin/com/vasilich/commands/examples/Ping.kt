package com.vasilich.commands

import org.springframework.stereotype.Component
import reactor.core.Observable
import reactor.event.Event
import reactor.spring.annotation.Selector
import reactor.spring.annotation.ReplyTo
import com.vasilich.config.Config
import org.springframework.beans.factory.annotation.Autowired

Component
Config("ping")
class PingCfg(val enabled: Boolean = false)


/**
 *  General command example.
 *  One should subscribe for "receive" topic and reply to "reply" one
 *  Config will be read from file on startup
 */
Component
public class Ping [Autowired] (val reactor: Observable, val cfg: PingCfg) {

    Selector("recieve-message")
    ReplyTo("send-message")
    fun pong(msg: Event<String>): String? {
        if(cfg.enabled && msg.getData()?.contains("ping") as Boolean) {
            return "pong"
        }
        return null
    }

}
