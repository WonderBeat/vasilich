package com.vasilich.commands.monitoring

import org.hyperic.sigar.SigarProxy
import org.hyperic.sigar.Sigar
import org.hyperic.jni.ArchLoader
import org.scijava.nativelib.NativeLoader
import org.slf4j.LoggerFactory


/**
 *
 * @author Ilya_Mestnikov
 */
public class SigarLoader {

    val logger = LoggerFactory.getLogger(javaClass<SigarLoader>())!!;

    fun createSigar() : SigarProxy {
        val loader = org.hyperic.sigar.SigarLoader(javaClass<org.hyperic.sigar.SigarLoader>()) // create a Sigar Loader instance
        val nativeName = loader.getDefaultLibName() // resolve a native library name like sigar-amd64-winnt
        logger.debug("Native library name ${nativeName}")
        val extractor = NativeLoader.getJniExtractor()!! // create an extractor to exctract a native lib to the temp dir
        val file = extractor.extractJni("META-INF/lib/", nativeName)!! // extract the native lib to the temp dir
        logger.debug("File path ${file.getAbsolutePath()}")
        val ext:String? = ArchLoader.getLibraryExtension() // get an extension: .dll .so etc...
        val libName: String = file.getName().trimTrailing(ext as String) // lib name without an extension
        logger.debug("Loaded library name ${libName}")
        loader.setLibName(libName) // set libName as sigar-amd64-winnt<temp_numbers>
        val tmpDirPath = System.getProperty("java.io.tmpdir")
        logger.debug("Temp dir path ${tmpDirPath}")
        System.setProperty("org.hyperic.sigar.path", tmpDirPath as String) //point Sigar to the temp dir as the native lib dir
        loader.load() // load a native dir
        System.setProperty("org.hyperic.sigar.path", "-") // say to Sigar do not try to load anything
        return Sigar() //create a Sigar instance
    }
}
