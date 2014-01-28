package com.vasilich.commands.exec

import org.springframework.stereotype.Component
import com.vasilich.config.Config
import com.vasilich.commands.api.Command
import java.text.MessageFormat
import org.springframework.beans.factory.annotation.Autowired

Component
Config("exec")
public class ExecCgf(val scripts: Array<ExecUnit> = array(),
                     val timeout: Long = 60000,
                     val done: String = "Done",
                     val error: String = "Failed")

public class ExecUnit(val aliases: Array<String> = array(), val script: String = "", val output: String = "")

Component
public class ExecCommand [Autowired] (private val cfg: ExecCgf,
                                      private val shell: ShellCommandExecutor): Command {

    override fun execute(msg: String): String? {
        val scriptUnit = cfg.scripts.find { it.aliases.any { msg.contains(it) } }
        if(scriptUnit == null) {
            return null
        }
        val execResult = shell.exec(scriptUnit.script, cfg.timeout)
        val resultCode = if(execResult.exitCode == 0) cfg.done else cfg.error
        val output = array(MessageFormat.format(scriptUnit.output, execResult.output), resultCode)
        return when {
            output.get(0).isEmpty() -> resultCode
            else -> output.makeString("\n")
        }
    }

}
