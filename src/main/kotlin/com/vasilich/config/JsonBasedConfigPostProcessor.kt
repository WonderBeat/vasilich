package com.vasilich.config

import org.springframework.beans.factory.config.BeanPostProcessor
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Retention
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.core.PriorityOrdered
import org.springframework.core.Ordered
import com.fasterxml.jackson.databind.JsonNode

Retention(RetentionPolicy.RUNTIME) annotation class Config(val value: String)

/**
 * Provides configuration beans
 * Works for "Config" annotated classes
 *
 * Env object, that are instances of "@Config" annotated class will be replaced with filled once if config node is found
 */
public class JsonBasedConfigPostProcessor(private val appCfg: JsonNode,
                                 private val jsonMapper: ObjectMapper): BeanPostProcessor, PriorityOrdered {

    override fun postProcessBeforeInitialization(p0: Any?, p1: String?): Any? {
        return p0
    }

    override fun postProcessAfterInitialization(p0: Any?, p1: String?): Any? {
        val annotation = p0.javaClass.getAnnotation(javaClass<Config>())
        if(annotation == null) {
            return p0
        }
        val node = appCfg.get(annotation.value)
        val obj = jsonMapper.convertValue(node, javaClass<ObjectNode>())
        if(obj == null) {
            return p0
        }
        return jsonMapper.readValue(obj.traverse(), p0!!.javaClass)
    }

    override fun getOrder(): Int {
        return Ordered.HIGHEST_PRECEDENCE
    }
}
