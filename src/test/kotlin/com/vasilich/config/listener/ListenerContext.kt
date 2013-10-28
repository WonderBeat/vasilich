package com.vasilich.config.listener

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import com.vasilich.config.Config
import org.springframework.beans.factory.annotation.Autowired
import com.vasilich.connectors.xmpp.ReactiveChat
import reactor.core.Observable
import com.vasilich.connectors.xmpp.XmppConf
import com.vasilich.connectors.xmpp.XmppChat
import org.jivesoftware.smack.packet.Message
import com.vasilich.connectors.xmpp.Topics
import org.springframework.beans.factory.config.BeanPostProcessor
import com.vasilich.connectors.xmpp.XmppRoomCfg
import com.vasilich.connectors.chat.FilteredChat
import com.vasilich.connectors.xmpp.createChat
import reactor.core.Reactor

Config("listener")
class XmppLisenerCfg(val login: String = "", val password: String = "", val nick: String = "")

Configuration
public open class ListenerContext {

    Bean open fun chatListener(Autowired observable: Observable,
                                 Autowired listenerCredentials: XmppLisenerCfg,
                                 Autowired xmppCfg: XmppConf,
                                 Autowired reactor: Reactor): ReactiveChat {

        val roomCfg = XmppRoomCfg(xmppCfg.room.id, listenerCredentials.nick, xmppCfg.room.password)
        val listenerConfig = XmppConf(xmppCfg.server, listenerCredentials.login,
                                      listenerCredentials.password, roomCfg)
        fun listeningForVasilichAliasResolver(): (Message) -> Boolean {
            return { (msg: Message) ->
                msg.getFrom()!!.contains(xmppCfg.room.username) }
        }
        val simpleChat = FilteredChat(createChat(listenerConfig), recieveFilter = listeningForVasilichAliasResolver())
        return ReactiveChat(simpleChat, reactor, Topics("test-send", "test-recieve"))
    }

    Bean open fun roomRandomizerPostProcessor(): BeanPostProcessor {
        return RoomRandomizerPostProcessor()
    }

    Bean open fun cfg(): XmppLisenerCfg {
        return XmppLisenerCfg()
    }
}
