package com.vasilich.commands.bootstrap

import reactor.core.Observable
import javax.annotation.PostConstruct
import reactor.event.selector.Selectors
import reactor.event.Event
import reactor.function.Consumer
import org.springframework.core.Ordered
import org.slf4j.LoggerFactory
import com.vasilich.commands.api.Command
import com.vasilich.connectors.xmpp.Topics

/**
 * Grabs all available commands, sorts them by priority and listens to events
 * On event, passes it through the chain of commands, looking for the first one, that can process it
 */
public class ReactiveCommandInitializer (private val reactor: Observable,
                                         private val command: Command,
                                                     private val topics: Topics = Topics()) {

    val logger = LoggerFactory.getLogger(this.javaClass)!!;

    PostConstruct
    private fun makeReactive() {
        reactor.on(Selectors.`$`(topics.receive), Consumer<Event<String>> {
            val msg = it!!.getData()!!
            val response = command execute msg
            if(response != null) {
                logger.debug("Chat: ${msg} -> ${response}")
                reactor.notify(topics.send, Event.wrap(response))
            }
        })
    }
}
