package org.uqbar.tareas.service

import org.springframework.stereotype.Service
import org.uqbar.tareas.domain.Usuario
import org.uqbar.tareas.errors.NotFoundException
import org.uqbar.tareas.repository.UsuariosRepository

@Service
class UsuariosService(
    val usuariosRepository: UsuariosRepository
) {

    fun allInstances() = usuariosRepository.allInstances()
    fun crear(usuario: Usuario) = usuariosRepository.create(usuario)
    fun eliminar(usuarioId: Int) =
        usuariosRepository.find(usuarioId)?.let { usuariosRepository.delete(it) }
            ?: throw NotFoundException("Usuario no encontrado")

}