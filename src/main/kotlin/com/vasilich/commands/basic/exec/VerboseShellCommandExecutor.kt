package com.vasilich.commands.basic.exec

import com.vasilich.config.Config
import org.springframework.stereotype.Component
import java.io.OutputStream
import java.io.InputStream
import org.springframework.core.io.FileSystemResource
import java.util.Scanner
import java.util.LinkedList

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

    override fun exec(cmd: String, timeout: Long): String {
        val builder = ProcessBuilder(cmd)
        builder.directory(FileSystemResource(System.getProperty("user.dir")).getFile())
        val proc = builder.start()
        val scanner = Scanner(proc.getInputStream()!!, "UTF-8")
        val output = LinkedList<String>()
        while (scanner.hasNext()) {
            val line = scanner.nextLine()
            output add line
            processMonitor(line)
        }
        return output.toList().makeString("\n")
    }
}
