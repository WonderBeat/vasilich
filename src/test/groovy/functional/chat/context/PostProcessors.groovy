package functional.chat.context

import com.vasilich.commands.exec.AnsibleCgf
import com.vasilich.commands.exec.ExecCgf
import com.vasilich.commands.exec.ExecUnit
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

    @Override
    Object postProcessBeforeInitialization(Object bean, String s) throws BeansException
    { bean }

    @Override
    Object postProcessAfterInitialization(Object bean, String s) throws BeansException {
        if(bean instanceof ExecCgf) {
            ExecCgf execBean = bean as ExecCgf
            new ExecCgf(execBean.scripts.collect {
                    // no need to set extension if we're going to execute command. Dirty... but fine for tests only
                    def command = it.script.contains(' ') ? it.script : new OsDetails().pickProperScript(it.script)
                    new ExecUnit(it.aliases, command, it.output)} as ExecUnit[],
                    execBean.timeout,
                    execBean.done,
                    execBean.error
            )
        } else if(bean instanceof AnsibleCgf) { // copy-paste for Ansible, not cool, agree, to be cleaned later
                AnsibleCgf ansibleCgf = bean as AnsibleCgf
                new AnsibleCgf(ansibleCgf.scripts.collect {
                    // no need to set extension if we're going to execute command. Dirty... but fine for tests only
                    def command = it.script.contains(' ') ? it.script : new OsDetails().pickProperScript(it.script)
                    new ExecUnit(it.aliases, command, it.output)} as ExecUnit[],
                        ansibleCgf.timeout,
                        ansibleCgf.done,
                        ansibleCgf.error
                )
        } else {
            bean
        }
    }
}
