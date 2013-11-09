package functional.webhook
import app.WebServerContext
import com.vasilich.connectors.xmpp.Topics
import com.vasilich.webhook.WebhookCfg
import com.vasilich.webhook.WebhookCommand
import org.apache.http.HttpHost
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import reactor.core.Environment
import reactor.core.composable.spec.Promises
import reactor.core.spec.Reactors
import reactor.event.Event
import reactor.event.dispatch.ThreadPoolExecutorDispatcher
import reactor.event.selector.Selectors
import reactor.function.Consumer
import reactor.spring.context.config.EnableReactor
import spock.lang.Specification

import java.util.concurrent.TimeUnit

@Configuration
@EnableReactor
class TestSpecificConfig {

    @Bean
    reactor.core.Observable rootReactor(Environment env)  {
        return Reactors.reactor().env(env).dispatcher(new ThreadPoolExecutorDispatcher(3, 10)).get()
    }
    @Bean
    WebhookCfg config() {
        new WebhookCfg(true, 1234, '127.0.0.1', [
                new WebhookCommand([body: "regexp: revolution (.+)"], "", "our goal is {0}"),
        ] as WebhookCommand[])
    }
}

@ContextConfiguration(classes = [TestSpecificConfig, WebServerContext])
class WebhookTriggerTest extends Specification {

    @Autowired
    reactor.core.Observable observer

    def host = new HttpHost('127.0.0.1', 1234)

    def client = new DefaultHttpClient()

    String sendAndWait(HttpPost post) {
        def promise = Promises.defer().get()
        observer.on(Selectors.$(new Topics().send), { value ->
            promise.accept(value.getData())
        } as Consumer<Event<String>>)
        client.execute(host, post)
        promise.compose().await(1, TimeUnit.SECONDS)
    }


    def 'should notify about webhook if request payload matches condition from configuration'() {

        given:
        HttpPost post = new HttpPost('/somepath')
        post.setEntity(new StringEntity(body))
        client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false))

        when:
        def receivedNotification = sendAndWait(post)

        then:
        assert receivedNotification == notification

        where:
        body | notification
        'adolf' | null
        'Lenin revolution COMMUNISM' | 'our goal is COMMUNISM'
    }

}
