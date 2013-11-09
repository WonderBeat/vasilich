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

    override fun postProcessBeforeInitialization(bean: Any?, beanName: String?): Any? {
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any?, beanName: String?): Any? {
        val annotation = bean.javaClass.getAnnotation(javaClass<Config>())
        if(annotation == null) {
            return bean
        }
        val node = appCfg.get(annotation.value)
        val obj = jsonMapper.convertValue(node, javaClass<ObjectNode>())
        if(obj == null) {
            return bean
        }
        return jsonMapper.readValue(obj.traverse(), bean!!.javaClass)
    }

    override fun getOrder(): Int {
        return Ordered.HIGHEST_PRECEDENCE
    }
}
