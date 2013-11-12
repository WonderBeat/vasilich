package com.vasilich.commands.monitoring

import org.springframework.stereotype.Component
import com.vasilich.commands.api.Command
import java.nio.file.FileSystem
import java.nio.file.FileStore
import java.nio.file.FileSystems
import java.nio.file.Paths
import java.io.PrintStream

/**
 *
 * @author Ilya_Mestnikov
 * @date 11/12/13
 * @time 12:54 PM
 */
Component
public class DiskUsageCommand : Command {

    private val K = 1024*1000*1000

    override fun execute(msg: String): String? {

        var output = "Disk usage: \n%-20s %12s %12s %12s\n".format("Filesystem", "Gb", "used", "avail");

        val fs = FileSystems.getDefault();
        for (store in fs?.getFileStores()!!) {
            val total = store.getTotalSpace() / K;
            val used = (store.getTotalSpace() - store.getUnallocatedSpace()) / K;
            val avail = store.getUsableSpace() / K;

            val diskName = store.toString();

            output += "%-20s %12d %12d %12d\n".format(diskName, total, used, avail);
        }
        return output
    }

}