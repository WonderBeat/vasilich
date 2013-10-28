package com.vasilich.config.listener

import org.springframework.beans.factory.config.BeanPostProcessor
import com.vasilich.connectors.xmpp.XmppConf
import com.vasilich.connectors.xmpp.XmppRoomCfg
import java.util.Random
import org.springframework.core.PriorityOrdered
import org.springframework.core.Ordered

/**
 * Random room will be generated for test
 */
public class RoomRandomizerPostProcessor(): BeanPostProcessor, PriorityOrdered {


    override fun getOrder(): Int {
        return Ordered.HIGHEST_PRECEDENCE - 1
    }

    val rand = Random()

    override fun postProcessBeforeInitialization(bean: Any?, beanName: String?): Any? {
        return bean
    }
    override fun postProcessAfterInitialization(bean: Any?, beanName: String?): Any? {
        when(bean) {
            is XmppConf -> return XmppConf(bean.server, bean.login, bean.password,
                    XmppRoomCfg(rand.nextInt().toString() + bean.room.id, bean.room.username, bean.room.password))
            else -> return bean
        }
    }
}
