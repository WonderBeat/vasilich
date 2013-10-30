package com.vasilich.commands

import org.springframework.beans.factory.config.BeanPostProcessor
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory

public class CommandPostProcessor (private val appCfg: JsonNode,
                                   private val mapper: ObjectMapper
                                   private val wrapper: (Command, CommandCfg) -> Command): BeanPostProcessor {

    val logger = LoggerFactory.getLogger(javaClass<CommandPostProcessor>())!!;

    override fun postProcessBeforeInitialization(bean: Any?, beanName: String?): Any? {
        return bean
    }
    override fun postProcessAfterInitialization(bean: Any?, beanName: String?): Any? {
        return when(bean) {
            is Command -> {
                return init(bean)
            }
            else -> bean
        }
    }

    private fun init(bean: Command): Command {
        val cfg = getCfg(bean.javaClass.getSimpleName())
        return if(cfg == null) NoopCommand else wrapper(bean, cfg)
    }

    private fun getCfg(beanName: String): CommandCfg? {
        val cfgNode = appCfg.get(beanName.toLowerCase().trimTrailing("command"))
        if(cfgNode == null) {
            return null
        }
        return mapper.convertValue(cfgNode, javaClass<CommandCfg>())!!
    }
}
