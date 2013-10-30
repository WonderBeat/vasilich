package com.vasilich.commands

/**
 * Command accepts chat input message and should provide an answer (or 'null')
 * Simple configuration options are provided for simple command out of the box.
 * Example: 'enabled' - if command enabled,
 *          'aliases' - command triggers,
 *          'output' - output formatting
 *
 * Json configuration node should be the same as a class name
 * Example:
 *  CurrentTime -> currenttime json node will be used for this command
 *
 *
 */
public trait Command {
    fun execute(msg: String): String?
}
