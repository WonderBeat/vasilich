package com.vasilich.commands.simple

import reactor.core.Observable
import javax.annotation.PostConstruct
import com.vasilich.commands.Command
import reactor.event.selector.Selectors
import reactor.event.Event
import reactor.function.Consumer
import org.springframework.core.Ordered
import org.springframework.beans.factory.annotation.Autowired

public class ReactiveCommandInitializer [Autowired] (private val reactor: Observable,
                                                     commands: List<Command>,
                                                     defaultOrder: Int = 50) {

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
            val response = orderedByPriority.map { it.execute(msg) }.filterNotNull().first
            if(response != null) {
                reactor.notify("send-message", Event.wrap(response))
            }
        })
    }
}
