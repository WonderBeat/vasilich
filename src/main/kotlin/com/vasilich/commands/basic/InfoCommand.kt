package com.vasilich.commands.basic

import org.springframework.stereotype.Component
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.vasilich.commands.api.Command
import kotlin.properties.Delegates
import org.springframework.beans.factory.annotation.Autowired

/**
 * Provides information about available commands
 */
Component
public class InfoCommand [Autowired] (config: ObjectNode, mapper: ObjectMapper): Command {

    /**
     * All "descriptions" values from the second layer of config file
     * Example {
     *   ping: {
     *      alias: ["ping"]
     *      description: "This text will be grabbed"
     *   }
     * }
     */
    val descriptions = config.elements()?.filter { it.isObject() }
                                        ?.map { it.get("description") }
                                        ?.filterNotNull()
                                        ?.map { it.textValue() }
                                        ?.makeString("\n")

    override fun execute(msg: String): String? = descriptions
}
