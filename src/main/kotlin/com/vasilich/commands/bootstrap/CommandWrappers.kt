package com.vasilich.commands

import java.text.MessageFormat
import com.vasilich.commands.api.NoopCommand

public fun and(one: (Command, CommandCfg) -> Command,
               two: (Command, CommandCfg) -> Command):
                            (Command, CommandCfg) -> Command {
    return { cmd, cfg -> one(two(cmd, cfg), cfg)}
}

/**
 * Enable/Disable config functionality
 */
public fun enableThumblerCommandWrapper(): (Command, CommandCfg) -> Command {
    return { cmd, cfg ->
            when {
                cfg.enabled -> cmd
                else -> NoopCommand
            }
        }
}

/**
 * Command will be triggered only if message contains one of aliases
 */
public fun aliasMatchCommandDetection(): (Command, CommandCfg) -> Command {
    return { cmd, cfg ->
        object: Command {
        override fun execute(msg: String): String? = when {
            cfg.aliases.isEmpty() || cfg.aliases.any { msg.contains(it) } -> cmd.execute(msg)
            else -> null
        }
    }}
}

/**
 * Formats output message
 * Based on configuration string
 */
public fun outputMessageWrapper(): (Command, CommandCfg) -> Command {
    return { cmd, cfg ->
        object: Command {
            override fun execute(msg: String): String? = when {
                !cfg.output.isEmpty() -> MessageFormat.format(cfg.output, cmd.execute(msg))
                else -> cmd.execute(msg)
                }
        }}
}

/**
 * If first command doesn't resolve, then triggers another one
 */
public fun chainCommands(one: Command, another: Command): Command {
    return object: Command {
            override fun execute(msg: String): String? {
                val result = one.execute(msg)
                when(result) {
                    null -> another.execute(msg)
                    else -> result
                }
            }
    }
}
