package functional.chat
import app.AppContext
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

@ContextConfiguration(classes = [AppContext, SpringChatContext] )
@Slf4j
class ChatTest extends Specification {

    @Autowired
    Reactor reactor

    def timeout = 3

    private String replyFor(String msg) {
        reactor.notify('test-send', Event.wrap(msg))
        waitReply()
    }

    private String waitReply() {
        def promise = Promises.defer().get()
        reactor.on(Selectors.$('test-receive'), { promise.acceptEvent(it) } as Consumer<Event<String>>)
        promise.compose().onError({ log.error("Test failed", it) } as Consumer<Exception>)
        promise.compose().await(timeout, TimeUnit.SECONDS)
    }

    /**
     * Integration test. Real chat between Michalich and Vasilich
     */
    def 'Live chat: Vasilich and Michalich. Xmpp based'() {
        when:
        def reply = replyFor(michalich)

        then:
        assert reply != null, "reply is null. Vasilich didn't respond for '${michalich}' in ${timeout} seconds"
        if(matcher instanceof String) {
            assert reply == matcher // simple match
        } else if(matcher instanceof List) { // multi line answers
            matcher.first().call(reply)
            matcher.tail().each { it.call(waitReply()); }
        } else {
            matcher.call(reply) // complex matcher
        }

        where:
        michalich                   | matcher
        'Vasilich, ping'            | 'pong'
        'v ping'                    | 'pong'
        'v Hi'                      | 'Hello'
        'v good Morning!'           | 'Hello'
        'v so, Chao'                | 'Bye, see you'
        'v are you alive?'          | { assert it != null }
        "v what's your uptime"      | { assert it.startsWith('Oh, long enough') }
        'v what can you do'         | { assert it.contains('abracadabra') }
        'v not available'           | { assert it.contains('Sorry') }
        'v test exit code detection'| { assert it.contains('Failed') }
        'v launch verbose script'   | [{ assert it.startsWith('Invisible exception')},
                                       { assert it == 'Done' }]
        'v check git version' | { assert it.contains('git version') } // no matter if ping succeeded or not
        'Vasilich, Do you know any good IT place to work in Spb?' | 'EPAM St.Petersburg'
        "v ansible version"      | { assert it != null }
        "v ansible deploy"      | { assert it != null }
    }

}
