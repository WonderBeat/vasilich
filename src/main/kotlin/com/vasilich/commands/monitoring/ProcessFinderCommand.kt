package com.vasilich.commands.monitoring

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired
import com.vasilich.commands.api.Command
import org.hyperic.sigar.SigarProxy
import org.hyperic.sigar.ptql.ProcessFinder
import java.util.Arrays

/**
 *
 * @author Ilya_Mestnikov
 */
Component
public class ProcessFinderCommand [Autowired] (private val sigar: SigarProxy): Command {

    override fun execute(msg: String): String? {

        val pf = ProcessFinder(sigar)
        //pf.start=java
        val strings = msg.split("pf.")
        val strings1 = strings[1].split("=")
        when (strings1[0]) {
            "start" -> {
                val ids = pf.find("State.Name.sw=" + strings1[1])
                if (ids!!.size == 0) return "Found nothing" else return "Found ids: " + Arrays.toString(ids)

            }
        }
        return "Couldn't find anything"


    }

}