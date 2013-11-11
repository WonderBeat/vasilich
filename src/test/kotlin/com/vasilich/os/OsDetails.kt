package com.vasilich.os

private val osname = System.getProperty("os.name")!!.toLowerCase()

class ExecutionEnv(val shell: String, val extension: String)

public object OsDetails {

    val windows = ExecutionEnv("cmd.exe /C", ".bat")

    val linux = ExecutionEnv("sh", ".sh")

    val current = when {
        osname.contains("win") -> windows
        else -> linux
    }

    /**
     * Proper script should be selected, depending on platform
     * bat - Windows,
     * sh - Linux
     */
    fun pickProperScript(scriptName: String): String = when {
        scriptName.contains(".") -> scriptName
        else -> scriptName + current.extension
    }

}
