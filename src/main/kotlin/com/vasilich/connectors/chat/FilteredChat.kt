package com.vasilich.connectors.chat

import org.jivesoftware.smack.packet.Message
import com.vasilich.config.Config
import org.springframework.stereotype.Component

object NopMessage: Message()

Component
Config("vasilich")
class VasilichCfg(val aliases: Array<String> = array("vasilich", "vslc", "v"))

/**
 * Chat is too verbose. By default we will be triggered [receive] even if the message is ours
 * There is a need to filter useless messages
 */
public class FilteredChat<T>(private val delegate: Chat<T>,
                             private val sendFilter: (String) -> Boolean = {true},
                             private val recieveFilter: (T) -> T): Chat<T> by delegate {

    override fun send(msg: String) {
        if(sendFilter(msg)) {
            delegate.send(msg)
        }
    }

    override fun receive(callback: (msg: T) -> Unit) =
            delegate.receive {
                val filteredMsg = recieveFilter(it)
                if(filteredMsg !is NopMessage) {
                    callback(filteredMsg)
                }
            }
    
}
