package functional.chat.context
import app.AppContext
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import reactor.core.Reactor
import reactor.core.composable.Deferred
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
    Reactor tester

    def timeout = 3

    private String replyFor(String msg) {
        tester.notify('test-send', Event.wrap(msg))
        Deferred promise = Promises.defer().get()
        tester.on(Selectors.$('test-recieve'), {
            promise.acceptEvent(it) } as Consumer<Event<String>>)

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
            assert reply == matcher
        } else {
            matcher.call(reply)
        }

        where:
        michalich                   | matcher
        'Vasilich, ping'            | 'pong'
        'v ping'                    | 'pong'
        'v Hi'                      | 'Hello'
        'v so, Chao'                | 'Bye, see you'
        'v are you alive?'          | { assert it != null }
        "v what's your uptime"      | { assert it.startsWith('Oh, long enough') }
        'v what can you do'         | { assert it.contains('abracadabra') }
        'Vasilich, Do you know any good IT place to work in Spb?' | 'EPAM St.Petersburg'
    }

    def "Live chat: Vasilich and Michalich. Verbose script. Extracted to separate method because it shouldn't inerfier with others"() {
        when:
        def reply = replyFor(michalich)

        then:
        assert reply != null, "reply is null. Vasilich didn't respond for '${michalich}' in ${timeout} seconds"
        if(matcher instanceof String) {
            assert reply == matcher
        } else {
            matcher.call(reply)
        }

        where:
        michalich                   | matcher
        'v launch verbose script'   | { assert it.startsWith('Invisible exception') }
    }

}
