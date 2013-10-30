package com.vasilich.commands.examples

import com.vasilich.commands.Command
import org.springframework.stereotype.Component
import com.vasilich.config.Config
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.PumpStreamHandler
import java.io.ByteArrayOutputStream
import org.apache.commons.exec.ExecuteWatchdog
import java.text.MessageFormat
import org.springframework.core.io.FileSystemResource
import org.slf4j.LoggerFactory
import com.vasilich.commands.basic.exec.ExecutionEnv
import org.springframework.beans.factory.annotation.Autowired
import com.vasilich.commands.basic.exec.OsDetails

Component
Config("exec")
public class ExecCgf(val env: ExecutionEnv = OsDetails.current,
                     val scripts: Array<ExecUnit> = array(),
                     val timeout: Long = 10000)

public class ExecUnit(val aliases: Array<String> = array(), val script: String = "", val output: String = "")

Component
public class ExecCommand [Autowired] (private val cfg: ExecCgf): Command {

    val logger = LoggerFactory.getLogger(this.javaClass)!!;

    override fun execute(msg: String): String? {
        val scriptUnit = cfg.scripts.find { it.aliases.any { msg.contains(it) } }
        if(scriptUnit == null) {
            return null
        }
        logger.debug("Exec cmd: ${scriptUnit.script}")
        return MessageFormat.format(scriptUnit.output, executeScript(scriptUnit.script))
    }

    private fun executeScript(script: String): String {
        val stdout = ByteArrayOutputStream()
        val psh = PumpStreamHandler(stdout)
        val scriptResource = FileSystemResource(pickProperScript(script))
        val cmd = "${cfg.env.shell} ${scriptResource.getFile()!!.getAbsolutePath()}"
        val cl = CommandLine.parse(cmd)
        val exec = DefaultExecutor()
        exec.setWatchdog(ExecuteWatchdog(cfg.timeout))
        exec.setStreamHandler(psh)
        exec.execute(cl)

        return stdout.toString()
    }

    /**
     * Proper script should be selected, depending on platform
     * bat - Windows,
     * sh - Linux
     */
    private fun pickProperScript(scriptName: String): String {
        return when {
            scriptName.contains(".") -> scriptName
            else -> scriptName + cfg.env.extension
        }
    }
}
