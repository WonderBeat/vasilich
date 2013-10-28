package com.vasilich.connectors.chat

public class FilteredChat<T>(private val delegate: Chat<T>,
                             private val sendFilter: (String) -> Boolean = {true},
                             private val recieveFilter: (T) -> Boolean = {true} ): Chat<T> {

    override fun send(msg: String) {
        if(sendFilter(msg)) {
            delegate.send(msg)
        }
    }

    override fun recieve(callback: (msg: T) -> Unit) {
        delegate.recieve {
                if(recieveFilter(it)) {
                    callback(it)
                }
        }
    }
    
}
