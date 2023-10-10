package org.uqbar.tareas.repository

import org.springframework.stereotype.Component
import org.uqbar.tareas.domain.Tarea
import org.uqbar.tareas.domain.Usuario
import java.time.LocalDate

@Component
class TareasRepository {

    val tareas = mutableListOf<Tarea>()

    companion object {
        private var ultimoId = 1
    }

    fun allInstances(): List<Tarea> {
        return tareas
    }

    fun create(
        unaDescripcion: String,
        responsable: Usuario?,
        date: LocalDate?,
        unaIteracion: String,
        cumplimiento: Int
    ) {
        val tarea = Tarea().apply {
            if (responsable !== null) {
                asignarA(responsable)
            }
            descripcion = unaDescripcion
            fecha = date ?: LocalDate.now()
            iteracion = unaIteracion
            porcentajeCumplimiento = cumplimiento
        }
        create(tarea)
    }

    fun create(tarea: Tarea): Tarea {
        tarea.id = ultimoId++
        tareas.add(tarea)
        return tarea
    }

    fun searchById(id: Int) = allInstances().find { it.id == id }

    fun search(tareaBusqueda: Tarea) = tareas.filter { it.descripcion.uppercase().contains(tareaBusqueda.descripcion.uppercase()) }

    fun update(tarea: Tarea): Tarea {
        val indexTarea = tareas.indexOf(searchById(tarea.id!!))
        tareas.removeAt(indexTarea)
        tareas.add(indexTarea, tarea)
        return tarea
    }

    fun delete(tarea: Tarea): Tarea {
        tareas.remove(tarea)
        return tarea
    }

    fun clear() {
        tareas.clear()
    }

}