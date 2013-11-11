package functional.listener

import org.springframework.beans.factory.config.BeanPostProcessor
import com.vasilich.commands.basic.exec.ExecCgf
import com.vasilich.commands.basic.exec.ExecUnit
import com.vasilich.os.OsDetails

/**
 * Tests can be launched in Win or Linux
 * We should decide, what script should be executed
 */
class ScriptExtensionPostProcessor: BeanPostProcessor {


    override fun postProcessBeforeInitialization(bean: Any?, beanName: String?): Any? {
        return bean
    }
    override fun postProcessAfterInitialization(bean: Any?, beanName: String?): Any? = when(bean) {
        is ExecCgf -> {
            ExecCgf(bean.scripts.map { ExecUnit(it.aliases, OsDetails.pickProperScript(it.script), it.output) }.copyToArray(),
                    bean.timeout,
                    bean.done)
        }
        else -> bean
    }
}
