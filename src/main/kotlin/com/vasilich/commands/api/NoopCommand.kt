package com.vasilich.commands.api

import com.vasilich.commands.Command

/**
 * Empty command
 */
object NoopCommand: Command {
    override fun execute(msg: String): String? {
        return null
    }
}
