package org.uqbar.tareas.service

import org.springframework.stereotype.Service
import org.uqbar.tareas.domain.Usuario
import org.uqbar.tareas.errors.NotFoundException
import org.uqbar.tareas.repository.TareasRepository
import org.uqbar.tareas.repository.UsuariosRepository

@Service
class UsuariosService(
    val usuariosRepository: UsuariosRepository,
    val tareasRepository: TareasRepository
) {

    fun allInstances() = usuariosRepository.allInstances()
    fun crear(usuario: Usuario) = usuariosRepository.create(usuario)
    /**
     * Elimina al usuario desasignando antes sus tareas, que sobreviven
     * con asignatario null. Se recorre el repo y no la lista del usuario
     * para cubrir también tareas que no estén en tareasAsignadas.
     */
    fun eliminar(usuarioId: Int): Usuario {
        val usuario = usuariosRepository.find(usuarioId)
            ?: throw NotFoundException("Usuario no encontrado")
        tareasRepository.allInstances()
            .filter { it.asignatario?.id == usuarioId }
            .forEach { it.desasignar() }
        usuariosRepository.delete(usuario)
        return usuario
    }

}