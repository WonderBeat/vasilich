package com.vasilich.commands.monitoring

import org.springframework.stereotype.Component
import com.vasilich.commands.api.Command
import javax.management.MBeanServerConnection
import java.lang.management.OperatingSystemMXBean
import java.lang.management.ManagementFactory

/**
 *
 * @author Ilya_Mestnikov
 * @date 11/13/13
 * @time 3:02 AM
 */
Component
public class CpuUsageCommand: Command {
    override fun execute(msg: String): String? {
        val mbsc = ManagementFactory.getPlatformMBeanServer()!!;

        val osMBean = ManagementFactory.newPlatformMXBeanProxy(
            mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, javaClass<OperatingSystemMXBean>());

        val nanoBefore = System.nanoTime();
        /*val cpuBefore = osMBean.getProcessCpuTime();

        // Call an expensive task, or sleep if you are monitoring a remote process

        long cpuAfter = osMBean.getProcessCpuTime();
        long nanoAfter = System.nanoTime();

        long percent;
        if (nanoAfter > nanoBefore)
            percent = ((cpuAfter-cpuBefore)*100L)/
            (nanoAfter-nanoBefore);
        else percent = 0;

        System.out.println("Cpu usage: "+percent+"%");*/
        return ""
    }

}