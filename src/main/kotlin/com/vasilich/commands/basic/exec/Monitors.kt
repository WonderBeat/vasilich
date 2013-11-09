package com.vasilich.commands.basic.exec

import reactor.event.Event
import com.vasilich.connectors.xmpp.Topics
import reactor.core.Observable

/**
 * Sends notification in case of line contains marker
 */
fun createMarkerBasedNotificator(markers: Array<String>, reactor: Observable,
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
