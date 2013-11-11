package com.vasilich.config.listener

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import com.vasilich.config.Config
import com.vasilich.connectors.xmpp.ReactiveChat
import reactor.core.Observable
import com.vasilich.connectors.xmpp.XmppConf
import org.jivesoftware.smack.packet.Message
import com.vasilich.connectors.xmpp.Topics
import org.springframework.beans.factory.config.BeanPostProcessor
import com.vasilich.connectors.xmpp.XmppRoomCfg
import com.vasilich.connectors.chat.FilteredChat
import com.vasilich.connectors.xmpp.createChat
import reactor.core.Reactor
import com.vasilich.connectors.chat.NopMessage
import functional.listener.ScriptExtensionPostProcessor

Config("listener")
class XmppLisenerCfg(val login: String = "", val password: String = "", val nick: String = "")

Configuration
public open class ListenerContext {

    /**
     * Michalich. Another bot, that will chat with Vasilich and asks everything about Vasilich knows ;)
     * He will login into the same room, but with different nick
     */
    Bean open fun chatListener(observable: Observable,
                               listenerCredentials: XmppLisenerCfg,
                               xmppCfg: XmppConf,
                               reactor: Reactor): ReactiveChat {

        val roomCfg = XmppRoomCfg(xmppCfg.room.id, listenerCredentials.nick, xmppCfg.room.password)
        val listenerConfig = XmppConf(xmppCfg.server, listenerCredentials.login,
                                      listenerCredentials.password, roomCfg)
        fun listeningForVasilichAliasResolver(): (Message) -> Message {
            return { (msg: Message) ->
                when(msg.getFrom()!!.contains(xmppCfg.room.username)) {
                    true -> msg
                    else -> NopMessage
                }
            }

        }
        val simpleChat = FilteredChat(createChat(listenerConfig), recieveFilter = listeningForVasilichAliasResolver())
        return ReactiveChat(simpleChat, reactor, Topics("test-send", "test-recieve"))
    }

    /**
     * Let's randomize test room, because we don't want to interfere with another test executions, that
     * could be launched the same time
     */
    Bean open fun roomRandomizerPostProcessor(): BeanPostProcessor {
        return RoomRandomizerPostProcessor()
    }

    /**
     * Tests can be launched in Win and Linux
     * We should pick a proper script
     */
    Bean open fun scriptExtensionPostProcessor(): BeanPostProcessor {
        return ScriptExtensionPostProcessor()
    }

    Bean open fun cfg(): XmppLisenerCfg {
        return XmppLisenerCfg()
    }
}
