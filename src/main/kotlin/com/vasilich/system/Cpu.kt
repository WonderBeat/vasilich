package com.vasilich.system

import java.lang.management.ManagementFactory
import com.sun.management.OperatingSystemMXBean

/**
 *
 * @author Ilya_Mestnikov
 */
public class Cpu () {

    private val osMBean: OperatingSystemMXBean =  ManagementFactory.newPlatformMXBeanProxy(
            ManagementFactory.getPlatformMBeanServer()!!,
            ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME,
            javaClass<OperatingSystemMXBean>())!!


    fun getSystemLoad() : Double {
        return osMBean.getSystemCpuLoad()
    }
    fun getProcNumber() : Int {
        return osMBean.getAvailableProcessors()
    }

}