package com.vasilich.commands.monitoring

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired
import com.vasilich.commands.api.Command
import org.hyperic.sigar.SigarProxy

/**
 *
 * @author Ilya_Mestnikov
 */
Component
public class DiskUsageCommand [Autowired] (private val sigar: SigarProxy): Command {

    override fun execute(msg: String): String? {
        /*
        val diskUsage = sigar.getDiskUsage("D:")
        return diskUsage.toString()
        */
        return "Stub disk command"
    }

}