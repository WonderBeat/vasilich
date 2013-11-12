package com.vasilich.commands.bootstrap

import reactor.core.Observable
import javax.annotation.PostConstruct
import reactor.event.selector.Selectors
import reactor.event.Event
import reactor.function.Consumer
import org.springframework.core.Ordered
import org.slf4j.LoggerFactory
import com.vasilich.commands.api.Command
import org.springframework.beans.factory.annotation.Autowired

/**
 * Chain of responsibility. First command, that produces output wins
 */
private fun chainCommandsByOrder(commands: List<Command>, defaultCommandOrder: Int = 50): Command {
    fun Iterable<Command>.byOrder(): List<Command> = this.sortBy {
        when(it) {
            is Ordered -> it.getOrder()
            else -> defaultCommandOrder
        }
    }
    return commands.byOrder() reduce ::chainCommands
}

/**
 * Grabs all available commands, sorts them by priority and listens to events
 * On event, passes it through the chain of commands, looking for the first one, that can process it
 */
public class ReactiveCommandInitializer [Autowired] (private val reactor: Observable,
                                                     commands: List<Command>) {

    val logger = LoggerFactory.getLogger(this.javaClass)!!;

    val commandComposite = chainCommandsByOrder(commands)

    PostConstruct
    private fun makeReactive() {
        reactor.on(Selectors.`$`("receive-message"), Consumer<Event<String>> {
            val msg = it!!.getData()!!
            val response = commandComposite execute msg
            if(response != null) {
                logger.debug("Chat: ${msg} -> ${response}")
                reactor.notify("send-message", Event.wrap(response))
            }
        })
    }
}
