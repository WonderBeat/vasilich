package com.vasilich.commands.simple

import reactor.core.Observable
import javax.annotation.PostConstruct
import com.vasilich.commands.Command
import reactor.event.selector.Selectors
import reactor.event.Event
import reactor.function.Consumer
import org.springframework.core.Ordered
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

public class ReactiveCommandInitializer [Autowired] (private val reactor: Observable,
                                                     commands: List<Command>,
                                                     defaultOrder: Int = 50) {

    val logger = LoggerFactory.getLogger(this.javaClass)!!;

    private val orderedByPriority = commands.map {
            val order = when(it) {
                 is Ordered -> it.getOrder()
                 else -> defaultOrder
            }
            Pair(it, order)
        }.sortBy { it.second }.reverse()
            .map { it.first }

    PostConstruct
    private fun makeReactive() {
        reactor.on(Selectors.`$`("receive-message"), Consumer<Event<String>> {
            val msg = it!!.getData()!!
            val responses = orderedByPriority.map { it.execute(msg) }.filterNotNull()
            val response = responses.first
            if(response != null) {
                logger.debug("Chat: ${msg} -> ${response}")
                reactor.notify("send-message", Event.wrap(response))
            }
        })
    }
}
