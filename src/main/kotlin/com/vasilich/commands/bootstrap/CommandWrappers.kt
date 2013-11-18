package com.vasilich.commands.bootstrap

import java.text.MessageFormat
import com.vasilich.commands.api.NoopCommand
import com.vasilich.commands.api.Command

public fun and(one: (Command, CommandCfg) -> Command,
               two: (Command, CommandCfg) -> Command):
                            (Command, CommandCfg) -> Command {
    return { cmd, cfg -> one(two(cmd, cfg), cfg)}
}

/**
 * Enable/Disable config functionality
 */
public val enableThumblerCommandWrapper: (Command, CommandCfg) -> Command =
    { cmd, cfg ->
        when {
            cfg.enabled -> cmd
            else -> NoopCommand
        }
    }

/**
 * Command will be triggered only if message contains one of aliases
 */
public val aliasMatchCommandDetection: (Command, CommandCfg) -> Command =
    { cmd, cfg ->
        object: Command {
            override fun execute(msg: String): String? = when {
                cfg.aliases.isEmpty() || cfg.aliases.any { msg.contains(it) } -> cmd.execute(msg)
                else -> null
            }
        }
    }

/**
 * Formats output message
 * Based on configuration string
 */
public val outputMessageWrapper: (Command, CommandCfg) -> Command =
    { cmd, cfg ->
        object: Command {
            override fun execute(msg: String): String? = when {
                !cfg.output.isEmpty() -> MessageFormat.format(cfg.output, cmd.execute(msg))
                else -> cmd.execute(msg)
                }
        }
    }

public val safeCommandWrapper: (Command, CommandCfg) -> Command =
    { command, cfg ->
        object: Command {
            override fun execute(msg: String): String? {
                try {
                    return command.execute(msg)
                } catch(exception: Exception) {
                    return MessageFormat.format(cfg.fail, exception.getMessage())
                }
            }
        }
    }

/**
 * If first command doesn't resolve, then triggers another one
 */
public fun chainCommands(one: Command, another: Command): Command =
    object: Command {
        override fun execute(msg: String): String? {
            val result = one.execute(msg)
            when(result) {
                null -> another.execute(msg)
                else -> result
            }
        }
    }
