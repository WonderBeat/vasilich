package com.vasilich.commands.monitoring

import org.springframework.stereotype.Component
import com.vasilich.commands.api.Command
import com.vasilich.system.FileSystem
import com.vasilich.system.Size

/**
 *
 * @author Ilya_Mestnikov
 * @date 11/12/13
 * @time 12:54 PM
 */
Component
public class DiskUsageCommand : Command {

    override fun execute(msg: String): String? {

        var output = "Disk Usage Statistics:"

        val fs = FileSystem()

        for (disk in fs.disks) {
            output += "\n%-4s T:%5dGb U:%4dGb A:%4dGb".format(disk.name,
                    Size.Gb.get(disk.total), Size.Gb.get(disk.used), Size.Gb.get(disk.available));

        }
        return output
    }

}