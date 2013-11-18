package com.vasilich.monitoring

import org.junit.Test
import com.vasilich.commands.basic.Ping
import org.junit.Assert
import org.hamcrest.CoreMatchers

class RequestReplyMonitorTest {

    Test fun monitoringShouldReturnNothingIfOK() {
        val monitoring = object: RequestReplyMonitor {
            override val matchers = createRequestReplyMatchers(
                    listOf(RequestReplyMonitoringCfg("ping", "answer.isEmpty()", "ping should reply with pong")),
                    ::elMatcher)
            override val command = Ping()
        }
        Assert.assertThat(monitoring.check().size, CoreMatchers.`is`(0))
    }

    Test fun monitoringShouldReturnExplanationIfFailed() {
        val monitoring = object: RequestReplyMonitor {
            override val matchers = createRequestReplyMatchers(
                    listOf(RequestReplyMonitoringCfg("ping", "!answer.isEmpty()", "ping should reply with pong")),
                    ::elMatcher)
            override val command = Ping()
        }
        Assert.assertThat(monitoring.check().size, CoreMatchers.`is`(1))
    }


}
