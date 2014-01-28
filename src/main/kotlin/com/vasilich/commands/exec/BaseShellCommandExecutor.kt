package com.vasilich.commands.exec

import com.vasilich.config.Config
import org.springframework.stereotype.Component
import java.util.Scanner
import java.util.LinkedList
import org.slf4j.LoggerFactory
import java.io.IOException

/**
 * Executes shell command, scan output stream and pass it to monitor for further processing
 *
 * @author Denis Golovachev
 */
public class BaseShellCommandExecutor(
        private val processMonitor: (String) -> Unit = {}): ShellCommandExecutor {

    val logger = LoggerFactory.getLogger(javaClass<BaseShellCommandExecutor>())!!;

    override fun exec(cmd: String, timeout: Long): ShellOutput {
        val builder = ProcessBuilder(cmd.split(" ").toList())
        logger.debug("Exec cmd: ${cmd}")
        val process = builder.start()
        val scanner = Scanner(process.getInputStream()!!, "UTF-8")
        val output = LinkedList<String>()
        while (scanner.hasNext()) {
            val line = scanner.nextLine()
            output add line
            processMonitor(line)
        }
        return ShellOutput(process.waitFor(), output.toList().makeString("\n"))
    }
}
