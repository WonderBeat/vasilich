package com.vasilich.commands.basic.exec

import reactor.core.Observable
import com.vasilich.config.Config
import org.springframework.stereotype.Component
import org.apache.commons.exec.ExecuteWatchdog
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.PumpStreamHandler
import org.apache.commons.exec.DefaultExecuteResultHandler
import java.io.PipedOutputStream
import java.io.PipedInputStream
import com.vasilich.connectors.xmpp.Topics
import java.io.OutputStream
import java.io.InputStream
import reactor.event.Event

Component
Config("exec")
public class VerboseExecuteCfg(val notify: Array<String> = array("VSLC: "), val discrete: Long = 800)


/**
 * Weee. Apache exec has not been released for 3 years.
 * Need to find a replacement
 * https://issues.apache.org/jira/browse/EXEC-49
 */
class PumpStreamHandlerFix(out: OutputStream): PumpStreamHandler(out) {

    override protected fun createPump(`is`: InputStream?, os: OutputStream?): Thread? {
        return createPump(`is`, os, true);
    }

}


/**
 * This executor knows, that it's possible to notify about execution process via reactor notificatin
 * Every time, output contains line, witch matches 'notify' selector, we will notify users
 * @author Denis Golovachev
 */
public class VerboseShellCommandExecutor(private val reactor: Observable,
                                         private val topics: Topics = Topics(),
                                         private val cfg: VerboseExecuteCfg): ShellCommandExecutor {

    override fun exec(cmd: String, timeout: Long): String {
        val stdout = PipedInputStream()
        val pipedStdout = PipedOutputStream(stdout)
        val psh = PumpStreamHandlerFix(pipedStdout)
        val command = CommandLine.parse(cmd)
        val exec = DefaultExecutor()
        val resultHandler = DefaultExecuteResultHandler()
        exec.setWatchdog(ExecuteWatchdog(timeout))
        exec.setStreamHandler(psh)
        exec.execute(command, resultHandler)
        val output = linkedListOf<String>()
        do {
            resultHandler.waitFor(cfg.discrete)
            val outputPortion = stdout.reader("UTF-8").readText()
            output add outputPortion
            notifyAboutProgress(outputPortion)
        } while(!resultHandler.hasResult())
        return output.reduce { a, b -> a + b }
    }

    private fun notifyAboutProgress(output: String) {
        output.replace("\\r", "").split("\\n").forEach { line ->
            val marker = cfg.notify.find { line.contains(it) }
            if(marker != null) {
                val withoutNotificationMarkers = line.substring(line.indexOf(marker) + marker.length).replace("\n", "").replace("\r", "")
                reactor.notify(topics.send, Event.wrap(withoutNotificationMarkers))
            }
        }
    }


}
