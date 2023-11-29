package org.uqbar.tareas.controller

import io.swagger.v3.oas.annotations.Operation
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
    @Operation(summary = "Regresa todos los repositorios a su estado inicial")
    fun resetAll() = tareasBootstrap.afterPropertiesSet()

    @PutMapping("/reset/tareas")
    @Operation(summary = "Regresa el repositorio de tareas a su estado inicial")
    fun resetTareas() = tareasBootstrap.crearTareas()

}