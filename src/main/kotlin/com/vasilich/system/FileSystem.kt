package com.vasilich.system

import org.springframework.context.annotation.Bean
import java.nio.file.FileStore
import java.nio.file.FileSystems
import java.util.ArrayList
import org.apache.commons.lang3.SystemUtils

/**
 *
 * @author Ilya_Mestnikov
 */
enum class Size (val k: Long) {
    Kb : Size(1024)
    Mb : Size(Size.Kb.k * 1000)
    Gb : Size(Size.Mb.k * 1000)

    fun get(value: Long): Long {
        return value / k
    }
}

public class Disk(val name: String? = "", val total: Long, val available: Long, val used: Long) {

    /*fun name: String {
        if (SystemUtils.IS_OS_WINDOWS) {
            return name!!.split("\\s").filter {
                it.matches("^\\([A-Z]:\\)$")
            }.first as String
        }
    }*/
}

public class FileSystem {

    var disks: ArrayList<Disk>;
    {
        val fs = FileSystems.getDefault()!!
        val stores = fs.getFileStores()!!
        disks = ArrayList()
        for (store in stores) {
            val disk = Disk(store.name(), store.getTotalSpace(),
                    store.getTotalSpace() - store.getUnallocatedSpace(),
                    store.getUsableSpace())
            disks.add(disk)
        }
    }

}