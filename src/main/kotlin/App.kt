package app

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.lang.Long

fun main(args : Array<String>) {
    val ctx = AnnotationConfigApplicationContext(javaClass<AppContext>(), javaClass<WebServerContext>())
    ctx.start()
    print("Running...")
    ctx.registerShutdownHook()
    Thread.sleep(Long.MAX_VALUE)
}
