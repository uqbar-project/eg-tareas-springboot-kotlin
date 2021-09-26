package org.uqbar.tareas

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class TareasApplication

fun main(args: Array<String>) {
    runApplication<TareasApplication>(*args)
}
