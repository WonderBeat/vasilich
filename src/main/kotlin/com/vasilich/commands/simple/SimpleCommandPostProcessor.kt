package com.vasilich.commands

import org.springframework.beans.factory.config.BeanPostProcessor
import reactor.core.Observable
import reactor.event.selector.Selectors
import reactor.event.Event
import reactor.function.Consumer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

public class SimpleCommandPostProcessor (val reactor: Observable,
                                         private val appCfg: JsonNode,
                                         private val mapper: ObjectMapper
                                         private val wrapper: (SimpleCommand,
                                                               SimpleCfg) -> SimpleCommand): BeanPostProcessor {

    override fun postProcessBeforeInitialization(bean: Any?, beanName: String?): Any? {
        return bean
    }
    override fun postProcessAfterInitialization(bean: Any?, beanName: String?): Any? {
        return when(bean) {
            is SimpleCommand -> {
                return init(bean)
            }
            else -> bean
        }
    }

    private fun init(bean: SimpleCommand): SimpleCommand {
        val cfg = getCfg(bean.javaClass.getSimpleName())
        if(cfg == null || !cfg.enabled) {
            return bean
        }
        val wrapped = wrapper(bean, cfg)
        makeReactive(wrapped)
        return wrapped
    }

    private fun makeReactive(bean: SimpleCommand) {
        reactor.on(Selectors.`$`("recieve-message"), Consumer<Event<String>> {
            reactor.notify("send-message", Event.wrap(bean.execute(it!!.getData()!!)))
        })
    }

    private fun getCfg(beanName: String): SimpleCfg? {
        val cfgNode = appCfg.get(beanName.toLowerCase().trimTrailing("command"))
        if(cfgNode == null) {
            return null
        }
        return mapper.convertValue(cfgNode, javaClass<SimpleCfg>())!!
    }
}
