package com.vasilich.commands.exec


import com.vasilich.config.Config
import org.springframework.stereotype.Component
import reactor.event.Event
import com.vasilich.connectors.xmpp.Topics
import reactor.core.Observable

Component
Config("exec")
public class MarkerBasedMonitorCfg(val marker: Array<String> = array("VSLC: "))

/**
 * Sends notification in case of line contains marker
 */
fun createMarkerBasedMonitor(markers: Array<String>, reactor: Observable,
                                  topics: Topics = Topics()): (String) -> Unit {
    return {
        it.split("\\n").forEach { line ->
            val marker = markers.find { line.contains(it) }
            if(marker != null) {
                val withoutNotificationMarkers = line.substring(line.indexOf(marker) + marker.length).replaceAll("\r?\n|\r", "").trim()
                reactor.notify(topics.send, Event.wrap(withoutNotificationMarkers))
            }
        }
    }
}

/**
 * Ansible output monitor
 */
fun createAnsibleMonitor(reactor: Observable,
                         topics: Topics = Topics()): (String) -> Unit {
    return {
        it.split("\\n").forEach { line ->
            if(line.isNotEmpty()) {
                reactor.notify(topics.send, Event.wrap(line))
            }
        }
    }
}
