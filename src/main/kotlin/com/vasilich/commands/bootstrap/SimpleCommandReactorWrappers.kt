package com.vasilich.commands

import java.text.MessageFormat

public class CommandCfg(val enabled: Boolean = false,
                       val aliases: Array<String> = array(),
                       val output: String = "")

object NoopCommand: Command {
    override fun execute(msg: String): String? {
        return null
    }
}

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
        override fun execute(msg: String): String? {
            if(cfg.aliases.isEmpty() || cfg.aliases.any { msg.contains(it) }) {
                return cmd.execute(msg)
            } else {
                return null
            }
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
            override fun execute(msg: String): String? {
                if(!cfg.output.isEmpty()) {
                    return MessageFormat.format(cfg.output, cmd.execute(msg))
                } else {
                    return cmd.execute(msg)
                }
            }
        }}
}
