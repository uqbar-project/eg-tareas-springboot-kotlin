package org.uqbar.tareas.controller

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
   fun buscar(@RequestParam(name = "descripcion") descripcionTarea: String) = tareasService.buscar(Tarea().apply{descripcion = descripcionTarea})

   @PutMapping("/tareas/{id}")
   fun actualizar(@PathVariable id: Int, @RequestBody tareaBody: Tarea): Tarea {
      return tareasService.actualizar(id, tareaBody)
   }

   @DeleteMapping("/tareas/{descripcion}")
   fun eliminar(@PathVariable descripcion: String): List<Tarea> {
      return tareasService.borrar(descripcion)
   }

   @PostMapping("/tareas")
   fun crear(@RequestBody tareaBody: Tarea): Tarea {
      return tareasService.crear(tareaBody)
   }
}