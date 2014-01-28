package app

import org.springframework.context.annotation.Configuration
import reactor.spring.context.config.EnableReactor
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Bean
import reactor.core.Environment
import reactor.core.spec.Reactors
import reactor.core.Observable
import reactor.event.dispatch.ThreadPoolExecutorDispatcher
import com.vasilich.config.JsonBasedConfigPostProcessor
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import com.vasilich.connectors.chat.Chat
import com.vasilich.connectors.xmpp.XmppConf
import com.vasilich.connectors.xmpp.ReactiveChat
import com.vasilich.connectors.chat.FilteredChat
import com.vasilich.connectors.xmpp.createChat
import org.jivesoftware.smack.packet.Message
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.config.BeanPostProcessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.vasilich.config.CommandConfigResolver
import com.vasilich.commands.bootstrap.enableThumblerCommandWrapper
import com.vasilich.commands.bootstrap.ReactiveCommandInitializer
import com.vasilich.commands.api.Command
import com.vasilich.commands.bootstrap.CommandPostProcessor
import com.vasilich.commands.bootstrap.outputMessageWrapper
import com.vasilich.commands.bootstrap.safeCommandWrapper
import com.vasilich.commands.bootstrap.and
import com.vasilich.commands.bootstrap.aliasMatchCommandDetection
import com.vasilich.commands.exec.ShellCommandExecutor
import com.vasilich.commands.exec.VerboseExecuteCfg
import com.vasilich.commands.exec.VerboseShellCommandExecutor
import com.vasilich.commands.exec.createMarkerBasedNotificator
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.AbstractResource
import com.vasilich.commands.chatbot.ChatBotCommand
import org.springframework.context.annotation.Lazy
import com.vasilich.connectors.xmpp.createAliasDetectorFilter
import com.vasilich.connectors.chat.VasilichCfg
import com.vasilich.monitoring.RequestReplyScheduledMonitoringRegistar
import com.vasilich.monitoring.MonitoringCfg
import com.vasilich.connectors.xmpp.Topics
import com.vasilich.monitoring.RequestReplyMatcher
import com.vasilich.monitoring.createRequestReplyMatchers
import com.vasilich.monitoring.elMatcher
import org.springframework.core.Ordered
import com.vasilich.commands.bootstrap.chainCommands
import com.vasilich.monitoring.MonitoringRegistrar
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import java.util.concurrent.TimeUnit
import java.util.Comparator
import com.vasilich.monitoring.stringDistanceComparator

/**
 * Chain of responsibility. First command, that produces output wins
 */
fun chainCommandsByOrder(commands: List<Command>, defaultCommandOrder: Int = 50): Command {
    fun Iterable<Command>.byOrder(): List<Command> = this.sortBy {
        when(it) {
            is Ordered -> it.getOrder()
            else -> defaultCommandOrder
        }
    }
    return commands.byOrder() reduce ::chainCommands
}

Configuration
EnableReactor
ComponentScan(basePackages = array("com.vasilich.commands", "com.vasilich.connectors", "com.vasilich.webhook"))
open public class AppContext {

    Bean open fun appConfig(Autowired mapper: ObjectMapper): JsonNode {
        val configFileName = "config.json"
        var config:AbstractResource = FileSystemResource(configFileName)
        if(!config.exists()) {
            config = ClassPathResource(configFileName)
        }
        return mapper.readTree(config.getFile())!!
    }

    Bean open fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        return mapper
    }

    Bean open fun configReaderPostProcessor(appCfg: JsonNode,
                                            objMapper: ObjectMapper): JsonBasedConfigPostProcessor {
        return JsonBasedConfigPostProcessor(appCfg, objMapper)
    }

    Bean open fun configResolver(appCfg: JsonNode,
                                 mapper: ObjectMapper): CommandConfigResolver {
        return CommandConfigResolver(appCfg, mapper)
    }

    Bean open fun simpleCommandPostProcessor(configResolver: CommandConfigResolver): BeanPostProcessor {
        val wrappers = array(aliasMatchCommandDetection, outputMessageWrapper, safeCommandWrapper)
        return CommandPostProcessor(configResolver,
                wrappers.fold(enableThumblerCommandWrapper, { one, another -> and(one, another) }))
    }

    Bean open fun reactiveCommandInitializer(reactor: Observable, commands: List<Command>): ReactiveCommandInitializer {
        return ReactiveCommandInitializer(reactor, chainCommandsByOrder(commands))
    }

    Bean open fun shellExec(reactor: Observable, cfg: VerboseExecuteCfg): ShellCommandExecutor {
        return VerboseShellCommandExecutor(cfg, createMarkerBasedNotificator(cfg.marker, reactor))
    }

    Bean open fun chat(cfg: XmppConf, vasilichCfg: VasilichCfg, reactor: Observable): Chat<Message> {
        val simpleChat = FilteredChat(createChat(cfg), recieveFilter = createAliasDetectorFilter(vasilichCfg
                .aliases))
        return ReactiveChat(simpleChat, reactor)
    }

    Bean open fun rootReactor(env: Environment): Observable {
        return Reactors.reactor()!!.env(env)!!.dispatcher(ThreadPoolExecutorDispatcher(3, 10))!!.get()!!;
    }

    Bean open fun scheduler(): ScheduledTaskRegistrar = ScheduledTaskRegistrar()

    Bean open fun monitoringCfg(): MonitoringCfg = MonitoringCfg()

    Bean open fun requestReplyMonitoring(cfg : MonitoringCfg, reactor: Observable,
                                         commands: List<Command>, schedulerRegistar: ScheduledTaskRegistrar): MonitoringRegistrar? = when {
        cfg.chat.empty -> null
        else -> object: RequestReplyScheduledMonitoringRegistar {
            override val scheduler: ScheduledTaskRegistrar = schedulerRegistar
            override val cfg: MonitoringCfg = cfg
            override val reactor: Observable = reactor
            override val topics: Topics = Topics()
            override val matchers: Collection<RequestReplyMatcher> = createRequestReplyMatchers(cfg.chat, ::elMatcher)
            override val command: Command = chainCommandsByOrder(commands)
        }
    }

    Lazy
    Bean open fun chatBot(): ChatBotCommand {
        return ChatBotCommand("classpath:/Bots/Alice/*.aiml", "/Bots/context.xml", "/Bots/splitters.xml", "/Bots/substitutions.xml")
    }
}
