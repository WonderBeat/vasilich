package com.vasilich.connectors.xmpp

import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smackx.muc.MultiUserChat
import org.springframework.stereotype.Component
import com.vasilich.config.Config
import com.vasilich.connectors.chat.Chat
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.PacketListener
import com.vasilich.connectors.chat.NopMessage
import org.springframework.beans.factory.annotation.Autowired

public class XmppRoomCfg(val id: String = "", val username: String = "", val password: String = "")


/**
 * XMPP chat implementation.
 * Creates connection and enters chat room
 */
Component
Config("xmpp")
public class XmppConf(val server: String = "",
                      val login: String = "",
                      val password: String = "",
                      val room: XmppRoomCfg = XmppRoomCfg("", "", ""))

fun createAliasDetectorFilter(aliases: Array<String>): (Message) -> Message {
    val aliasesLowerCase = aliases.map { it.toLowerCase() }
    return {
        val firstWord = it.getBody()?.split(" ")?.get(0)?.toLowerCase()
        val matchedAlias = aliasesLowerCase.find { alias -> firstWord?.startsWith(alias) }
        when(matchedAlias) {
            null -> NopMessage
            else -> { it.setBody(it.getBody()?.substring(matchedAlias.length)?.trim()); it}
        }
    }
}

fun createChat(cfg: XmppConf): XmppChat {
    val connection = XMPPConnection(cfg.server)
    connection.connect()
    connection.login(cfg.login, cfg.password)
    val chat = MultiUserChat(connection, cfg.room.id)
    chat.join(cfg.room.username, cfg.room.password)
    return XmppChat(chat)
}

public class XmppChat [Autowired] (private val chat: MultiUserChat): Chat<Message> {

    override fun send(msg: String) {
        chat.sendMessage(msg)
    }
    override fun recieve(callback: (Message) -> Unit) {
        chat.addMessageListener(PacketListener {
            when(it) {
                is Message -> callback(it)
            }
        })
    }
}
