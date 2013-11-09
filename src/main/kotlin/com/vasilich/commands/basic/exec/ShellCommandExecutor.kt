package com.vasilich.commands.basic.exec

public trait ShellCommandExecutor {
    /**
     * timeout in mills
     * returns command output
     */
    fun exec(cmd: String, timeout: Long): String
}
