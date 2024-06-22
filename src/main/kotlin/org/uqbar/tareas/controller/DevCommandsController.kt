package org.uqbar.tareas.controller

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import org.uqbar.tareas.bootstrap.TareasBootstrap

@RestController
@CrossOrigin("*")
@ConditionalOnProperty(prefix = "dev-endpoints", name = ["enabled"])
class DevCommandsController(
   val tareasBootstrap: TareasBootstrap
) {

    @PutMapping("/reset/all")
    fun resetAll() = tareasBootstrap.afterPropertiesSet()

    @PutMapping("/reset/tareas")
    fun resetTareas() = tareasBootstrap.crearTareas()

}