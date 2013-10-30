package com.vasilich.config

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.vasilich.commands.CommandCfg
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired

/**
 * Provides CommandCfg object for node
 */
public class CommandConfigResolver [Autowired] (private val appCfg: JsonNode,
                                   private val mapper: ObjectMapper) {

    /**
     * Resolves configuration node
     */
    fun config(name: String): CommandCfg? {
        val cfgNode = appCfg.get(name)
        if(cfgNode == null) {
            return null
        }
        return mapper.convertValue(cfgNode, javaClass<CommandCfg>())!!
    }
}

