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
import com.vasilich.commands.bootstrap.and
import com.vasilich.commands.bootstrap.aliasMatchCommandDetection
import com.vasilich.commands.basic.exec.ShellCommandExecutor
import com.vasilich.commands.basic.exec.VerboseExecuteCfg
import com.vasilich.commands.basic.exec.VerboseShellCommandExecutor
import com.vasilich.commands.basic.exec.createMarkerBasedNotificator
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.AbstractResource
import com.vasilich.commands.chatbot.ChatBotCommand
import com.vasilich.commands.chatbot.loadAimsFromClasspath
import com.vasilich.commands.chatbot.ChatBotLoader
import java.io.ByteArrayOutputStream
import org.springframework.context.annotation.Lazy
import com.vasilich.connectors.xmpp.createAliasDetectorFilter
import com.vasilich.connectors.chat.VasilichCfg
import java.util.Observer

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
        val wrappers = array(aliasMatchCommandDetection, outputMessageWrapper)
        return CommandPostProcessor(configResolver,
                wrappers.fold(enableThumblerCommandWrapper, { one, another -> and(one, another) }))
    }

    Bean open fun reactiveCommandInitializer(reactor: Observable, commands: List<Command>): ReactiveCommandInitializer {
        return ReactiveCommandInitializer(reactor, commands)
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

    Lazy
    Bean open fun chatBot(): ChatBotCommand {
        val aimlResources = loadAimsFromClasspath("classpath:/Bots/Alice/*.aiml")
        val bot = ChatBotLoader.createBot(
                ClassPathResource("/Bots/context.xml").getInputStream(),
                ClassPathResource("/Bots/splitters.xml").getInputStream(),
                ClassPathResource("/Bots/substitutions.xml").getInputStream(), aimlResources)
        val context = bot!!.getContext();
        val gossip = ByteArrayOutputStream()
        context!!.outputStream(gossip);


        return ChatBotCommand(bot)
    }

}
