package functional.chat.context

import com.vasilich.commands.basic.exec.ExecCgf
import com.vasilich.commands.basic.exec.ExecUnit
import com.vasilich.connectors.xmpp.XmppConf
import com.vasilich.connectors.xmpp.XmppRoomCfg
import com.vasilich.os.OsDetails
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.core.Ordered
import org.springframework.core.PriorityOrdered


class RoomRandomizerPostProcessor implements BeanPostProcessor, PriorityOrdered {

    @Override
    int getOrder() { Ordered.HIGHEST_PRECEDENCE - 1 }

    def rand = new Random()

    @Override
    Object postProcessBeforeInitialization(Object bean, String s) throws BeansException
    { bean }

    @Override
    Object postProcessAfterInitialization(Object bean, String s) throws BeansException {
        if(bean instanceof XmppConf) {
            def xmppBean = bean as XmppConf
            new XmppConf(xmppBean.server, xmppBean.login, xmppBean.password,
                    new XmppRoomCfg(rand.nextInt().toString() + xmppBean.room.id, xmppBean.room.username, xmppBean.room.password))
        } else {
            bean
        }
    }
}

class ScriptExtensionPostProcessor implements BeanPostProcessor, PriorityOrdered {

    @Override
    int getOrder() { Ordered.HIGHEST_PRECEDENCE - 1 }

    /*
            is ExecCgf -> {
            ExecCgf(bean.scripts.map { ExecUnit(it.aliases, OsDetails.pickProperScript(it.script), it.output) }.copyToArray(),
                    bean.timeout,
                    bean.done)
        }
        else -> bean
     */

    @Override
    Object postProcessBeforeInitialization(Object bean, String s) throws BeansException
    { bean }

    @Override
    Object postProcessAfterInitialization(Object bean, String s) throws BeansException {
        if(bean instanceof ExecCgf) {
            ExecCgf execBean = bean as ExecCgf
            new ExecCgf(execBean.scripts.collect {
                    new ExecUnit(it.aliases, new OsDetails().pickProperScript(it.script), it.output)} as ExecUnit[],
                    execBean.timeout,
                    execBean.done
            )
        } else {
            bean
        }
    }
}
