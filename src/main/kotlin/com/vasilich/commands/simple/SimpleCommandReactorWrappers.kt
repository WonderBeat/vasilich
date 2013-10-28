package com.vasilich.commands

import java.text.MessageFormat

public class SimpleCfg(val enabled: Boolean = false,
                       val aliases: Array<String> = array(),
                       val output: String = "")

public fun and(one: (SimpleCommand, SimpleCfg) -> SimpleCommand,
               two: (SimpleCommand,SimpleCfg) -> SimpleCommand):
                            (SimpleCommand,SimpleCfg) -> SimpleCommand {
    return { cmd, cfg -> one(two(cmd, cfg), cfg)}
}

/**
 * Command will be triggered only if message contains one of aliases
 */
public fun aliasMatchCommandDetection(): (SimpleCommand, SimpleCfg) -> SimpleCommand {
    return { cmd, cfg ->
        object: SimpleCommand {
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
public fun outputMessageWrapper(): (SimpleCommand, SimpleCfg) -> SimpleCommand {
    return { cmd, cfg ->
        object: SimpleCommand {
            override fun execute(msg: String): String? {
                if(!cfg.output.isEmpty()) {
                    return MessageFormat.format(cfg.output, cmd.execute(msg))
                } else {
                    return cmd.execute(msg)
                }
            }
        }}
}
