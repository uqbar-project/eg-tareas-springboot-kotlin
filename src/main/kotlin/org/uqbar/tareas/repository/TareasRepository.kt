package org.uqbar.tareas.repository

import org.springframework.stereotype.Component
import org.uqbar.tareas.domain.Tarea
import org.uqbar.tareas.domain.Usuario
import org.uqbar.tareas.errors.NotFoundException
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicInteger

@Component
class TareasRepository {

    private val tareas = mutableListOf<Tarea>()
    private val lock = Any()

    // AtomicInteger permite incrementos atómicos sin sincronizar bloques completos.
    // En un escenario multi-thread (varios requests simultáneos) evitamos
    // que dos tareas reciban el mismo id por una condición de carrera.
    companion object {
        private val ultimoId = AtomicInteger(ID_INICIAL_REPOSITORY)
    }

    fun allInstances(): List<Tarea> = synchronized(lock) {
        tareas.toList()
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

    fun create(tarea: Tarea): Tarea = synchronized(lock) {
        tarea.id = ultimoId.getAndIncrement()
        tareas.add(tarea)
        tarea
    }

    fun searchById(id: Int) = synchronized(lock) {
        tareas.find { it.id == id }
    }

    fun search(descripcion: String) = synchronized(lock) {
        tareas.filter { it.descripcion.uppercase().contains(descripcion.uppercase()) }
    }

    fun update(tarea: Tarea): Tarea = synchronized(lock) {
        val index = tareas.indexOfFirst { it.id == tarea.id }
        if (index < 0) throw NotFoundException("No se encontró la tarea de id <${tarea.id}>")
        tareas[index] = tarea
        tarea
    }

    fun delete(tarea: Tarea) = synchronized(lock) {
        tareas.remove(tarea)
        tarea
    }

    fun clear() = synchronized(lock) {
        tareas.clear()
    }

    fun clearInit() = synchronized(lock) {
        tareas.clear()
        ultimoId.set(ID_INICIAL_REPOSITORY)
    }

}
