package com.vasilich.config

import org.springframework.test.context.ContextConfiguration
import org.junit.Test
import com.vasilich.config.listener.ListenerContext
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.Reactor
import reactor.event.Event
import reactor.core.composable.spec.Promises
import reactor.event.selector.Selectors
import reactor.function.Consumer
import java.util.concurrent.TimeUnit
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.slf4j.LoggerFactory

ContextConfiguration(classes = array(javaClass<AppContext>(), javaClass<ListenerContext>()))
RunWith(javaClass<SpringJUnit4ClassRunner>())
public class ChatTest() {

    Autowired
    var tester: Reactor? = null

    val logger = LoggerFactory.getLogger(this.javaClass)!!;

    fun replyFor(msg: String, matcher: (reply: String?) -> Boolean, description: String) {
        tester!!.notify("test-send", Event.wrap(msg))
        val promise = Promises.defer<String>()!!.get()!!
        tester!!.on(Selectors.`$`("test-recieve"), Consumer<Event<String>> {
            promise.acceptEvent(it)
        })
        promise.compose()!!.onError(Consumer{ logger.error("Test failed", it) })

        val response = promise.compose()!!.await(3, TimeUnit.SECONDS)
        assert(matcher(response), description)
    }
    /**
     * Integration test. Real chat between Michalich and Vasilich
     */
    Test fun chatTest() {
        replyFor("Vasilich, ping", { it == "pong" }, "Ping command should recieve a reply")
        replyFor("Vasilich, what time is it?", { it?.startsWith("Current time")!! },
                    "Vasilich should reply with current server time")
        replyFor("Vasilich, what's your uptime?", { it?.startsWith("Oh, long enough")!! && it!!.length > 20 },
                    "Vasilich should launch script and prints output")
        replyFor("Vasilich, what can you do?", { it?.contains("abracadabra")!! },
                    "Vasilich should launch script and prints output")
        replyFor("Vasilich, WTF?", { it != null }, "Vasilich is talkative. He should response ;)")
    }
}
