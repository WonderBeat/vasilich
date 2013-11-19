package com.vasilich.commands.monitoring

import com.vasilich.commands.api.Command
import org.springframework.stereotype.Component
import com.vasilich.system.Cpu
import com.vasilich.system.FileSystem
import com.vasilich.system.Size

/**
 *
 * @author Ilya_Mestnikov
 * @date 11/18/13
 * @time 2:08 AM
 */
Component
public class SystemMonitoringCommand (val cpu: Cpu = Cpu(), val fs: FileSystem = FileSystem()) : Command {


    override fun execute(msg: String): String? {
        val parts = msg.split("\\s?mon\\s")
        if (parts.size > 1) {
            when (parts.get(1).trim()) {
                "cpu" -> {
                    val percent = Cpu().getSystemLoad() * 100;
                    return "Current cpu usage: %.2f%%".format(percent);
                }
                "disk" -> {
                    var output = "Disk Usage Statistics:"
                    val fs = FileSystem()
                    for (disk in fs.disks) {
                        output += "\n%-4s T:%5dGb U:%4dGb A:%4dGb".format(disk.name,
                                Size.Gb.get(disk.total), Size.Gb.get(disk.used), Size.Gb.get(disk.available));
                    }
                    return output
                }
            }
        }
        return "Usage help: Vasilich, mon <command>"
    }

}