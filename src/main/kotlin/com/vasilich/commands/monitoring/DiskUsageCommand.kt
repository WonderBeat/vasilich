package com.vasilich.commands.monitoring

import org.springframework.stereotype.Component
import com.vasilich.commands.api.Command
import java.nio.file.FileSystem
import java.nio.file.FileStore
import java.nio.file.FileSystems
import java.nio.file.Paths
import java.io.PrintStream
import org.apache.commons.lang3.SystemUtils

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

        var output = "Disk Usage Statistics:"
        val fs = FileSystems.getDefault();

        for (store in fs?.getFileStores()!!) {
            val total = store.getTotalSpace() / K;
            val used = (store.getTotalSpace() - store.getUnallocatedSpace()) / K;
            val avail = store.getUsableSpace() / K;

            var diskName = store.toString()!!

            if (SystemUtils.IS_OS_WINDOWS) {
                diskName = diskName.split("\\s").filter {
                    it.matches("^\\([A-Z]:\\)$")
                }.first as String
            }
            output += "\n%-4s T:%5dGb U:%4dGb A:%4dGb".format(diskName, total, used, avail);
        }
        return output
    }

}