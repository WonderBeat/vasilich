package com.vasilich.commands.bootstrap

import org.springframework.beans.factory.config.BeanPostProcessor
import org.slf4j.LoggerFactory
import com.vasilich.config.CommandConfigResolver
import com.vasilich.commands.api.NoopCommand
import com.vasilich.commands.api.Command

public class CommandCfg(val enabled: Boolean = true,
                        val aliases: Array<String> = array(),
                        val output: String = "")

fun getNodeName(input: String) : String =
    input.fold("", { akk, data -> when {
        akk.isEmpty() -> akk + data.toString().toLowerCase()
        data.isUpperCase() -> akk + "-" + data.toString().toLowerCase()
        else -> akk + data
    }
})

public class CommandPostProcessor (private val cfgProvider: CommandConfigResolver,
                                   private val wrapper: (Command, CommandCfg) -> Command): BeanPostProcessor {

    val logger = LoggerFactory.getLogger(javaClass<CommandPostProcessor>())!!;

    override fun postProcessBeforeInitialization(bean: Any?, beanName: String?): Any? = bean

    override fun postProcessAfterInitialization(bean: Any?, beanName: String?): Any? = when(bean) {
        is Command -> init(bean)
        else -> bean
    }

    private fun init(bean: Command): Command {
        val cfg = cfgProvider.config(getNodeName(bean.javaClass.getSimpleName().trimTrailing("Command")));
        return if(cfg == null) NoopCommand else wrapper(bean, cfg)
    }
}
