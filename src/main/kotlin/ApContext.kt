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
import reactor.core.Reactor
import com.vasilich.connectors.chat.FilteredChat
import com.vasilich.connectors.xmpp.createChat
import org.jivesoftware.smack.packet.Message
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.config.BeanPostProcessor
import com.vasilich.commands.SimpleCommandPostProcessor
import com.vasilich.commands.and
import com.vasilich.commands.aliasMatchCommandDetection
import com.vasilich.commands.outputMessageWrapper
import com.fasterxml.jackson.databind.DeserializationFeature

class CommunicationTopics(val send: String = "send-message", val receive: String = "recieve-message")

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

    Bean open fun configReaderPostProcessor(Autowired appCfg: JsonNode,
                                            Autowired objMapper: ObjectMapper): JsonBasedConfigPostProcessor {
        return JsonBasedConfigPostProcessor(appCfg, objMapper)
    }

    Bean open fun simpleCommandPostProcessor(Autowired reactor: Observable,
                                            Autowired appCfg: JsonNode,
                                            Autowired objMapper: ObjectMapper): BeanPostProcessor {
        val wrappers = and(aliasMatchCommandDetection(), outputMessageWrapper())
        return SimpleCommandPostProcessor(reactor, appCfg, objMapper, wrappers)
    }

    Bean open fun chat(Autowired cfg: XmppConf, Autowired reactor: Reactor): Chat<Message> {
        fun usernameInputMessageFilter(username: String): (Message) -> Boolean {
            return { (msg: Message) -> msg.getBody()!!.startsWith(username) }
        }
        val simpleChat = FilteredChat(createChat(cfg), recieveFilter = usernameInputMessageFilter(cfg.room.username))
        return ReactiveChat(simpleChat, reactor)
    }

    Bean open fun rootReactor(env: Environment): Observable {
        return Reactors.reactor()!!.env(env)!!.dispatcher(ThreadPoolExecutorDispatcher(2, 2))!!.get()!!;
    }

}
