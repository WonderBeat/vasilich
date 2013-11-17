package com.vasilich.commands.basic.exec

import com.vasilich.config.Config
import org.springframework.stereotype.Component
import java.util.Scanner
import java.util.LinkedList
import org.slf4j.LoggerFactory
import java.io.IOException

Component
Config("exec")
public class VerboseExecuteCfg(val marker: Array<String> = array("VSLC: "))

/**
 * This executor knows, that it's possible to notify about execution process via reactor notificatin
 * Every time, output contains line, witch matches 'notify' selector, we will notify users
 * @author Denis Golovachev
 */
public class VerboseShellCommandExecutor(private val cfg: VerboseExecuteCfg,
                                         private val processMonitor: (String) -> Unit = {}): ShellCommandExecutor {

    val logger = LoggerFactory.getLogger(javaClass<VerboseShellCommandExecutor>())!!;

    override fun exec(cmd: String, timeout: Long): ShellOutput {
        val builder = ProcessBuilder(cmd.split(" ").toList())
        logger.debug("Exec cmd: ${cmd}")
        val proc = builder.start()
        val scanner = Scanner(proc.getInputStream()!!, "UTF-8")
        val output = LinkedList<String>()
        while (scanner.hasNext()) {
            val line = scanner.nextLine()
            output add line
            processMonitor(line)
        }
        return ShellOutput(proc.exitValue(), output.toList().makeString("\n"))
    }
}
