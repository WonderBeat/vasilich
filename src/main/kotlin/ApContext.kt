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

class CommunicationTopics(val send: String = "send-message", val receive: String = "receive-message")

Configuration
EnableReactor
ComponentScan(basePackages = array("com.vasilich.commands", "com.vasilich.connectors.xmpp"))
open public class AppContext {

    Bean open fun appConfig(Autowired mapper: ObjectMapper): JsonNode {
        return mapper.readTree(ClassPathResource("config.json").getFile()!!)!!
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
        val wrappers = array(aliasMatchCommandDetection(), outputMessageWrapper())
        return CommandPostProcessor(configResolver,
                wrappers.fold(enableThumblerCommandWrapper(), { one, another -> and(one, another) }))
    }

    Bean open fun reactiveCommandInitializer(reactor: Observable, commands: List<Command>): ReactiveCommandInitializer {
        return ReactiveCommandInitializer(reactor, commands)
    }

    Bean open fun shellExec(reactor: Observable, cfg: VerboseExecuteCfg): ShellCommandExecutor {
        return VerboseShellCommandExecutor(reactor = reactor, cfg = cfg)
    }

    Bean open fun chat(cfg: XmppConf, reactor: Observable): Chat<Message> {
        fun usernameInputMessageFilter(username: String): (Message) -> Boolean {
            return { (msg: Message) -> msg.getBody()!!.startsWith(username) }
        }
        val simpleChat = FilteredChat(createChat(cfg), recieveFilter = usernameInputMessageFilter(cfg.room.username))
        return ReactiveChat(simpleChat, reactor)
    }

    Bean open fun rootReactor(env: Environment): Observable {
        return Reactors.reactor()!!.env(env)!!.dispatcher(ThreadPoolExecutorDispatcher(2, 10))!!.get()!!;
    }

}
