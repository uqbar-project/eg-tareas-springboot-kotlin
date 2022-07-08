package org.uqbar.tareas.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.uqbar.tareas.domain.Tarea
import org.uqbar.tareas.errors.BusinessException
import org.uqbar.tareas.errors.NotFoundException
import org.uqbar.tareas.repository.TareasRepository
import org.uqbar.tareas.repository.UsuariosRepository

@Service
class TareasService {

    @Autowired
    lateinit var tareasRepository: TareasRepository

    @Autowired
    lateinit var usuariosRepository: UsuariosRepository

    fun tareas() = tareasRepository.allInstances()

    fun tareaPorId(id: Int): Tarea = tareasRepository.searchById(id) ?: throw NotFoundException("No se encontró la tarea de id <$id>")

    fun buscar(tareaBusqueda: Tarea) = tareasRepository.searchByExample(tareaBusqueda)

    fun actualizar(id: Int, tareaActualizada: Tarea): Tarea {
        if (tareaActualizada.id !== null && tareaActualizada.id != id) {
            throw BusinessException("Id en URL distinto del id que viene en el body")
        }
        val tarea = tareaPorId(id)
        val nombreAsignatario = tareaActualizada.asignatario?.nombre
        // Solo llamamos a getAsignatario si el nombre contiene un valor distinto de null
        tareaActualizada.asignatario = nombreAsignatario?.let { usuariosRepository.getAsignatario(it) }
        // Pisamos los valores del repo con los nuevos datos
        tarea.actualizar(tareaActualizada)
        tarea.validar()
        tareasRepository.update(tarea)
        return tarea
    }

}