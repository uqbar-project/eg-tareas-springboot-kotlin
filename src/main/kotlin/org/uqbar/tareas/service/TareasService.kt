package org.uqbar.tareas.service

import org.springframework.stereotype.Service
import org.uqbar.tareas.domain.Tarea
import org.uqbar.tareas.errors.BusinessException
import org.uqbar.tareas.errors.NotFoundException
import org.uqbar.tareas.repository.TareasRepository
import org.uqbar.tareas.repository.UsuariosRepository

@Service
class TareasService(
   val tareasRepository: TareasRepository,
   val usuariosRepository: UsuariosRepository
) {

   fun tareas() = tareasRepository.allInstances()

   fun tareaPorId(id: Int): Tarea =
      tareasRepository.searchById(id) ?: throw NotFoundException("No se encontró la tarea de id <$id>")

    fun buscar(descripcion: String) = tareasRepository.search(descripcion)

    fun actualizar(id: Int, tareaActualizada: Tarea): Tarea {
       val tarea = tareaPorId(id)
       tareaActualizada.id = id
       asignar(tareaActualizada)
       tarea.actualizar(tareaActualizada)
       tarea.validar()
       tareasRepository.update(tarea)
       return tarea
    }

    fun borrar(id: Int): Tarea {
       val tarea = tareaPorId(id)
       tareasRepository.delete(tarea)
       return tarea
    }

    fun crear(nuevaTarea: Tarea): Tarea {
       if (nuevaTarea.id != 0) {
          throw BusinessException("No debe pasar el identificador de la tarea")
       }
      asignar(nuevaTarea)
      nuevaTarea.validar()
      tareasRepository.create(nuevaTarea)
      return nuevaTarea
   }

   private fun asignar(tareaActualizada: Tarea) {
      val nombreAsignatario = tareaActualizada.asignatario?.nombre
      // Solo llamamos a getAsignatario si el nombre contiene un valor distinto de null
      tareaActualizada.asignatario = nombreAsignatario?.let {
         usuariosRepository.getAsignatario(it) ?: throw NotFoundException("No se encontró el usuario <$it>")
      }
   }
}