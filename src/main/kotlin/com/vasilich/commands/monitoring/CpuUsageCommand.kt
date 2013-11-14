package com.vasilich.commands.monitoring

import org.springframework.stereotype.Component
import com.vasilich.commands.api.Command
import java.lang.management.ManagementFactory
import com.sun.management.OperatingSystemMXBean
import com.vasilich.system.Cpu

/**
 *
 * @author Ilya_Mestnikov
 *
 */
Component
public class CpuUsageCommand: Command {

    override fun execute(msg: String): String? {

        val percent = Cpu().getSystemLoad() * 100;
        return "Current cpu usage: %.2f%%".format(percent);
    }

}