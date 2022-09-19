package org.uqbar.tareas.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.uqbar.tareas.domain.Tarea
import org.uqbar.tareas.service.TareasService

@RestController
@CrossOrigin("*")
class TareasController {

    @Autowired
    lateinit var tareasService: TareasService

    @GetMapping("/tareas")
    @Operation(summary = "Devuelve todas las tareas")
    fun tareas() = tareasService.tareas()

    @GetMapping("/tareas/{id}")
    @Operation(summary = "Permite conocer la información de una tarea por identificador")
    fun tareaPorId(@PathVariable id: Int) = tareasService.tareaPorId(id)

    @GetMapping("/tareas/search")
    @Operation(summary = "Devuelve todas las tareas cuya descripción contiene la descripción que pasamos como parámetro")
    fun buscar(@RequestBody tareaBusqueda: Tarea) = tareasService.buscar(tareaBusqueda)

    @PutMapping("/tareas/{id}")
    @Operation(summary = "Permite actualizar la información de una tarea")
    fun actualizar(@PathVariable id: Int, @RequestBody tareaBody: Tarea): Tarea {
        return tareasService.actualizar(id, tareaBody)
    }

    @PostMapping("/tareas")
    @Operation(summary = "Permite crear una tarea nueva")
    fun crear(@RequestBody tareaBody: Tarea): Tarea {
        return tareasService.crear(tareaBody)
    }
}