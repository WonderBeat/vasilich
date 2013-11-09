package com.vasilich.webhook.processors

import reactor.core.Observable
import reactor.event.Event
import java.text.MessageFormat

fun createNotificationProcessor(reactor: Observable, topic: String, template: String): (Collection<String>) -> Unit = {
    reactor.notify(topic, Event.wrap(MessageFormat.format(template, *it.copyToArray())))
}
