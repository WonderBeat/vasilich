package com.vasilich.monitoring

import org.springframework.scheduling.config.ScheduledTaskRegistrar
import reactor.core.Observable
import com.vasilich.connectors.xmpp.Topics
import reactor.event.Event
import javax.annotation.PostConstruct

trait MonitoringRegistrar {

    val cfg: MonitoringCfg
    val reactor: Observable
    val topics: Topics

    fun init() {
    }
}


trait RequestReplyScheduledMonitoringRegistar: MonitoringRegistrar, RequestReplyMonitor {

    val scheduler: ScheduledTaskRegistrar

    PostConstruct
    override fun init() {
        scheduler.addFixedRateTask(Runnable{ checkAndReportFailures() } , (cfg.interval * 1000).toLong())
        super<MonitoringRegistrar>.init()
    }

    private fun checkAndReportFailures() = check().forEach { reactor.notify(topics.send, Event.wrap(it)) }
}
