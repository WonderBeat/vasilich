package com.vasilich.commands.basic.exec

import org.springframework.stereotype.Component
import com.vasilich.config.Config
import org.slf4j.LoggerFactory
import com.vasilich.commands.api.Command
import java.text.MessageFormat
import org.springframework.core.io.FileSystemResource
import org.springframework.beans.factory.annotation.Autowired

Component
Config("exec")
public class ExecCgf(val scripts: Array<ExecUnit> = array(),
                     val timeout: Long = 60000,
                     val done: String = "Done")

public class ExecUnit(val aliases: Array<String> = array(), val script: String = "", val output: String = "")

Component
public class ExecCommand [Autowired] (private val cfg: ExecCgf,
                                      private val shell: ShellCommandExecutor,
                                      private val shellCommandProvider: ShellCommandResolver): Command {

    val logger = LoggerFactory.getLogger(this.javaClass)!!;

    override fun execute(msg: String): String? {
        val scriptUnit = cfg.scripts.find { it.aliases.any { msg.contains(it) } }
        if(scriptUnit == null) {
            return null
        }
        val cmd = shellCommandProvider.cmdForName(scriptUnit.script)
        logger.debug("Exec cmd: ${cmd}")
        val output = MessageFormat.format(scriptUnit.output, shell.exec(cmd, cfg.timeout))
        return if(output.isEmpty()) cfg.done else output
    }

}
