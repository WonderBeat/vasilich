package com.vasilich.commands.exec

public class ShellOutput(val exitCode: Int, val output: String = "")

public trait ShellCommandExecutor {
    /**
     * timeout in mills
     * returns command output
     */
    fun exec(cmd: String, timeout: Long): ShellOutput
}
