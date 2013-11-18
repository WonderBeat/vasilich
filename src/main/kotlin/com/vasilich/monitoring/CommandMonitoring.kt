package com.vasilich.monitoring

import com.vasilich.commands.api.Command
import com.vasilich.config.Config
import org.springframework.stereotype.Component

Component
Config("monitoring")
public class MonitoringCfg(val interval: Int = 10, val chat: Collection<RequestReplyMonitoringCfg> = listOf())

public class RequestReplyMonitoringCfg(val say: String = "", val reply: String = "",
                                       val explanation: String = "assertion failed: ${reply}")

public trait CommandMonitoring {
    val command: Command

    fun check(): Collection<String> = listOf()
}
