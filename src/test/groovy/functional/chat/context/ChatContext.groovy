package functional.chat.context

import com.vasilich.config.Config
import com.vasilich.connectors.chat.Chat
import com.vasilich.connectors.chat.FilteredChat
import com.vasilich.connectors.chat.NopMessage
import com.vasilich.connectors.xmpp.*
import jet.Function1
import org.jivesoftware.smack.packet.Message
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Bean
import reactor.core.Reactor

@Config('listener')
class XmppLisenerCfg {
    String login
    String password
    String nick
}

class SpringChatContext {

    @Bean XmppLisenerCfg xmppListenerCfg() { new XmppLisenerCfg() }

    /**
     * Michalich. Another bot, that will chat with Vasilich and asks everything about Vasilich knows ;)
     * He will login into the same room, but with different nick
     */
    @Bean
    ReactiveChat chatListener(XmppLisenerCfg listenerCredentials, XmppConf xmppCfg, Reactor reactor) {

        def roomCfg = new XmppRoomCfg(xmppCfg.room.id, listenerCredentials.nick, xmppCfg.room.password)
        XmppConf listenerConfig = new XmppConf(xmppCfg.server, listenerCredentials.login,
                listenerCredentials.password, roomCfg)
        Function1 listeningForVasilichAliasResolver =
            { Message msg -> msg.getFrom().contains(xmppCfg.room.username) ? msg : NopMessage } as Function1

        Chat simpleChat = new FilteredChat(XmppPackage.createChat(listenerConfig),
                                            {true} as Function1,
                                            listeningForVasilichAliasResolver)
        new ReactiveChat(simpleChat, reactor, new Topics("test-send", "test-recieve"))
    }

    /**
     * Let's randomize test room, because we don't want to interfere with another test executions, that
     * could be launched the same time
     */
    @Bean
    BeanPostProcessor roomRandomizerPostProcessor() { new RoomRandomizerPostProcessor() }

    /**
     * Tests can be launched in Win and Linux
     * We should pick a proper script
     */
    @Bean BeanPostProcessor scriptExtensionPostProcessor() { new ScriptExtensionPostProcessor() }

}
