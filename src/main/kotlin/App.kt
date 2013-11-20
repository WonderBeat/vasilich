package app

import org.springframework.context.annotation.AnnotationConfigApplicationContext

fun main(args : Array<String>) {
    val ctx = AnnotationConfigApplicationContext(javaClass<AppContext>(), javaClass<WebServerContext>())
    ctx.start()
    print("Running...")
    ctx.registerShutdownHook()
    Thread.sleep(Integer.MAX_VALUE.toLong()) // Kotlin complains, that there is no MAX_VALUE for jet.Long and forbid to use java.Long
}
