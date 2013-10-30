package com.vasilich.commands.simple

import reactor.core.Observable
import javax.annotation.PostConstruct
import com.vasilich.commands.Command
import reactor.event.selector.Selectors
import reactor.event.Event
import reactor.function.Consumer
import org.springframework.core.Ordered
import org.slf4j.LoggerFactory
import com.vasilich.commands.chainCommands
import org.springframework.beans.factory.annotation.Autowired

/**
 * Grabs all available commands, sorts them by priority and listens to events
 * On event, passes it through the chain of commands, looking for the first one, that can process it
 */
public class ReactiveCommandInitializer [Autowired] (private val reactor: Observable,
                                                     commands: List<Command>,
                                                     defaultOrder: Int = 50) {

    val logger = LoggerFactory.getLogger(this.javaClass)!!;

    private val commandsByPriority = commands.map {
            val order = when(it) {
                 is Ordered -> it.getOrder()
                 else -> defaultOrder
            }
            Pair(it, order)
        }.sortBy { it.second }.reverse()
            .map { it.first }

    /**
     * Chain of responsibility. First command, that produces output wins
     */
    private val chainedCommands = commandsByPriority.reduce(::chainCommands)


    PostConstruct
    private fun makeReactive() {
        reactor.on(Selectors.`$`("receive-message"), Consumer<Event<String>> {
            val msg = it!!.getData()!!
            val response = chainedCommands.execute(msg)
            if(response != null) {
                logger.debug("Chat: ${msg} -> ${response}")
                reactor.notify("send-message", Event.wrap(response))
            }
        })
    }
}
