package com.vasilich.commands.basic

import app.AppContext
import com.vasilich.connectors.chat.Chat
import functional.chat.context.SpringChatContext
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import reactor.core.Reactor
import reactor.core.composable.spec.Promises
import reactor.event.Event
import reactor.event.selector.Selectors
import reactor.function.Consumer
import spock.lang.Specification

import java.util.concurrent.TimeUnit

/**
 *
 * @author Ilia_Mestnikov
 * @date 11/25/13
 * @time 11:27 PM
 */
@ContextConfiguration(classes = [AppContext, SpringChatContext])
@Slf4j
class DelayedCommandTest extends Specification {

    @Autowired
    Chat chat

    @Autowired
    Reactor reactor

    def timeout = 3

    private String replyFor(String msg) {
        reactor.notify('send-message', Event.wrap(msg))
        waitReply()
    }

    private String waitReply() {
        def promise = Promises.defer().get()
        reactor.on(Selectors.$('receive-message'), { promise.acceptEvent(it) } as Consumer<Event<String>>)
        promise.compose().onError({ log.error("Test failed", it) } as Consumer<Exception>)
        promise.compose().await(timeout, TimeUnit.SECONDS)
    }

    def "on delayed command Vasilich should run the original command in given time"() {

        given:
        DelayedCommandCfg cfg = new DelayedCommandCfg(true, 0, 1, "SECONDS")
        DelayedCommand command = new DelayedCommand(cfg, chat)

        when:
        def reply = command.execute(msg)
        then:
        reply.contains(matcher)
        assert reply != null, "reply is null, delayed command wasn't recognized "
        def reply2 = waitReply()
        assert reply2 != null, "command is null, target command wasn't recognized "
        reply2.contains(matcher2)
        where:
        msg                                | matcher                 | matcher2
        ", delay 1 sec, ping"              | "will be executed in 1" | "ping"
        ", delay 1 sec, delay 1 sec, ping" | "will be executed in 1" | "delay"
        ", delay 1 sec, hi"                | "will be executed in 1" | "hi"
    }
}
