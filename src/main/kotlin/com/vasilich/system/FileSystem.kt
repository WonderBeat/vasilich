package com.vasilich.system

import java.nio.file.FileStore
import java.nio.file.FileSystems
import java.util.ArrayList

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

public class Disk(val fs : FileStore) {

    val name : String
    get() = fs.name()!!

    val total : Long
    get() = fs.getTotalSpace()

    val available : Long
    get() = fs.getTotalSpace() - fs.getUnallocatedSpace()

    val used : Long
    get() = fs.getUsableSpace()
    /*fun name: String {
        if (SystemUtils.IS_OS_WINDOWS) {
            return name!!.split("\\s").filter {
                it.matches("^\\([A-Z]:\\)$")
            }.first as String
        }
    }*/
}

public class FileSystem {

    val disks: ArrayList<Disk>;
    {
        val fs = FileSystems.getDefault()!!
        val stores = fs.getFileStores()!!
        disks = ArrayList()
        for (store in stores) {
            val disk = Disk(store)
            disks.add(disk)
        }
    }

}