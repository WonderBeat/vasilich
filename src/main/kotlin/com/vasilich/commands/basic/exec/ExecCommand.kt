package com.vasilich.commands.basic.exec

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
        val output = MessageFormat.format(scriptUnit.output, execResult.output)
        return when {
            output.isEmpty() && execResult.exitCode == 0 -> cfg.done
            output.isEmpty() -> cfg.error
            else -> output
        }
    }

}
