package com.vasilich.config

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.vasilich.commands.bootstrap.CommandCfg
import org.springframework.beans.factory.annotation.Autowired

/**
 * Provides CommandCfg object for node
 * Example:
 * {
 *  'ping': {
 *          alias: pong,
 *          data: 1
 *      }
 * }
 * Resolver tries to convert this node into CommandCfg object
 */
public class CommandConfigResolver [Autowired] (
        private val appCfg: JsonNode,
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

