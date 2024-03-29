package org.uqbar.tareas.repository

import org.springframework.stereotype.Component
import org.uqbar.tareas.domain.Usuario

@Component
class UsuariosRepository {
    val usuarios = mutableListOf<Usuario>()

    companion object {
        private var ultimoId = ID_INICIAL_REPOSITORY
    }

    fun allInstances() = usuarios.sortedBy { it.nombre }

    fun create(usuario: Usuario): Usuario {
        usuario.id = ultimoId++
        usuarios.add(usuario)
        return usuario
    }

    fun getAsignatario(nombre: String) = usuarios.find { it.nombre.uppercase() == nombre.uppercase() }

    fun clear() {
        usuarios.clear()
    }

    fun clearInit() {
        clear()
        ultimoId = ID_INICIAL_REPOSITORY
    }
}