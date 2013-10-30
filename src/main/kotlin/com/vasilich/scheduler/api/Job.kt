package com.vasilich.scheduler.api

/**
 * Scheduler job abstraction
 */
public trait Job {

    /**
     * returned string will be sent to the chat
     */
    fun execute(): String
}
