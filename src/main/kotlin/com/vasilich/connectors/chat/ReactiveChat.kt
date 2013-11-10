package com.vasilich.connectors.xmpp

import reactor.event.Event
import org.jivesoftware.smack.packet.Message
import reactor.core.Observable
import reactor.event.selector.Selectors
import reactor.function.Consumer
import com.vasilich.connectors.chat.Chat

public class Topics(val send: String = "send-message", val receive: String = "receive-message")

/**
 * This class can be used to add reactive behaviour to Chat instance
 */
public class ReactiveChat(private val chat: Chat<Message>,
                         val reactor: Observable,
                         private val topics: Topics = Topics()): Chat<Message> by chat {
    {
        reactor.on(Selectors.`$`(topics.send), Consumer<Event<String>> {
            chat.send(it!!.getData()!!)
        })

        chat.recieve {
            reactor.notify(topics.receive, Event.wrap(it.getBody()))
        }
    }

}
