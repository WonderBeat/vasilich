package com.vasilich.commands.examples

import com.vasilich.commands.Command
import org.springframework.stereotype.Component
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired

/**
 * Provides information about available commands
 */
Component
public class InfoCommand [Autowired] (config: ObjectNode, mapper: ObjectMapper): Command {

    /**
     * All "descriptions" values from the second layer of config file
     * Example {
     *   one: {
     *      description: "This text will be grabbed"
     *   }
     * }
     */
    val descriptions = config.elements()?.filter { it.isObject() }
                                        ?.map { it.get("description") }
                                        ?.filterNotNull()
                                        ?.map { it.textValue() }
                                        ?.makeString("\n")

    override fun execute(msg: String): String? {
        return descriptions
    }
}
