package com.vasilich.monitoring

import org.junit.Test
import reactor.core.Observable
import com.vasilich.connectors.xmpp.Topics
import com.vasilich.commands.api.Command
import com.vasilich.commands.basic.Ping
import org.mockito.Mockito
import org.mockito.Matchers
import reactor.event.Event
import org.springframework.scheduling.config.ScheduledTaskRegistrar

public class RequestReplyMonitoringRegistarTest {

    Test fun shouldNotifyByTimeoutIfFailed() {
        val mockedReactor = Mockito.mock(javaClass<Observable>())!!
        val scheduler = ScheduledTaskRegistrar()
        val registar = object: RequestReplyScheduledMonitoringRegistar {
            override val scheduler: ScheduledTaskRegistrar = scheduler
            override val cfg: MonitoringCfg = MonitoringCfg(interval = 1, chat =
                listOf(RequestReplyMonitoringCfg("ping", "answer.contains('pong')")))
            override val reactor: Observable = mockedReactor
            override val topics: Topics = Topics()
            override val matchers: Collection<RequestReplyMatcher> = createRequestReplyMatchers(cfg.chat, ::elMatcher)
            override val command: Command = Ping()
        }
        registar.init()
        scheduler.afterPropertiesSet()

        Mockito.verify(mockedReactor, Mockito.timeout(1000))!!.notify(Matchers.anyString(), Matchers.any(javaClass<Event<String>>()))
    }
}
