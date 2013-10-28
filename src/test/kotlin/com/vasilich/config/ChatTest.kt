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

ContextConfiguration(classes = array(javaClass<AppContext>(), javaClass<ListenerContext>()))
RunWith(javaClass<SpringJUnit4ClassRunner>())
public class ChatTest() {

    Autowired
    var tester: Reactor? = null
    

    fun replyFor(msg: String, matcher: (reply: String?) -> Unit ) {
        tester!!.notify("test-send", Event.wrap(msg))
        val promise = Promises.defer<String>()!!.get()!!
        tester!!.on(Selectors.`$`("test-recieve"), Consumer<Event<String>> {
            promise.acceptEvent(it)
        })
        val response = promise.compose()!!.await(5, TimeUnit.SECONDS)
        matcher(response)
    }

    Test fun testConfigExtraction() {
        replyFor("Vasilich, ping", { assert(it == "pong", "Ping command should recieve a reply") })
        replyFor("Vasilich, what time is it?", { assert(it?.startsWith("Current time")!!,
                    "Vasilich should reply with current server time") })
        replyFor("Vasilich, wait", { assert(it == null,
                    "Nothing can stop Vasilich") })
    }
}
