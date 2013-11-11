package com.vasilich.commands.monitoring

import org.hyperic.sigar.SigarProxy
import org.hyperic.sigar.Sigar
import org.hyperic.jni.ArchLoader
import org.scijava.nativelib.NativeLoader


/**
 *
 * @author Ilya_Mestnikov
 */
public class SigarLoader {

    fun createSigar() : SigarProxy {
        val loader = org.hyperic.sigar.SigarLoader(javaClass<org.hyperic.sigar.SigarLoader>()) // create a Sigar Loader instance
        val nativeName = loader.getDefaultLibName() // resolve a native library name like sigar-amd64-winnt
        val extractor = NativeLoader.getJniExtractor() // create an extractor to exctract a native lib to the temp dir
        val file = extractor!!.extractJni("META-INF/lib/", nativeName) // extract the native lib to the temp dir
        val ext:String? = ArchLoader.getLibraryExtension() // get an extension: .dll .so etc...
        val libName: String = file!!.getName().trimTrailing(ext as String) // lib name without an extension
        loader.setLibName(libName) // set libName as sigar-amd64-winnt<temp_numbers>
        System.setProperty("org.hyperic.sigar.path", System.getProperty("java.io.tmpdir") as String) //point Sigar to the temp dir as the native lib dir
        loader.load() // load a native dir
        System.setProperty("org.hyperic.sigar.path", "-") // say to Sigar do not try to load anything
        return Sigar() //create a Sigar instance
    }
}
