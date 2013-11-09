package app

import java.io.BufferedReader
import java.io.InputStreamReader
import org.springframework.context.annotation.AnnotationConfigApplicationContext

fun main(args : Array<String>) {
    AnnotationConfigApplicationContext(javaClass<AppContext>(), javaClass<WebServerContext>())
    BufferedReader(InputStreamReader(System.`in`)).readLine();
}
