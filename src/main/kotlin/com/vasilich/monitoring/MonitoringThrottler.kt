package com.vasilich.monitoring

import com.google.common.cache.Cache
import java.util.HashMap
import org.apache.commons.lang3.StringUtils

/**
 * Monitoring can be very importunate
 * With this trait we can reduce it verbosity
 */
public trait MonitoringThrottler: CommandMonitoring {
    // time eviction is preferable
    val throttlerCache: Cache<String, Unit>

    val criticalDistance: Int

    override fun check(): Collection<String> {
        val answers = super<CommandMonitoring>.check()

        val newAlerts = answers.filter { answer ->
                throttlerCache.asMap()!!.keySet().any { StringUtils.getLevenshteinDistance(answer, it) > criticalDistance } }
        val newAlertsMap = newAlerts.fold(HashMap<String, Unit>(), {acc, item -> acc.put(item, Unit.VALUE); acc; })
        throttlerCache.putAll(newAlertsMap)
        return newAlerts
    }

}
