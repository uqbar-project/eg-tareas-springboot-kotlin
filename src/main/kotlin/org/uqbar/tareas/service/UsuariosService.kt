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
    fun eliminar(usuarioId: Int): Usuario {
        val usuario = usuariosRepository.find(usuarioId)
            ?: throw NotFoundException("Usuario no encontrado")
        // Desasignamos sus tareas: se recorre el repo (no la lista del usuario)
        // para cubrir también tareas que no estén en tareasAsignadas.
        tareasRepository.allInstances()
            .filter { it.asignatario?.id == usuarioId }
            .forEach { it.desasignar() }
        usuariosRepository.delete(usuario)
        return usuario
    }

}