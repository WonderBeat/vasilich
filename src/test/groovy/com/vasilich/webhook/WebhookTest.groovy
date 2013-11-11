package com.vasilich.webhook
import io.netty.buffer.ByteBufHolder
import io.netty.buffer.ByteBufOutputStream
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.HttpRequest
import jet.Function1
import spock.lang.Specification

class WebhookTest extends Specification {

    interface HttpWithContent extends HttpRequest, ByteBufHolder {}

    def 'Webhook listener should execute processor if conditions are met'() {

        given:
        DefaultHttpHeaders headers = new DefaultHttpHeaders()
        headers.add('Cookie', '123')
        headers.add('Host', 'porn.com')
        HttpWithContent request = Mock()
        request.getUri() >> '/top10'
        request.headers() >> headers

        ByteBufOutputStream stream = new ByteBufOutputStream(Unpooled.buffer(100));
        stream.writeBytes("last night a DJ saved my life with this song")

        request.content() >> stream.buffer()
        Function1 processor = Mock()
        def listener = WebhookPackage.createWebhookConsumer(conditions, [processor])

        when:
        listener.accept(request)

        then:
        processorCall * processor.invoke(processorInput)

        where:
        conditions          |   processorCall    |  processorInput
        [ Cookie: 'abc' ] |     0      |     []
        [ Host: 'porn.com' ] |  1       |     ['porn.com']
        [ body: 'regexp: (\\bDJ\\b)' ] |  1       |     ['DJ']
        [ body: 'regexp: (\\bDJ\\b)' ] |  1       |     ['DJ']
        [ body: 'regexp: (\\bDJ\\b)', Cache: 'none' ] | 0 | [] // second condition will fail
        [ body: 'regexp: (\\bDJ\\b)', Cookie: '123' ] | 1 | ['DJ', '123']
        [ uri: '/top10', body: 'regexp: (\\bDJ\\b)' ] |  1       |     ['/top10', 'DJ']
        [ uri: '/top10', body: 'regexp: (\\bDJ\\b)', Host: 'porn.com'] |  1       |     ['/top10', 'DJ', 'porn.com']
    }
}
