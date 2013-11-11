package com.vasilich.commands.basic.exec

import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Component
import com.vasilich.config.Config
import org.springframework.beans.factory.annotation.Autowired

Component
Config("exec")
class ShellProviderCfg(val env: ExecutionEnv = OsDetails.current)

/**
 * Resolves script by name
 * Prepends env shell
 * Appends extension if needed
 *
 */
Component
class ShellCommandResolver [Autowired] (private val cfg: ShellProviderCfg) {

    fun cmdForName(name: String): String {
        val scriptResource = FileSystemResource(pickProperScript(name))
        return "${cfg.env.shell} '${scriptResource.getFile()!!.getAbsolutePath()}'"
    }

    /**
     * Proper script should be selected, depending on platform
     * bat - Windows,
     * sh - Linux
     */
    private fun pickProperScript(scriptName: String): String = when {
        scriptName.contains(".") -> scriptName
        else -> scriptName + cfg.env.extension
    }
}


