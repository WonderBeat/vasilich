package com.vasilich.commands.api

/**
 * Empty command
 */
object NoopCommand: Command {
    override fun execute(msg: String): String? {
        return null
    }
}
