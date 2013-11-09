package app

import org.springframework.context.annotation.Configuration
import reactor.core.Environment
import com.vasilich.webhook.WebhookCfg
import reactor.tcp.spec.TcpServerSpec
import reactor.tcp.netty.NettyTcpServer
import reactor.function.Consumer
import reactor.tcp.TcpConnection
import reactor.core.Observable
import org.springframework.context.annotation.Bean
import reactor.tcp.TcpServer
import io.netty.handler.codec.http.HttpResponse
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.HttpVersion
import io.netty.handler.codec.http.HttpResponseStatus
import reactor.tcp.netty.NettyServerSocketOptions
import io.netty.channel.ChannelPipeline
import com.vasilich.webhook.createWebhookConsumer
import com.vasilich.webhook.createProcessors
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import org.springframework.context.annotation.ComponentScan

Configuration
public open class WebServerContext {

    Bean
    open fun webhookTcpServer(cfg: WebhookCfg, reactor: Observable, env: Environment):
                                        TcpServer<FullHttpRequest,HttpResponse>? {
        if(!cfg.enabled) {
            return null
        }
        val consumers = cfg.commands.map { createWebhookConsumer(it.conditions,
                                                createProcessors(reactor, it.notify, it.trigger)) }

        var server = TcpServerSpec<FullHttpRequest, HttpResponse>(javaClass<NettyTcpServer<FullHttpRequest, HttpResponse>>())
                .listen(cfg.address, cfg.port)!!
                .env(env)!!
                .options(NettyServerSocketOptions().pipelineConfigurer(Consumer<ChannelPipeline> {
                    it!!.addLast(HttpServerCodec())
                    it.addLast(HttpObjectAggregator(Integer.MAX_VALUE))
                }))
                ?.consume(Consumer<TcpConnection<FullHttpRequest, HttpResponse>>() { conn ->
                    conn!!.`in`()!!.consume(Consumer<FullHttpRequest> { request ->
                        consumers.forEach { it.accept(request)  }
                        conn.send(DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT))
                        conn.close()
                    })
                })!!.get()!!
        server.start()
        return server
    }
}
