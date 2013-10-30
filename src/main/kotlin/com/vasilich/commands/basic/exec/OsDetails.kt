package com.vasilich.commands.basic.exec

private val osname = System.getProperty("os.name")!!.toLowerCase()

class ExecutionEnv(val shell: String, val extension: String)

object OsDetails {

    val windows = ExecutionEnv("cmd.exe /C", ".bat")

    val linux = ExecutionEnv("sh", ".sh")

    val current = when {
        osname.contains("win") -> windows
        else -> linux
    }

}
