package com.vasilich.commands.monitoring

import com.vasilich.commands.api.Command
import spock.lang.Specification

/**
 *
 * @author Ilya_Mestnikov
 */
class SystemMonitorTest extends Specification {

    def "on monitoring command Vasilich should give disk|cpu status" () {
        given :
            Command systemMonitoringCommand = new SystemMonitoringCommand()
        when :
            def output = systemMonitoringCommand.execute(msg)
        then :
            output.startsWith(a)
        where :
            msg | a
            "Vasilich, mon" | "Usage"
            "Vasilich, mon " | "Usage"
            "Vasilich, mon cpu" | "Current cpu usage"
            "Vasilich, mon cpu " | "Current cpu usage"
            "Vasilich, mon disk" | "Disk Usage Statistics:"
            "Vasilich, mon disk " | "Disk Usage Statistics:"
    }

}
