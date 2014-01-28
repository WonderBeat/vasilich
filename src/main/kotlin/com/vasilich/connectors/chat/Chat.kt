package com.vasilich.connectors.chat

/**
 * Basic chat abstraction
 */
public trait Chat<T> {
    fun send(msg: String)
    fun receive(callback: (msg: T) -> Unit)
}
