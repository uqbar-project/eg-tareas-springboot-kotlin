package org.uqbar.tareas.controller

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import org.uqbar.tareas.domain.Tarea
import org.uqbar.tareas.service.TareasService

@RestController
@CrossOrigin("*")
class TareasController(val tareasService: TareasService) {

    @GetMapping("/tareas")
    fun tareas() = tareasService.tareas()

    @GetMapping("/tareas/{id}")
    fun tareaPorId(@PathVariable id: Int) = tareasService.tareaPorId(id)

    @GetMapping("/tareas/search")
    fun buscar(@RequestParam(name = "descripcion") descripcionTarea: String) = tareasService.buscar(descripcionTarea)

    @PutMapping("/tareas/{id}")
    fun actualizar(@PathVariable id: Int, @Valid @RequestBody tareaBody: Tarea): Tarea {
        return tareasService.actualizar(id, tareaBody)
    }

    @DeleteMapping("/tareas/{id}")
    fun eliminar(@PathVariable id: Int): Tarea {
        return tareasService.borrar(id)
    }

    @PostMapping("/tareas")
    fun crear(@Valid @RequestBody tareaBody: Tarea): Tarea {
        return tareasService.crear(tareaBody)
    }
}
