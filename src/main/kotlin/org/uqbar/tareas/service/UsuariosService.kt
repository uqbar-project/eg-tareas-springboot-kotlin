package org.uqbar.tareas.service

import org.springframework.stereotype.Service
import org.uqbar.tareas.domain.Usuario
import org.uqbar.tareas.repository.UsuariosRepository

@Service
class UsuariosService(
    val usuariosRepository: UsuariosRepository
) {

    fun allInstances() = usuariosRepository.allInstances()
    fun crear(usuario: Usuario) = usuariosRepository.create(usuario)

}