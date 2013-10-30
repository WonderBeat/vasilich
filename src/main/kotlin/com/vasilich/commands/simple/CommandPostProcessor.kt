package com.vasilich.commands

import org.springframework.beans.factory.config.BeanPostProcessor
import reactor.core.Observable
import reactor.event.selector.Selectors
import reactor.event.Event
import reactor.function.Consumer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

public class CommandPostProcessor (private val appCfg: JsonNode,
                                   private val mapper: ObjectMapper
                                   private val wrapper: (Command,SimpleCfg) -> Command): BeanPostProcessor {

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

    private fun getCfg(beanName: String): SimpleCfg? {
        val cfgNode = appCfg.get(beanName.toLowerCase().trimTrailing("command"))
        if(cfgNode == null) {
            return null
        }
        return mapper.convertValue(cfgNode, javaClass<SimpleCfg>())!!
    }
}
